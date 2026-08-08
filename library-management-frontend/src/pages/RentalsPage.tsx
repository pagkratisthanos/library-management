import { useEffect, useState } from "react"
import { CalendarPlus, Plus, RotateCcw, Search } from "lucide-react"
import { toast } from "sonner"
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
import TablePagination from "@/components/TablePagination"
import SortableTableHead from "@/components/SortableTableHead"
import RentalFormDialog from "@/components/RentalFormDialog"
import ExtendRentalDialog from "@/components/ExtendRentalDialog"
import { getRentals, returnRental } from "@/api/rentals"
import type { Rental } from "@/schemas/rentals"
import type { Page } from "@/schemas/common"
import { useDebounce } from "@/hooks/useDebounce"
import { useSort } from "@/hooks/useSort"

const PAGE_SIZE = 10

type StatusFilter = "ALL" | "ACTIVE" | "RETURNED"

/** Formats an ISO instant as a local date. */
function formatDate(value: string | null): string {
    if (!value) return "—"
    return new Date(value).toLocaleDateString("en-GB")
}

const RentalsPage = () => {
    const [search, setSearch] = useState("")
    const [status, setStatus] = useState<StatusFilter>("ALL")
    const [page, setPage] = useState(0)

    const [data, setData] = useState<Page<Rental> | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [reloadKey, setReloadKey] = useState(0)

    const [createOpen, setCreateOpen] = useState(false)
    const [extendOpen, setExtendOpen] = useState(false)
    const [selectedRental, setSelectedRental] = useState<Rental | null>(null)

    const { sort, toggleSort, sortParam } = useSort({ field: "dueDate", direction: "asc" })
    const debouncedSearch = useDebounce(search)

    const handleSearchChange = (value: string) => {
        setSearch(value)
        setPage(0)
    }

    const handleStatusChange = (value: StatusFilter) => {
        setStatus(value)
        setPage(0)
    }

    const handleSort = (field: string) => {
        toggleSort(field)
        setPage(0)
    }

    const reload = () => setReloadKey((key) => key + 1)

    const openExtendDialog = (rental: Rental) => {
        setSelectedRental(rental)
        setExtendOpen(true)
    }

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            setLoading(true)
            setError(null)

            try {
                const result = await getRentals(
                    {
                        search: debouncedSearch || undefined,
                        active:
                            status === "ALL" ? undefined : status === "ACTIVE" ? "true" : "false",
                    },
                    { page, size: PAGE_SIZE, sort: sortParam },
                )
                if (!cancelled) setData(result)
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : "Failed to load rentals")
                }
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void load()

        return () => {
            cancelled = true
        }
    }, [debouncedSearch, status, page, sortParam, reloadKey])

    const handleReturn = async (rental: Rental) => {
        if (!window.confirm(`Return "${rental.bookTitle}"?`)) return

        try {
            await returnRental(rental.id)
            toast.success(`"${rental.bookTitle}" was returned`)
            reload()
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to return the rental")
        }
    }

    const rentals = data?.content ?? []
    const today = new Date()

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Rentals</h1>
                    <p className="text-sm text-muted-foreground">
                        Books currently borrowed and past loans.
                    </p>
                </div>
                <Button onClick={() => setCreateOpen(true)}>
                    <Plus className="size-4" />
                    New rental
                </Button>
            </div>

            <div className="flex flex-wrap items-center gap-3">
                <div className="relative max-w-sm flex-1">
                    <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        placeholder="Search by member or book..."
                        className="pl-9"
                        value={search}
                        onChange={(event) => handleSearchChange(event.target.value)}
                    />
                </div>

                <div className="flex gap-1">
                    {(["ALL", "ACTIVE", "RETURNED"] as StatusFilter[]).map((option) => (
                        <Button
                            key={option}
                            variant={status === option ? "default" : "outline"}
                            size="sm"
                            onClick={() => handleStatusChange(option)}
                        >
                            {option.charAt(0) + option.slice(1).toLowerCase()}
                        </Button>
                    ))}
                </div>
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <SortableTableHead field="member.lastname" sort={sort} onSort={handleSort}>
                                Member
                            </SortableTableHead>
                            <SortableTableHead field="copy.book.title" sort={sort} onSort={handleSort}>
                                Book
                            </SortableTableHead>
                            <SortableTableHead field="rentalDate" sort={sort} onSort={handleSort}>
                                Rented on
                            </SortableTableHead>
                            <SortableTableHead field="dueDate" sort={sort} onSort={handleSort}>
                                Due date
                            </SortableTableHead>
                            <TableHead>Status</TableHead>
                            <TableHead className="w-48 text-right">Actions</TableHead>
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

                        {!loading && !error && rentals.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} className="h-24 text-center text-muted-foreground">
                                    No rentals found.
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading &&
                            !error &&
                            rentals.map((rental) => {
                                const isActive = rental.returnDate === null
                                const isOverdue = isActive && new Date(rental.dueDate) < today

                                return (
                                    <TableRow key={rental.id}>
                                        <TableCell className="font-medium">
                                            {rental.memberFirstname} {rental.memberLastname}
                                        </TableCell>
                                        <TableCell>{rental.bookTitle}</TableCell>
                                        <TableCell>{formatDate(rental.rentalDate)}</TableCell>
                                        <TableCell>{formatDate(rental.dueDate)}</TableCell>
                                        <TableCell>
                                            {isOverdue ? (
                                                <Badge variant="destructive">Overdue</Badge>
                                            ) : isActive ? (
                                                <Badge>Active</Badge>
                                            ) : (
                                                <Badge variant="secondary">
                                                    Returned {formatDate(rental.returnDate)}
                                                </Badge>
                                            )}
                                        </TableCell>
                                        <TableCell className="text-right space-x-2">
                                            {isActive && (
                                                <>
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => openExtendDialog(rental)}
                                                    >
                                                        <CalendarPlus className="size-4" />
                                                        Extend
                                                    </Button>
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => handleReturn(rental)}
                                                    >
                                                        <RotateCcw className="size-4" />
                                                        Return
                                                    </Button>
                                                </>
                                            )}
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

            <RentalFormDialog
                open={createOpen}
                onOpenChange={setCreateOpen}
                onSaved={reload}
            />

            <ExtendRentalDialog
                open={extendOpen}
                rental={selectedRental}
                onOpenChange={setExtendOpen}
                onSaved={reload}
            />
        </div>
    )
}

export default RentalsPage