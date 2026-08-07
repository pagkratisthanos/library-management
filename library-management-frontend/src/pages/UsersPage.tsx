import { useEffect, useState } from "react"
import { KeyRound, Plus, Search, Trash2, UserCog } from "lucide-react"
import { toast } from "sonner"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import TablePagination from "@/components/TablePagination"
import SortableTableHead from "@/components/SortableTableHead"
import UserFormDialog from "@/components/UserFormDialog"
import UserRoleDialog from "@/components/UserRoleDialog"
import ResetPasswordDialog from "@/components/ResetPasswordDialog"
import { deleteUser, getRoles, getUsers } from "@/api/users"
import type { RoleOption, User } from "@/schemas/users"
import type { Page } from "@/schemas/common"
import { useAuth } from "@/hooks/useAuth"
import { useDebounce } from "@/hooks/useDebounce"
import { useSort } from "@/hooks/useSort"

const PAGE_SIZE = 10

const UsersPage = () => {
    const { username: currentUsername } = useAuth()

    const [search, setSearch] = useState("")
    const [role, setRole] = useState("ALL")
    const [page, setPage] = useState(0)

    const [roles, setRoles] = useState<RoleOption[]>([])
    const [data, setData] = useState<Page<User> | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [reloadKey, setReloadKey] = useState(0)

    const [createOpen, setCreateOpen] = useState(false)
    const [roleDialogOpen, setRoleDialogOpen] = useState(false)
    const [resetDialogOpen, setResetDialogOpen] = useState(false)
    const [selectedUser, setSelectedUser] = useState<User | null>(null)

    const { sort, toggleSort, sortParam } = useSort({ field: "username", direction: "asc" })
    const debouncedSearch = useDebounce(search)

    const handleSearchChange = (value: string) => {
        setSearch(value)
        setPage(0)
    }

    const handleRoleChange = (value: string) => {
        setRole(value)
        setPage(0)
    }

    const handleSort = (field: string) => {
        toggleSort(field)
        setPage(0)
    }

    const reload = () => setReloadKey((key) => key + 1)

    const openRoleDialog = (user: User) => {
        setSelectedUser(user)
        setRoleDialogOpen(true)
    }

    const openResetDialog = (user: User) => {
        setSelectedUser(user)
        setResetDialogOpen(true)
    }

    // the list of roles never changes while the page is open
    useEffect(() => {
        getRoles()
            .then(setRoles)
            .catch(() => toast.error("Failed to load roles"))
    }, [])

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            setLoading(true)
            setError(null)

            try {
                const result = await getUsers(
                    {
                        search: debouncedSearch || undefined,
                        role: role === "ALL" ? undefined : role,
                    },
                    { page, size: PAGE_SIZE, sort: sortParam },
                )
                if (!cancelled) setData(result)
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : "Failed to load users")
                }
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void load()

        return () => {
            cancelled = true
        }
    }, [debouncedSearch, role, page, sortParam, reloadKey])

    const handleDelete = async (user: User) => {
        if (!window.confirm(`Delete user "${user.username}"?`)) return

        try {
            await deleteUser(user.id)
            toast.success(`"${user.username}" was deleted`)
            reload()
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to delete the user")
        }
    }

    const users = data?.content ?? []

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Users</h1>
                    <p className="text-sm text-muted-foreground">
                        Accounts that can sign in to the system.
                    </p>
                </div>
                <Button onClick={() => setCreateOpen(true)}>
                    <Plus className="size-4" />
                    New user
                </Button>
            </div>

            <div className="flex flex-wrap items-center gap-3">
                <div className="relative max-w-sm flex-1">
                    <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        placeholder="Search by username..."
                        className="pl-9"
                        value={search}
                        onChange={(event) => handleSearchChange(event.target.value)}
                    />
                </div>

                <Select value={role} onValueChange={handleRoleChange}>
                    <SelectTrigger className="w-44">
                        <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="ALL">All roles</SelectItem>
                        {roles.map((option) => (
                            <SelectItem key={option.id} value={option.name}>
                                {option.name}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <SortableTableHead field="username" sort={sort} onSort={handleSort}>
                                Username
                            </SortableTableHead>
                            <SortableTableHead field="role.name" sort={sort} onSort={handleSort}>
                                Role
                            </SortableTableHead>
                            <TableHead className="w-40 text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {loading && (
                            <TableRow>
                                <TableCell colSpan={3} className="h-24 text-center text-muted-foreground">
                                    Loading...
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && error && (
                            <TableRow>
                                <TableCell colSpan={3} className="h-24 text-center text-destructive">
                                    {error}
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && !error && users.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={3} className="h-24 text-center text-muted-foreground">
                                    No users found.
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading &&
                            !error &&
                            users.map((user) => {
                                const isCurrentUser = user.username === currentUsername

                                return (
                                    <TableRow key={user.id}>
                                        <TableCell className="font-medium">
                                            {user.username}
                                            {isCurrentUser && (
                                                <span className="ml-2 text-xs text-muted-foreground">
                                                    (you)
                                                </span>
                                            )}
                                        </TableCell>
                                        <TableCell>
                                            <Badge variant={user.role === "ADMIN" ? "default" : "secondary"}>
                                                {user.role}
                                            </Badge>
                                        </TableCell>
                                        <TableCell className="text-right space-x-2">
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Change role"
                                                title="Change role"
                                                disabled={isCurrentUser}
                                                onClick={() => openRoleDialog(user)}
                                            >
                                                <UserCog className="size-4" />
                                            </Button>
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Reset password"
                                                title="Reset password"
                                                disabled={isCurrentUser}
                                                onClick={() => openResetDialog(user)}
                                            >
                                                <KeyRound className="size-4" />
                                            </Button>
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Delete"
                                                title="Delete"
                                                disabled={isCurrentUser}
                                                onClick={() => handleDelete(user)}
                                            >
                                                <Trash2 className="size-4" />
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                )
                            })}
                    </TableBody>
                </Table>
            </div>

            {data && (
                <TablePagination
                    page={data.number}
                    totalPages={data.totalPages}
                    totalElements={data.totalElements}
                    onPageChange={setPage}
                />
            )}

            <UserFormDialog
                open={createOpen}
                roles={roles}
                onOpenChange={setCreateOpen}
                onSaved={reload}
            />

            <UserRoleDialog
                open={roleDialogOpen}
                user={selectedUser}
                roles={roles}
                onOpenChange={setRoleDialogOpen}
                onSaved={reload}
            />

            <ResetPasswordDialog
                open={resetDialogOpen}
                user={selectedUser}
                onOpenChange={setResetDialogOpen}
            />
        </div>
    )
}

export default UsersPage