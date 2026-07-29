import { z } from "zod"

export const loginSchema = z.object({
    username: z.string().min(1, { error: "Username is required" }),
    password: z.string().min(1, { error: "Password is required" }),
})

export type LoginFields = z.infer<typeof loginSchema>

export type AuthenticationResponse = {
    token: string
}

export type Role = "ADMIN" | "LIBRARIAN"

export type JwtPayload = {
    sub: string
    role: Role
    iss: string
    iat: number
    exp: number
}