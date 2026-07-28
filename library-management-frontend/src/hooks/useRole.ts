import { useOutletContext } from "react-router"
import type { AppLayoutContext } from "@/components/AppLayout"

export function useRole() {
    const { role } = useOutletContext<AppLayoutContext>()
    return role
}