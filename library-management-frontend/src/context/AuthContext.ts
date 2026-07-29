import { createContext } from "react"
import type { LoginFields, Role } from "@/schemas/auth"

export type AuthContextProps = {
    isAuthenticated: boolean
    username: string | null
    role: Role | null
    loginUser: (fields: LoginFields) => Promise<Role>
    logoutUser: () => void
}

export const AuthContext = createContext<AuthContextProps | undefined>(undefined)