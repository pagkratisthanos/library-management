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
import AuthorFormDialog from "@/components/AuthorFormDialog"
import { deleteAuthor, getAuthors } from "@/api/authors"
import type { Author } from "@/schemas/authors"
import type { Page } from "@/schemas/common"
import { useDebounce } from "@/hooks/useDebounce"
import { useRole } from "@/hooks/useRole"

const PAGE_SIZE = 10

const AuthorsPage = () => {
    const role = useRole()
    const canEdit = role === "ADMIN"

    const [search, setSearch] = useState("")
    const [page, setPage] = useState(0)
    const [data, setData] = useState<Page<Author> | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [reloadKey, setReloadKey] = useState(0)

    const [dialogOpen, setDialogOpen] = useState(false)
    const [editingAuthor, setEditingAuthor] = useState<Author | null>(null)

    const debouncedSearch = useDebounce(search)

    const handleSearchChange = (value: string) => {
        setSearch(value)
        setPage(0)
    }

    const openCreateDialog = () => {
        setEditingAuthor(null)
        setDialogOpen(true)
    }

    const openEditDialog = (author: Author) => {
        setEditingAuthor(author)
        setDialogOpen(true)
    }

    const reload = () => setReloadKey((key) => key + 1)

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            setLoading(true)
            setError(null)

            try {
                const result = await getAuthors(
                    { search: debouncedSearch || undefined },
                    { page, size: PAGE_SIZE },
                )
                if (!cancelled) setData(result)
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : "Failed to load authors")
                }
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void load()

        return () => {
            cancelled = true
        }
    }, [debouncedSearch, page, reloadKey])

    const handleDelete = async (author: Author) => {
        const fullName = `${author.firstname} ${author.lastname}`
        if (!window.confirm(`Delete "${fullName}"?`)) return

        try {
            await deleteAuthor(author.id)
            toast.success(`"${fullName}" was deleted`)
            reload()
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to delete the author")
        }
    }

    const authors = data?.content ?? []
    const columnCount = canEdit ? 5 : 4

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Authors</h1>
                    <p className="text-sm text-muted-foreground">
                        People who wrote the books in the catalogue.
                    </p>
                </div>
                {canEdit && (
                    <Button onClick={openCreateDialog}>
                        <Plus className="size-4" />
                        New author
                    </Button>
                )}
            </div>

            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    placeholder="Search by name..."
                    className="pl-9"
                    value={search}
                    onChange={(event) => handleSearchChange(event.target.value)}
                />
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Name</TableHead>
                            <TableHead>Birth date</TableHead>
                            <TableHead>Birth place</TableHead>
                            <TableHead className="text-right">Books</TableHead>
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

                        {!loading && !error && authors.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={columnCount} className="h-24 text-center text-muted-foreground">
                                    No authors found.
                                </TableCell>
                            </TableRow>
                        )}

                        {!loading &&
                            !error &&
                            authors.map((author) => (
                                <TableRow key={author.id}>
                                    <TableCell className="font-medium">
                                        {author.firstname} {author.lastname}
                                    </TableCell>
                                    <TableCell>{author.birthDate}</TableCell>
                                    <TableCell>{author.birthPlace ?? "—"}</TableCell>
                                    <TableCell className="text-right">
                                        {author.bookReadOnlyDTOs?.length ?? 0}
                                    </TableCell>
                                    {canEdit && (
                                        <TableCell className="text-right space-x-2">
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Edit"
                                                onClick={() => openEditDialog(author)}
                                            >
                                                <Pencil className="size-4" />
                                            </Button>
                                            <Button
                                                variant="outline"
                                                size="icon"
                                                aria-label="Delete"
                                                onClick={() => handleDelete(author)}
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

            <AuthorFormDialog
                open={dialogOpen}
                author={editingAuthor}
                onOpenChange={setDialogOpen}
                onSaved={reload}
            />
        </div>
    )
}

export default AuthorsPage