import { Navigate, Outlet, useLocation } from "react-router"
import { useAuth } from "@/hooks/useAuth"
import type { Role } from "@/schemas/auth"

const ProtectedRoute = ({ allowedRole }: { allowedRole: Role }) => {
    const { isAuthenticated, role } = useAuth()
    const location = useLocation()

    // not signed in — send to login and remember where they wanted to go
    if (!isAuthenticated) {
        return <Navigate to="/login" replace state={{ from: location.pathname }} />
    }

    // signed in, but this area belongs to the other role
    if (role !== allowedRole) {
        return <Navigate to={role === "ADMIN" ? "/admin" : "/librarian"} replace />
    }

    return <Outlet />
}

export default ProtectedRoute