import { Navigate, Route, Routes } from "react-router"
import LoginPage from "@/pages/LoginPage.tsx"

function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/login" element={<LoginPage />} />
        </Routes>
    )
}

export default App