import { useAuth } from "@/hooks/useAuth"
import type { Role } from "@/schemas/auth"

export function useRole(): Role {
    const { role } = useAuth()

    if (!role) {
        throw new Error("useRole must be used inside an authenticated route")
    }

    return role
}