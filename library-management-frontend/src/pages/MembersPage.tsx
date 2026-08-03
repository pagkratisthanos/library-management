import { useEffect, useState } from "react"
import { Pencil, Plus, Search, Trash2 } from "lucide-react"
import { toast } from "sonner"
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
import TablePagination from "@/components/TablePagination"
import SortableTableHead from "@/components/SortableTableHead"
import { deleteMember, getMembers } from "@/api/members"
import type { Member } from "@/schemas/members"
import type { Page } from "@/schemas/common"
import { useDebounce } from "@/hooks/useDebounce"
import { useSort } from "@/hooks/useSort"

const PAGE_SIZE = 10

const MembersPage = () => {
    const [search, setSearch] = useState("")
    const [page, setPage] = useState(0)
    const [data, setData] = useState<Page<Member> | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [reloadKey, setReloadKey] = useState(0)

    const { sort, toggleSort, sortParam } = useSort({ field: "lastname", direction: "asc" })
    const debouncedSearch = useDebounce(search)

    const handleSearchChange = (value: string) => {
        setSearch(value)
        setPage(0)
    }

    const handleSort = (field: string) => {
        toggleSort(field)
        setPage(0)
    }

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            setLoading(true)
            setError(null)

            try {
                const result = await getMembers(
                    { search: debouncedSearch || undefined },
                    { page, size: PAGE_SIZE, sort: sortParam },
                )
                if (!cancelled) setData(result)
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : "Failed to load members")
                }
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void load()

        return () => {
            cancelled = true
        }
    }, [debouncedSearch, page, sortParam, reloadKey])

    const handleDelete = async (member: Member) => {
        const fullName = `${member.firstname} ${member.lastname}`
        if (!window.confirm(`Delete "${fullName}"?`)) return

        try {
            await deleteMember(member.id)
            toast.success(`"${fullName}" was deleted`)
            setReloadKey((key) => key + 1)
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to delete the member")
        }
    }

    const members = data?.content ?? []

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Members</h1>
                    <p className="text-sm text-muted-foreground">
                        People registered to borrow from the library.
                    </p>
                </div>
                <Button>
                    <Plus className="size-4" />
                    New member
                </Button>
            </div>

            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    placeholder="Search by name, email or phone..."
                    className="pl-9"
                    value={search}
                    onChange={(event) => handleSearchChange(event.target.value)}
                />
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <SortableTableHead field="lastname" sort={sort} onSort={handleSort}>
                                Name
                            </SortableTableHead>
                            <SortableTableHead field="email" sort={sort} onSort={handleSort}>
                                Email
                            </SortableTableHead>
                            <SortableTableHead field="phoneNumber" sort={sort} onSort={handleSort}>
                                Phone
                            </SortableTableHead>
                            <SortableTableHead field="address.city" sort={sort} onSort={handleSort}>
                                Address
                            </SortableTableHead>
                            <SortableTableHead
                                field="membershipDate"
                                sort={sort}
                                onSort={handleSort}
                            >
                                Member since
                            </SortableTableHead>
                            <TableHead className="w-28 text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {loading && (
                            <TableRow>
                                <TableCell colSpan={6} className="h-24 text-center text-muted-foreground">
                                    Loading...
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && error && (
                            <TableRow>
                                <TableCell colSpan={6} className="h-24 text-center text-destructive">
                                    {error}
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && !error && members.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} className="h-24 text-center text-muted-foreground">
                                    No members found.
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading &&
                            !error &&
                            members.map((member) => {
                                const address = member.addressReadOnlyDTO

                                return (
                                    <TableRow key={member.id}>
                                        <TableCell className="font-medium">
                                            {member.firstname} {member.lastname}
                                        </TableCell>
                                        <TableCell>{member.email}</TableCell>
                                        <TableCell>{member.phoneNumber}</TableCell>
                                        <TableCell>
                                            {address.street} {address.streetNumber}, {address.city}{" "}
                                            {address.postalCode}
                                        </TableCell>
                                        <TableCell>{member.membershipDate}</TableCell>
                                        <TableCell className="text-right space-x-2">
                                            <Button variant="outline" size="icon" aria-label="Edit">
                                                <Pencil className="size-4" />
                                            </Button>
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Delete"
                                                onClick={() => handleDelete(member)}
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
        </div>
    )
}

export default MembersPage