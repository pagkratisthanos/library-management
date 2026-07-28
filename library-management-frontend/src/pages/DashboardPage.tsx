import type { LucideIcon } from "lucide-react"
import {
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
import { useRole } from "@/hooks/useRole"

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

const recentRentals = [
    {
        id: "1",
        member: "Thanos Pagkratis",
        book: "Animal Farm",
        dueDate: "2026-08-10",
        overdue: false,
    },
    {
        id: "2",
        member: "Maria Ioannou",
        book: "The Little Prince",
        dueDate: "2026-07-01",
        overdue: true,
    },
    {
        id: "3",
        member: "Nikos Dimitriou",
        book: "Nineteen Eighty-Four",
        dueDate: "2026-08-22",
        overdue: false,
    },
]

const DashboardPage = () => {
    const role = useRole()
    const isAdmin = role === "ADMIN"

    const stats: Stat[] = [
        { label: "Books", value: 128, icon: BookOpen },
        { label: "Members", value: 43, icon: Users },
        { label: "Active rentals", value: 17, icon: ClipboardList },
        { label: "Overdue", value: 3, icon: TriangleAlert },
    ]

    if (isAdmin) {
        stats.push({ label: "Users", value: 4, icon: UserCog })
    }

    return (
        <div className="space-y-8">
            <div>
                <h1 className="text-2xl font-semibold">Dashboard</h1>
                <p className="text-sm text-muted-foreground">
                    Overview of the library at a glance.
                </p>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4 xl:grid-cols-5">
                {stats.map((stat) => (
                    <StatCard key={stat.label} {...stat} />
                ))}
            </div>

            <div className="space-y-3">
                <h2 className="text-lg font-medium">Recent rentals</h2>

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
                            {recentRentals.map((rental) => (
                                <TableRow key={rental.id}>
                                    <TableCell className="font-medium">{rental.member}</TableCell>
                                    <TableCell>{rental.book}</TableCell>
                                    <TableCell>{rental.dueDate}</TableCell>
                                    <TableCell>
                                        {rental.overdue ? (
                                            <Badge variant="destructive">Overdue</Badge>
                                        ) : (
                                            <Badge>Active</Badge>
                                        )}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </div>
            </div>
        </div>
    )
}

export default DashboardPage