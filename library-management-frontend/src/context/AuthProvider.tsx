import { useEffect, useState, type ReactNode } from "react"
import { jwtDecode } from "jwt-decode"
import { toast } from "sonner"
import { login as loginRequest } from "@/api/auth"
import { setUnauthorizedHandler, TOKEN_COOKIE } from "@/api/client"
import { AuthContext } from "@/context/AuthContext"
import type { JwtPayload, LoginFields, Role } from "@/schemas/auth"
import { deleteCookie, getCookie, setCookie } from "@/utils/cookies"

function decodeToken(token: string | null): JwtPayload | null {
    if (!token) return null

    try {
        const payload = jwtDecode<JwtPayload>(token)
        // treat an expired token as no token at all
        if (payload.exp * 1000 < Date.now()) return null
        return payload
    } catch {
        return null
    }
}

/**
 * The JWT cookie is the only source of truth for who is signed in. Identity is derived by decoding
 * it on each render rather than copied into state, so there is no second copy that can drift — a
 * cleared cookie means signed out, immediately.
 *
 * The role read from the token decides what the interface offers. It is not a security boundary:
 * the backend re-checks every request against the database, so a tampered token buys a different
 * menu and nothing else.
 */
export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [token, setToken] = useState<string | null>(
        () => getCookie(TOKEN_COOKIE) ?? null,
    )

    const payload = decodeToken(token)

    const loginUser = async (fields: LoginFields): Promise<Role> => {
        const response = await loginRequest(fields)

        setCookie(TOKEN_COOKIE, response.token, {
            expires: 0.5, // half a day = 12 hours, same as the backend
            sameSite: "Lax",
            path: "/",
        })
        setToken(response.token)

        const decoded = decodeToken(response.token)
        if (!decoded) throw new Error("Received an invalid token")

        return decoded.role
    }

    const logoutUser = () => {
        deleteCookie(TOKEN_COOKIE, { path: "/" })
        setToken(null)
    }

    // The api client cannot import React, so it calls back here when the server rejects a token we
    // were still holding. That is the only way the app learns a session died on the backend.
    useEffect(() => {
        setUnauthorizedHandler(() => {
            deleteCookie(TOKEN_COOKIE, { path: "/" })
            setToken(null)
            toast.error("Your session has expired. Please sign in again.")
        })

        return () => setUnauthorizedHandler(null)
    }, [])

    return (
        <AuthContext.Provider
            value={{
                isAuthenticated: payload !== null,
                username: payload?.sub ?? null,
                role: payload?.role ?? null,
                loginUser,
                logoutUser,
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}