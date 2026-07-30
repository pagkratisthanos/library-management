import { Navigate, Route, Routes } from "react-router"
import LoginPage from "@/pages/LoginPage.tsx"
import AppLayout from "@/components/AppLayout.tsx"
import ProtectedRoute from "@/components/ProtectedRoute.tsx"
import DashboardPage from "@/pages/DashboardPage.tsx"
import BooksPage from "@/pages/BooksPage.tsx"
import AuthorsPage from "@/pages/AuthorsPage.tsx"
import CopiesPage from "@/pages/CopiesPage.tsx"
import MembersPage from "@/pages/MembersPage.tsx"
import RentalsPage from "@/pages/RentalsPage.tsx"
import UsersPage from "@/pages/UsersPage.tsx"

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