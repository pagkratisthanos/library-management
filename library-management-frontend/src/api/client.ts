import { getCookie } from "@/utils/cookies"

const API_URL = import.meta.env.VITE_API_URL

export const TOKEN_COOKIE = "access_token"

/** Mirrors ErrorResponseDTO and ValidationErrorResponseDTO from the backend. */
type ApiErrorBody = {
    code: string
    description: string
    errors?: Record<string, string>
}

export class ApiError extends Error {
    readonly status: number
    readonly code: string
    /** Field name to message. Present only when validation failed. */
    readonly errors?: Record<string, string>

    constructor(
        status: number,
        code: string,
        description: string,
        errors?: Record<string, string>,
    ) {
        super(description)
        this.name = "ApiError"
        this.status = status
        this.code = code
        this.errors = errors
    }
}

type UnauthorizedHandler = () => void

let onUnauthorized: UnauthorizedHandler | null = null

/** Lets the auth layer react to an expired session without importing React here. */
export function setUnauthorizedHandler(handler: UnauthorizedHandler | null) {
    onUnauthorized = handler
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const token = getCookie(TOKEN_COOKIE)

    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...options.headers,
        },
    })

    if (!response.ok) {
        // a 401 while we were carrying a token means the session is no longer valid
        if (response.status === 401 && token) {
            onUnauthorized?.()
        }

        let code = "UNKNOWN"
        let description = "Something went wrong. Please try again."
        let errors: Record<string, string> | undefined

        try {
            const body = (await response.json()) as ApiErrorBody
            if (body?.description) {
                code = body.code
                description = body.description
                errors = body.errors
            }
        } catch {
            // the response had no JSON body — keep the defaults
        }

        throw new ApiError(response.status, code, description, errors)
    }

    // 204 No Content — nothing to parse
    if (response.status === 204) {
        return undefined as T
    }

    return (await response.json()) as T
}

export const api = {
    get: <T>(path: string) => request<T>(path),
    post: <T>(path: string, body: unknown) =>
        request<T>(path, { method: "POST", body: JSON.stringify(body) }),
    put: <T>(path: string, body?: unknown) =>
        request<T>(path, {
            method: "PUT",
            body: body === undefined ? undefined : JSON.stringify(body),
        }),
    delete: <T>(path: string) => request<T>(path, { method: "DELETE" }),
}

/** Builds a query string, skipping empty filters. */
export function buildQuery(
    params: Record<string, string | number | boolean | undefined | null>,
): string {
    const search = new URLSearchParams()

    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== "") {
            search.append(key, String(value))
        }
    })

    const query = search.toString()
    return query ? `?${query}` : ""
}