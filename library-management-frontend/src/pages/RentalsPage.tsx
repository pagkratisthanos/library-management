import { useState } from "react"
import { Plus, RotateCcw, Search } from "lucide-react"
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

type Rental = {
    id: string
    memberFirstname: string
    memberLastname: string
    bookTitle: string
    rentalDate: string
    dueDate: string
    returnDate: string | null
}

const mockRentals: Rental[] = [
    {
        id: "1",
        memberFirstname: "Thanos",
        memberLastname: "Pagkratis",
        bookTitle: "Animal Farm",
        rentalDate: "2026-07-10",
        dueDate: "2026-08-10",
        returnDate: null,
    },
    {
        id: "2",
        memberFirstname: "Maria",
        memberLastname: "Ioannou",
        bookTitle: "The Little Prince",
        rentalDate: "2026-06-01",
        dueDate: "2026-07-01",
        returnDate: null,
    },
    {
        id: "3",
        memberFirstname: "Nikos",
        memberLastname: "Dimitriou",
        bookTitle: "Nineteen Eighty-Four",
        rentalDate: "2026-05-05",
        dueDate: "2026-06-05",
        returnDate: "2026-05-28",
    },
]

type StatusFilter = "ALL" | "ACTIVE" | "RETURNED"

const RentalsPage = () => {
    const [search, setSearch] = useState("")
    const [status, setStatus] = useState<StatusFilter>("ALL")

    const today = new Date().toISOString().slice(0, 10)

    const rentals = mockRentals
        .filter((rental) =>
            `${rental.memberFirstname} ${rental.memberLastname} ${rental.bookTitle}`
                .toLowerCase()
                .includes(search.toLowerCase()),
        )
        .filter((rental) => {
            if (status === "ACTIVE") return rental.returnDate === null
            if (status === "RETURNED") return rental.returnDate !== null
            return true
        })

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Rentals</h1>
                    <p className="text-sm text-muted-foreground">
                        Books currently borrowed and past loans.
                    </p>
                </div>
                <Button>
                    <Plus className="size-4" />
                    New rental
                </Button>
            </div>

            <div className="flex items-center gap-3">
                <div className="relative max-w-sm flex-1">
                    <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        placeholder="Search by member or book..."
                        className="pl-9"
                        value={search}
                        onChange={(event) => setSearch(event.target.value)}
                    />
                </div>

                <div className="flex gap-1">
                    {(["ALL", "ACTIVE", "RETURNED"] as StatusFilter[]).map((option) => (
                        <Button
                            key={option}
                            variant={status === option ? "default" : "outline"}
                            size="sm"
                            onClick={() => setStatus(option)}
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
                            <TableHead>Member</TableHead>
                            <TableHead>Book</TableHead>
                            <TableHead>Rented on</TableHead>
                            <TableHead>Due date</TableHead>
                            <TableHead>Status</TableHead>
                            <TableHead className="w-32 text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {rentals.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} className="h-24 text-center text-muted-foreground">
                                    No rentals found.
                                </TableCell>
                            </TableRow>
                        )}

                        {rentals.map((rental) => {
                            const isActive = rental.returnDate === null
                            const isOverdue = isActive && rental.dueDate < today

                            return (
                                <TableRow key={rental.id}>
                                    <TableCell className="font-medium">
                                        {rental.memberFirstname} {rental.memberLastname}
                                    </TableCell>
                                    <TableCell>{rental.bookTitle}</TableCell>
                                    <TableCell>{rental.rentalDate}</TableCell>
                                    <TableCell>{rental.dueDate}</TableCell>
                                    <TableCell>
                                        {isOverdue ? (
                                            <Badge variant="destructive">Overdue</Badge>
                                        ) : isActive ? (
                                            <Badge>Active</Badge>
                                        ) : (
                                            <Badge variant="secondary">
                                                Returned {rental.returnDate}
                                            </Badge>
                                        )}
                                    </TableCell>
                                    <TableCell className="text-right">
                                        {isActive && (
                                            <Button variant="outline" size="sm">
                                                <RotateCcw className="size-4" />
                                                Return
                                            </Button>
                                        )}
                                    </TableCell>
                                </TableRow>
                            )
                        })}
                    </TableBody>
                </Table>
            </div>
        </div>
    )
}

export default RentalsPage