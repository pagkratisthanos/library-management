import { useEffect, useState } from "react"
import { Pencil, Plus, Search, Trash2 } from "lucide-react"
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
import CopyFormDialog from "@/components/CopyFormDialog"
import { deleteCopy, getCopies } from "@/api/copies"
import { COPY_CONDITIONS, type Copy, type CopyCondition } from "@/schemas/copies"
import type { Page } from "@/schemas/common"
import { useDebounce } from "@/hooks/useDebounce"
import { useSort } from "@/hooks/useSort"

const PAGE_SIZE = 10

type Availability = "ALL" | "AVAILABLE" | "UNAVAILABLE"
type ConditionFilter = "ALL" | CopyCondition

const CopiesPage = () => {
    const [search, setSearch] = useState("")
    const [availability, setAvailability] = useState<Availability>("ALL")
    const [condition, setCondition] = useState<ConditionFilter>("ALL")
    const [page, setPage] = useState(0)

    const [data, setData] = useState<Page<Copy> | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [reloadKey, setReloadKey] = useState(0)

    const [dialogOpen, setDialogOpen] = useState(false)
    const [editingCopy, setEditingCopy] = useState<Copy | null>(null)

    const { sort, toggleSort, sortParam } = useSort({ field: "book.title", direction: "asc" })
    const debouncedSearch = useDebounce(search)

    const handleSearchChange = (value: string) => {
        setSearch(value)
        setPage(0)
    }

    const handleAvailabilityChange = (value: Availability) => {
        setAvailability(value)
        setPage(0)
    }

    const handleConditionChange = (value: ConditionFilter) => {
        setCondition(value)
        setPage(0)
    }

    const handleSort = (field: string) => {
        toggleSort(field)
        setPage(0)
    }

    const openCreateDialog = () => {
        setEditingCopy(null)
        setDialogOpen(true)
    }

    const openEditDialog = (copy: Copy) => {
        setEditingCopy(copy)
        setDialogOpen(true)
    }

    const reload = () => setReloadKey((key) => key + 1)

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            setLoading(true)
            setError(null)

            try {
                const result = await getCopies(
                    {
                        bookTitle: debouncedSearch || undefined,
                        available:
                            availability === "ALL" ? undefined : availability === "AVAILABLE",
                        condition: condition === "ALL" ? undefined : condition,
                    },
                    { page, size: PAGE_SIZE, sort: sortParam },
                )
                if (!cancelled) setData(result)
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : "Failed to load copies")
                }
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void load()

        return () => {
            cancelled = true
        }
    }, [debouncedSearch, availability, condition, page, sortParam, reloadKey])

    const handleDelete = async (copy: Copy) => {
        if (!window.confirm(`Delete this copy of "${copy.bookTitle}"?`)) return

        try {
            await deleteCopy(copy.id)
            toast.success("Copy was deleted")
            reload()
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to delete the copy")
        }
    }

    const copies = data?.content ?? []

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Copies</h1>
                    <p className="text-sm text-muted-foreground">
                        Physical copies available for rental.
                    </p>
                </div>
                <Button onClick={openCreateDialog}>
                    <Plus className="size-4" />
                    New copy
                </Button>
            </div>

            <div className="flex flex-wrap items-center gap-3">
                <div className="relative max-w-sm flex-1">
                    <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        placeholder="Search by book title..."
                        className="pl-9"
                        value={search}
                        onChange={(event) => handleSearchChange(event.target.value)}
                    />
                </div>

                <div className="flex gap-1">
                    {(["ALL", "AVAILABLE", "UNAVAILABLE"] as Availability[]).map((option) => (
                        <Button
                            key={option}
                            variant={availability === option ? "default" : "outline"}
                            size="sm"
                            onClick={() => handleAvailabilityChange(option)}
                        >
                            {option.charAt(0) + option.slice(1).toLowerCase()}
                        </Button>
                    ))}
                </div>

                <Select
                    value={condition}
                    onValueChange={(value) => handleConditionChange(value as ConditionFilter)}
                >
                    <SelectTrigger className="w-44">
                        <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="ALL">All conditions</SelectItem>
                        {COPY_CONDITIONS.map((option) => (
                            <SelectItem key={option} value={option}>
                                {option}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <SortableTableHead field="book.title" sort={sort} onSort={handleSort}>
                                Book
                            </SortableTableHead>
                            <SortableTableHead field="available" sort={sort} onSort={handleSort}>
                                Availability
                            </SortableTableHead>
                            <SortableTableHead field="conditionRank" sort={sort} onSort={handleSort}>
                                Condition
                            </SortableTableHead>
                            <TableHead className="w-28 text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {loading && (
                            <TableRow>
                                <TableCell colSpan={4} className="h-24 text-center text-muted-foreground">
                                    Loading...
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && error && (
                            <TableRow>
                                <TableCell colSpan={4} className="h-24 text-center text-destructive">
                                    {error}
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && !error && copies.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={4} className="h-24 text-center text-muted-foreground">
                                    No copies found.
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading &&
                            !error &&
                            copies.map((copy) => (
                                <TableRow key={copy.id}>
                                    <TableCell className="font-medium">{copy.bookTitle}</TableCell>
                                    <TableCell>
                                        <Badge variant={copy.available ? "default" : "secondary"}>
                                            {copy.available ? "Available" : "Unavailable"}
                                        </Badge>
                                    </TableCell>
                                    <TableCell>
                                        <Badge variant="outline">{copy.condition}</Badge>
                                    </TableCell>
                                    <TableCell className="text-right space-x-2">
                                        <Button
                                            variant="outline"
                                            size="icon"
                                            aria-label="Edit"
                                            onClick={() => openEditDialog(copy)}
                                        >
                                            <Pencil className="size-4" />
                                        </Button>
                                        <Button
                                            variant="outline"
                                            size="icon"
                                            aria-label="Delete"
                                            onClick={() => handleDelete(copy)}
                                        >
                                            <Trash2 className="size-4" />
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
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

            <CopyFormDialog
                open={dialogOpen}
                copy={editingCopy}
                onOpenChange={setDialogOpen}
                onSaved={reload}
            />
        </div>
    )
}

export default CopiesPage