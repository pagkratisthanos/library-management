import { lazy } from "react"
import { Navigate, Route, Routes } from "react-router"
import LoginPage from "@/pages/LoginPage"
import AppLayout from "@/components/AppLayout"
import ProtectedRoute from "@/components/ProtectedRoute"

// each page becomes its own chunk, fetched the first time it is visited
const DashboardPage = lazy(() => import("@/pages/DashboardPage"))
const BooksPage = lazy(() => import("@/pages/BooksPage"))
const AuthorsPage = lazy(() => import("@/pages/AuthorsPage"))
const CopiesPage = lazy(() => import("@/pages/CopiesPage"))
const MembersPage = lazy(() => import("@/pages/MembersPage"))
const RentalsPage = lazy(() => import("@/pages/RentalsPage"))
const UsersPage = lazy(() => import("@/pages/UsersPage"))

function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/login" element={<LoginPage />} />

            {/* Administrator */}
            <Route element={<ProtectedRoute allowedRole="ADMIN" />}>
                <Route path="/admin" element={<AppLayout />}>
                    <Route index element={<DashboardPage />} />
                    <Route path="books" element={<BooksPage />} />
                    <Route path="authors" element={<AuthorsPage />} />
                    <Route path="copies" element={<CopiesPage />} />
                    <Route path="members" element={<MembersPage />} />
                    <Route path="rentals" element={<RentalsPage />} />
                    <Route path="users" element={<UsersPage />} />
                </Route>
            </Route>

            {/* Librarian */}
            <Route element={<ProtectedRoute allowedRole="LIBRARIAN" />}>
                <Route path="/librarian" element={<AppLayout />}>
                    <Route index element={<DashboardPage />} />
                    <Route path="books" element={<BooksPage />} />
                    <Route path="authors" element={<AuthorsPage />} />
                    <Route path="copies" element={<CopiesPage />} />
                    <Route path="members" element={<MembersPage />} />
                    <Route path="rentals" element={<RentalsPage />} />
                </Route>
            </Route>
        </Routes>
    )
}

export default App