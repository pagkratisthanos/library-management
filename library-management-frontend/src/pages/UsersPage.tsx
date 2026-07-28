import { useState } from "react"
import { Plus, Search, Trash2 } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"

type User = {
    id: string
    username: string
    role: "ADMIN" | "LIBRARIAN"
}

const mockUsers: User[] = [
    { id: "1", username: "admin", role: "ADMIN" },
    { id: "2", username: "maria.lib", role: "LIBRARIAN" },
    { id: "3", username: "nikos.lib", role: "LIBRARIAN" },
]

const UsersPage = () => {
    const [search, setSearch] = useState("")

    const users = mockUsers.filter((user) =>
        user.username.toLowerCase().includes(search.toLowerCase()),
    )

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Users</h1>
                    <p className="text-sm text-muted-foreground">
                        Accounts that can sign in to the system.
                    </p>
                </div>
                <Button>
                    <Plus className="size-4" />
                    New user
                </Button>
            </div>

            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    placeholder="Search by username..."
                    className="pl-9"
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                />
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Username</TableHead>
                            <TableHead>Role</TableHead>
                            <TableHead className="w-28 text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {users.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={3} className="h-24 text-center text-muted-foreground">
                                    No users found.
                                </TableCell>
                            </TableRow>
                        )}

                        {users.map((user) => (
                            <TableRow key={user.id}>
                                <TableCell className="font-medium">{user.username}</TableCell>
                                <TableCell>
                                    <Badge variant={user.role === "ADMIN" ? "default" : "secondary"}>
                                        {user.role}
                                    </Badge>
                                </TableCell>
                                <TableCell className="text-right">
                                    <Button variant="outline" size="icon" aria-label="Delete">
                                        <Trash2 className="size-4" />
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        </div>
    )
}

export default UsersPage