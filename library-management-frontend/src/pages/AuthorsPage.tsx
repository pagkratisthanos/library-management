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

type Author = {
    id: string
    firstname: string
    lastname: string
    birthDate: string
    birthPlace: string
    bookCount: number
}

const mockAuthors: Author[] = [
    {
        id: "1",
        firstname: "George",
        lastname: "Orwell",
        birthDate: "1903-06-25",
        birthPlace: "Motihari, India",
        bookCount: 2,
    },
    {
        id: "2",
        firstname: "Antoine",
        lastname: "de Saint-Exupéry",
        birthDate: "1900-06-29",
        birthPlace: "Lyon, France",
        bookCount: 1,
    },
    {
        id: "3",
        firstname: "Virginia",
        lastname: "Woolf",
        birthDate: "1882-01-25",
        birthPlace: "London, England",
        bookCount: 0,
    },
]

const AuthorsPage = () => {
    const role = useRole()
    const canEdit = role === "ADMIN"
    const [search, setSearch] = useState("")

    const authors = mockAuthors.filter((author) =>
        `${author.firstname} ${author.lastname}`
            .toLowerCase()
            .includes(search.toLowerCase()),
    )

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
                    <Button>
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
                    onChange={(event) => setSearch(event.target.value)}
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
                        {authors.length === 0 && (
                            <TableRow>
                                <TableCell
                                    colSpan={canEdit ? 5 : 4}
                                    className="h-24 text-center text-muted-foreground"
                                >
                                    No authors found.
                                </TableCell>
                            </TableRow>
                        )}

                        {authors.map((author) => (
                            <TableRow key={author.id}>
                                <TableCell className="font-medium">
                                    {author.firstname} {author.lastname}
                                </TableCell>
                                <TableCell>{author.birthDate}</TableCell>
                                <TableCell>{author.birthPlace}</TableCell>
                                <TableCell className="text-right">{author.bookCount}</TableCell>
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

export default AuthorsPage