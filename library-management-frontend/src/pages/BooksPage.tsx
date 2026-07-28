import { useState } from "react"
import { Pencil, Plus, Search, Trash2 } from "lucide-react"
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
import { useRole } from "@/hooks/useRole"

type Book = {
    id: string
    title: string
    isbn: string
    language: string
    dailyCost: number
    authors: string[]
}

const mockBooks: Book[] = [
    {
        id: "1",
        title: "Animal Farm",
        isbn: "978-0451526342",
        language: "English",
        dailyCost: 1.5,
        authors: ["George Orwell"],
    },
    {
        id: "2",
        title: "Nineteen Eighty-Four",
        isbn: "978-0452284234",
        language: "English",
        dailyCost: 2,
        authors: ["George Orwell"],
    },
    {
        id: "3",
        title: "The Little Prince",
        isbn: "978-0156012195",
        language: "French",
        dailyCost: 1.2,
        authors: ["Antoine de Saint-Exupéry"],
    },
]

const BooksPage = () => {
    const role = useRole()
    const canEdit = role === "ADMIN"
    const [search, setSearch] = useState("")

    const books = mockBooks.filter((book) =>
        book.title.toLowerCase().includes(search.toLowerCase()),
    )

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
                    <Button>
                        <Plus className="size-4" />
                        New book
                    </Button>
                )}
            </div>

            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    placeholder="Search by title..."
                    className="pl-9"
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                />
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Title</TableHead>
                            <TableHead>ISBN</TableHead>
                            <TableHead>Language</TableHead>
                            <TableHead>Authors</TableHead>
                            <TableHead className="text-right">Daily cost</TableHead>
                            {canEdit && <TableHead className="w-28 text-right">Actions</TableHead>}
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {books.length === 0 && (
                            <TableRow>
                                <TableCell
                                    colSpan={canEdit ? 6 : 5}
                                    className="h-24 text-center text-muted-foreground"
                                >
                                    No books found.
                                </TableCell>
                            </TableRow>
                        )}

                        {books.map((book) => (
                            <TableRow key={book.id}>
                                <TableCell className="font-medium">{book.title}</TableCell>
                                <TableCell>{book.isbn}</TableCell>
                                <TableCell>{book.language}</TableCell>
                                <TableCell>{book.authors.join(", ")}</TableCell>
                                <TableCell className="text-right">
                                    {book.dailyCost.toFixed(2)} €
                                </TableCell>
                                {canEdit && (
                                    <TableCell className="text-right space-x-2">
                                        <Button variant="outline" size="icon" aria-label="Edit">
                                            <Pencil className="size-4" />
                                        </Button>
                                        <Button variant="outline" size="icon" aria-label="Delete">
                                            <Trash2 className="size-4" />
                                        </Button>
                                    </TableCell>
                                )}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        </div>
    )
}

export default BooksPage