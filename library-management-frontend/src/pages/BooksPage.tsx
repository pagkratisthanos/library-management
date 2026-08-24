import { useEffect, useState } from "react"
import { Pencil, Plus, Search, Trash2 } from "lucide-react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
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
import BookFormDialog from "@/components/BookFormDialog"
import { deleteBook, getBooks } from "@/api/books"
import type { Book } from "@/schemas/books"
import type { Page } from "@/schemas/common"
import { useDebounce } from "@/hooks/useDebounce"
import { useRole } from "@/hooks/useRole"
import { useSort } from "@/hooks/useSort"

const PAGE_SIZE = 10

const BooksPage = () => {
    const role = useRole()
    const canEdit = role === "ADMIN"

    const [search, setSearch] = useState("")
    const [page, setPage] = useState(0)
    const [data, setData] = useState<Page<Book> | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [reloadKey, setReloadKey] = useState(0)

    const [dialogOpen, setDialogOpen] = useState(false)
    const [editingBook, setEditingBook] = useState<Book | null>(null)

    const { sort, toggleSort, sortParam } = useSort({ field: "title", direction: "asc" })
    const debouncedSearch = useDebounce(search)

    const handleSearchChange = (value: string) => {
        setSearch(value)
        setPage(0)
    }

    // a different order always starts from the first page
    const handleSort = (field: string) => {
        toggleSort(field)
        setPage(0)
    }

    const openCreateDialog = () => {
        setEditingBook(null)
        setDialogOpen(true)
    }

    const openEditDialog = (book: Book) => {
        setEditingBook(book)
        setDialogOpen(true)
    }

    const reload = () => setReloadKey((key) => key + 1)

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            setLoading(true)
            setError(null)

            try {
                const result = await getBooks(
                    { search: debouncedSearch || undefined },
                    { page, size: PAGE_SIZE, sort: sortParam },
                )
                if (!cancelled) setData(result)
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : "Failed to load books")
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

    const handleDelete = async (book: Book) => {
        if (!window.confirm(`Delete "${book.title}"?`)) return

        try {
            await deleteBook(book.id)
            toast.success(`"${book.title}" was deleted`)
            reload()
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to delete the book")
        }
    }

    const books = data?.content ?? []
    const columnCount = canEdit ? 7 : 6

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Books</h1>
                    <p className="text-sm text-muted-foreground">
                        Browse and manage the library catalogue.
                    </p>
                </div>
                {canEdit && (
                    <Button onClick={openCreateDialog}>
                        <Plus className="size-4" />
                        New book
                    </Button>
                )}
            </div>

            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    placeholder="Search by title, ISBN or author..."
                    className="pl-9"
                    value={search}
                    onChange={(event) => handleSearchChange(event.target.value)}
                />
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <SortableTableHead field="title" sort={sort} onSort={handleSort}>
                                Title
                            </SortableTableHead>
                            <SortableTableHead field="isbn" sort={sort} onSort={handleSort}>
                                ISBN
                            </SortableTableHead>
                            <SortableTableHead field="language" sort={sort} onSort={handleSort}>
                                Language
                            </SortableTableHead>
                            <TableHead>Authors</TableHead>
                            <SortableTableHead
                                field="availableCopies"
                                sort={sort}
                                onSort={handleSort}
                                className="text-right"
                            >
                                Available
                            </SortableTableHead>
                            <SortableTableHead
                                field="dailyCost"
                                sort={sort}
                                onSort={handleSort}
                                className="text-right"
                            >
                                Daily cost
                            </SortableTableHead>
                            {canEdit && <TableHead className="w-28 text-right">Actions</TableHead>}
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {loading && (
                            <TableRow>
                                <TableCell colSpan={columnCount} className="h-24 text-center text-muted-foreground">
                                    Loading...
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && error && (
                            <TableRow>
                                <TableCell colSpan={columnCount} className="h-24 text-center text-destructive">
                                    {error}
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading && !error && books.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={columnCount} className="h-24 text-center text-muted-foreground">
                                    No books found.
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading &&
                            !error &&
                            books.map((book) => (
                                <TableRow key={book.id}>
                                    <TableCell className="font-medium">{book.title}</TableCell>
                                    <TableCell>{book.isbn}</TableCell>
                                    <TableCell>{book.language ?? "—"}</TableCell>
                                    <TableCell>
                                        {book.authorReadOnlyDTOs.length > 0
                                            ? book.authorReadOnlyDTOs
                                                .map((author) => `${author.firstname} ${author.lastname}`)
                                                .join(", ")
                                            : "—"}
                                    </TableCell>
                                    <TableCell className="text-right">
                                        {book.totalCopies === 0 ? (
                                            <span className="text-muted-foreground">—</span>
                                        ) : book.availableCopies === 0 ? (
                                            <Badge variant="secondary">
                                                0 of {book.totalCopies}
                                            </Badge>
                                        ) : (
                                            <Badge>
                                                {book.availableCopies} of {book.totalCopies}
                                            </Badge>
                                        )}
                                    </TableCell>
                                    <TableCell className="text-right">
                                        {book.dailyCost.toFixed(2)} €
                                    </TableCell>
                                    {canEdit && (
                                        <TableCell className="text-right space-x-2">
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Edit"
                                                onClick={() => openEditDialog(book)}
                                            >
                                                <Pencil className="size-4" />
                                            </Button>
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Delete"
                                                onClick={() => handleDelete(book)}
                                            >
                                                <Trash2 className="size-4" />
                                            </Button>
                                        </TableCell>
                                    )}
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

            <BookFormDialog
                open={dialogOpen}
                book={editingBook}
                onOpenChange={setDialogOpen}
                onSaved={reload}
            />
        </div>
    )
}

export default BooksPage