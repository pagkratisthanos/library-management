import { Link, NavLink, Outlet, useNavigate } from "react-router"
import type { LucideIcon } from "lucide-react"
import {
    BookCopy,
    BookOpen,
    ClipboardList,
    LayoutDashboard,
    LogOut,
    PenLine,
    UserCog,
    Users,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { useAuth } from "@/hooks/useAuth"

type NavItem = {
    to: string
    label: string
    icon: LucideIcon
    end?: boolean
}

const sharedItems: NavItem[] = [
    { to: ".", label: "Dashboard", icon: LayoutDashboard, end: true },
    { to: "books", label: "Books", icon: BookOpen },
    { to: "authors", label: "Authors", icon: PenLine },
    { to: "copies", label: "Copies", icon: BookCopy },
    { to: "members", label: "Members", icon: Users },
    { to: "rentals", label: "Rentals", icon: ClipboardList },
]

const adminOnlyItems: NavItem[] = [
    { to: "users", label: "Users", icon: UserCog },
]

const AppLayout = () => {
    const navigate = useNavigate()
    const { role, username, logoutUser } = useAuth()

    const items = role === "ADMIN" ? [...sharedItems, ...adminOnlyItems] : sharedItems

    const handleSignOut = () => {
        logoutUser()
        navigate("/login", { replace: true })
    }

    return (
        <div className="min-h-screen flex bg-muted/40">
            <aside className="w-64 shrink-0 border-r bg-background flex flex-col">
                <Link to="." className="h-16 flex items-center px-6 border-b hover:bg-accent">
                    <span className="font-semibold">Library Management</span>
                </Link>

                <nav className="flex-1 p-4 space-y-1">
                    {items.map(({ to, label, icon: Icon, end }) => (
                        <NavLink
                            key={to}
                            to={to}
                            end={end}
                            className={({ isActive }) =>
                                cn(
                                    "flex items-center gap-3 rounded-md px-3 py-2 text-sm transition-colors",
                                    isActive
                                        ? "bg-primary text-primary-foreground"
                                        : "text-muted-foreground hover:bg-accent hover:text-accent-foreground",
                                )
                            }
                        >
                            <Icon className="size-4" />
                            {label}
                        </NavLink>
                    ))}
                </nav>

                <div className="p-4 border-t space-y-3">
                    <div className="px-3">
                        <p className="text-sm font-medium">{username}</p>
                        <p className="text-xs text-muted-foreground">{role}</p>
                    </div>
                    <Button
                        variant="outline"
                        className="w-full justify-start gap-3"
                        onClick={handleSignOut}
                    >
                        <LogOut className="size-4" />
                        Sign out
                    </Button>
                </div>
            </aside>

            <main className="flex-1 p-8">
                <Outlet />
            </main>
        </div>
    )
}

export default AppLayout