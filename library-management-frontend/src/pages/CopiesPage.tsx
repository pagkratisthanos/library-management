import { useState } from "react"
import { Pencil, Plus, Search, Trash2 } from "lucide-react"
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

type CopyCondition = "NEW" | "GOOD" | "FAIR" | "POOR" | "DAMAGED"

type Copy = {
    id: string
    bookTitle: string
    available: boolean
    condition: CopyCondition
}

const mockCopies: Copy[] = [
    { id: "1", bookTitle: "Animal Farm", available: true, condition: "NEW" },
    { id: "2", bookTitle: "Animal Farm", available: false, condition: "GOOD" },
    { id: "3", bookTitle: "Nineteen Eighty-Four", available: true, condition: "FAIR" },
    { id: "4", bookTitle: "The Little Prince", available: false, condition: "DAMAGED" },
]

const CopiesPage = () => {
    const [search, setSearch] = useState("")

    const copies = mockCopies.filter((copy) =>
        copy.bookTitle.toLowerCase().includes(search.toLowerCase()),
    )

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Copies</h1>
                    <p className="text-sm text-muted-foreground">
                        Physical copies available for rental.
                    </p>
                </div>
                <Button>
                    <Plus className="size-4" />
                    New copy
                </Button>
            </div>

            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    placeholder="Search by book title..."
                    className="pl-9"
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                />
            </div>

            <div className="rounded-lg border bg-background">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Book</TableHead>
                            <TableHead>Availability</TableHead>
                            <TableHead>Condition</TableHead>
                            <TableHead className="w-28 text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {copies.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={4} className="h-24 text-center text-muted-foreground">
                                    No copies found.
                                </TableCell>
                            </TableRow>
                        )}

                        {copies.map((copy) => (
                            <TableRow key={copy.id}>
                                <TableCell className="font-medium">{copy.bookTitle}</TableCell>
                                <TableCell>
                                    <Badge variant={copy.available ? "default" : "secondary"}>
                                        {copy.available ? "Available" : "Rented"}
                                    </Badge>
                                </TableCell>
                                <TableCell>
                                    <Badge variant="outline">{copy.condition}</Badge>
                                </TableCell>
                                <TableCell className="text-right space-x-2">
                                    <Button variant="outline" size="icon" aria-label="Edit">
                                        <Pencil className="size-4" />
                                    </Button>
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

export default CopiesPage