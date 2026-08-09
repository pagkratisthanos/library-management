import { useEffect, useState } from "react"
import type { LucideIcon } from "lucide-react"
import {
    BookCopy,
    BookOpen,
    ClipboardList,
    TriangleAlert,
    UserCog,
    Users,
} from "lucide-react"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent } from "@/components/ui/card"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import { getBooks } from "@/api/books"
import { getCopies } from "@/api/copies"
import { getMembers } from "@/api/members"
import { getOverdueRentals, getRentals } from "@/api/rentals"
import { getUsers } from "@/api/users"
import type { Rental } from "@/schemas/rentals"
import { useRole } from "@/hooks/useRole"

/** How many rentals to show in the "due soon" table. */
const DUE_SOON_COUNT = 5

/** A page of size 1 is enough when only totalElements matters. */
const COUNT_ONLY = { page: 0, size: 1 }

type Stat = {
    label: string
    value: number
    icon: LucideIcon
}

const StatCard = ({ label, value, icon: Icon }: Stat) => (
    <Card>
        <CardContent className="pt-6">
            <div className="flex items-center justify-between">
                <p className="text-sm text-muted-foreground">{label}</p>
                <Icon className="size-4 text-muted-foreground" />
            </div>
            <p className="mt-2 text-3xl font-semibold">{value}</p>
        </CardContent>
    </Card>
)

type Totals = {
    books: number
    copies: number
    members: number
    activeRentals: number
    overdue: number
    users: number
}

function formatDate(value: string | null): string {
    if (!value) return "—"
    return new Date(value).toLocaleDateString("en-GB")
}

const DashboardPage = () => {
    const role = useRole()
    const isAdmin = role === "ADMIN"

    const [totals, setTotals] = useState<Totals | null>(null)
    const [dueSoon, setDueSoon] = useState<Rental[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            setLoading(true)
            setError(null)

            try {
                const [books, copies, members, activeRentals, overdueRentals, users] =
                    await Promise.all([
                        getBooks({}, COUNT_ONLY),
                        getCopies({}, COUNT_ONLY),
                        getMembers({}, COUNT_ONLY),
                        getRentals(
                            { active: "true" },
                            { page: 0, size: DUE_SOON_COUNT, sort: "dueDate,asc" },
                        ),
                        getOverdueRentals(COUNT_ONLY),
                        // only an admin may list users
                        isAdmin ? getUsers({}, COUNT_ONLY) : Promise.resolve(null),
                    ])

                if (cancelled) return

                setTotals({
                    books: books.totalElements,
                    copies: copies.totalElements,
                    members: members.totalElements,
                    activeRentals: activeRentals.totalElements,
                    overdue: overdueRentals.totalElements,
                    users: users?.totalElements ?? 0,
                })

                setDueSoon(activeRentals.content)
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : "Failed to load the dashboard")
                }
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void load()

        return () => {
            cancelled = true
        }
    }, [isAdmin])

    const stats: Stat[] = totals
        ? [
            { label: "Books", value: totals.books, icon: BookOpen },
            { label: "Copies", value: totals.copies, icon: BookCopy },
            { label: "Members", value: totals.members, icon: Users },
            { label: "Active rentals", value: totals.activeRentals, icon: ClipboardList },
            { label: "Overdue", value: totals.overdue, icon: TriangleAlert },
            ...(isAdmin ? [{ label: "Users", value: totals.users, icon: UserCog }] : []),
        ]
        : []

    return (
        <div className="space-y-8">
            <div>
                <h1 className="text-2xl font-semibold">Dashboard</h1>
                <p className="text-sm text-muted-foreground">
                    Overview of the library at a glance.
                </p>
            </div>

            {error && <p className="text-sm text-destructive">{error}</p>}

            {loading && <p className="text-sm text-muted-foreground">Loading...</p>}

            {!loading && !error && (
                <>
                    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
                        {stats.map((stat) => (
                            <StatCard key={stat.label} {...stat} />
                        ))}
                    </div>

                    <div className="space-y-3">
                        <h2 className="text-lg font-medium">Due soon</h2>

                        <div className="rounded-lg border bg-background">
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Member</TableHead>
                                        <TableHead>Book</TableHead>
                                        <TableHead>Due date</TableHead>
                                        <TableHead>Status</TableHead>
                                    </TableRow>
                                </TableHeader>

                                <TableBody>
                                    {dueSoon.length === 0 && (
                                        <TableRow>
                                            <TableCell
                                                colSpan={4}
                                                className="h-24 text-center text-muted-foreground"
                                            >
                                                No active rentals.
                                            </TableCell>
                                        </TableRow>
                                    )}

                                    {dueSoon.map((rental) => {
                                        const isOverdue = new Date(rental.dueDate) < new Date()

                                        return (
                                            <TableRow key={rental.id}>
                                                <TableCell className="font-medium">
                                                    {rental.memberFirstname} {rental.memberLastname}
                                                </TableCell>
                                                <TableCell>{rental.bookTitle}</TableCell>
                                                <TableCell>{formatDate(rental.dueDate)}</TableCell>
                                                <TableCell>
                                                    {isOverdue ? (
                                                        <Badge variant="destructive">Overdue</Badge>
                                                    ) : (
                                                        <Badge>Active</Badge>
                                                    )}
                                                </TableCell>
                                            </TableRow>
                                        )
                                    })}
                                </TableBody>
                            </Table>
                        </div>
                    </div>
                </>
            )}
        </div>
    )
}

export default DashboardPage