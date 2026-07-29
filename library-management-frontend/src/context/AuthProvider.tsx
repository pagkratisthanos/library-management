import { useState, type ReactNode } from "react"
import { jwtDecode } from "jwt-decode"
import { login as loginRequest } from "@/api/auth"
import { TOKEN_COOKIE } from "@/api/client"
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