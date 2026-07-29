import { api } from "@/api/client"
import type { AuthenticationResponse, LoginFields } from "@/schemas/auth"

export function login(fields: LoginFields): Promise<AuthenticationResponse> {
    return api.post<AuthenticationResponse>("/auth/authenticate", fields)
}