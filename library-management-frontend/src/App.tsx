import { Navigate, Route, Routes } from "react-router"
import LoginPage from "@/pages/LoginPage.tsx"
import PlaceholderPage from "@/pages/PlaceholderPage.tsx"
import AppLayout from "@/components/AppLayout.tsx"
import BooksPage from "@/pages/BooksPage.tsx"

function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/login" element={<LoginPage />} />

            {/* Administrator */}
            <Route path="/admin" element={<AppLayout role="ADMIN" />}>
                <Route index element={<Navigate to="books" replace />} />
                <Route path="books" element={<BooksPage />} />
                <Route path="authors" element={<PlaceholderPage title="Authors" />} />
                <Route path="copies" element={<PlaceholderPage title="Copies" />} />
                <Route path="members" element={<PlaceholderPage title="Members" />} />
                <Route path="rentals" element={<PlaceholderPage title="Rentals" />} />
                <Route path="users" element={<PlaceholderPage title="Users" />} />
            </Route>

            {/* Librarian */}
            <Route path="/librarian" element={<AppLayout role="LIBRARIAN" />}>
                <Route index element={<PlaceholderPage title="Dashboard" />} />
                <Route path="books" element={<PlaceholderPage title="Books" />} />
                <Route path="authors" element={<PlaceholderPage title="Authors" />} />
                <Route path="copies" element={<PlaceholderPage title="Copies" />} />
                <Route path="members" element={<PlaceholderPage title="Members" />} />
                <Route path="rentals" element={<PlaceholderPage title="Rentals" />} />
            </Route>
        </Routes>
    )
}

export default App