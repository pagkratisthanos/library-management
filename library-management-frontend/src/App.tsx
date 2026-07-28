import { Navigate, Route, Routes } from "react-router"
import LoginPage from "@/pages/LoginPage.tsx"
import PlaceholderPage from "@/pages/PlaceholderPage.tsx"
import AppLayout from "@/components/AppLayout.tsx"
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
            <Route path="/admin" element={<AppLayout role="ADMIN" />}>
                <Route index element={<PlaceholderPage title="Dashboard" />} />
                <Route path="books" element={<BooksPage />} />
                <Route path="authors" element={<AuthorsPage />} />
                <Route path="copies" element={<CopiesPage />} />
                <Route path="members" element={<MembersPage />} />
                <Route path="rentals" element={<RentalsPage />} />
                <Route path="users" element={<UsersPage />} />
            </Route>

            {/* Librarian */}
            <Route path="/librarian" element={<AppLayout role="LIBRARIAN" />}>
                <Route index element={<PlaceholderPage title="Dashboard" />} />
                <Route path="books" element={<BooksPage />} />
                <Route path="authors" element={<AuthorsPage />} />
                <Route path="copies" element={<CopiesPage />} />
                <Route path="members" element={<MembersPage />} />
                <Route path="rentals" element={<RentalsPage />} />
            </Route>
        </Routes>
    )
}

export default App