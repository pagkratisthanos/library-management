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

type Member = {
    id: string
    firstname: string
    lastname: string
    email: string
    phoneNumber: string
    street: string
    streetNumber: string
    city: string
    postalCode: string
    membershipDate: string
}

const mockMembers: Member[] = [
    {
        id: "1",
        firstname: "Thanos",
        lastname: "Pagkratis",
        email: "thanos@example.com",
        phoneNumber: "6912345678",
        street: "Ermou",
        streetNumber: "15",
        city: "Athens",
        postalCode: "10563",
        membershipDate: "2026-01-15",
    },
    {
        id: "2",
        firstname: "Maria",
        lastname: "Ioannou",
        email: "maria@example.com",
        phoneNumber: "6987654321",
        street: "Tsimiski",
        streetNumber: "42",
        city: "Thessaloniki",
        postalCode: "54623",
        membershipDate: "2026-03-02",
    },
    {
        id: "3",
        firstname: "Nikos",
        lastname: "Dimitriou",
        email: "nikos@example.com",
        phoneNumber: "6900112233",
        street: "Korinthou",
        streetNumber: "8",
        city: "Patras",
        postalCode: "26221",
        membershipDate: "2026-05-20",
    },
]

const MembersPage = () => {
    const [search, setSearch] = useState("")

    const members = mockMembers.filter((member) =>
        `${member.firstname} ${member.lastname} ${member.email}`
            .toLowerCase()
            .includes(search.toLowerCase()),
    )

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Members</h1>
                    <p className="text-sm text-muted-foreground">
                        People registered to borrow from the library.
                    </p>
                </div>
                <Button>
                    <Plus className="size-4" />
                    New member
                </Button>
            </div>

            <div className="relative max-w-sm">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    placeholder="Search by name or email..."
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
                            <TableHead>Email</TableHead>
                            <TableHead>Phone</TableHead>
                            <TableHead>Address</TableHead>
                            <TableHead>Member since</TableHead>
                            <TableHead className="w-28 text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {members.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} className="h-24 text-center text-muted-foreground">
                                    No members found.
                                </TableCell>
                            </TableRow>
                        )}

                        {members.map((member) => (
                            <TableRow key={member.id}>
                                <TableCell className="font-medium">
                                    {member.firstname} {member.lastname}
                                </TableCell>
                                <TableCell>{member.email}</TableCell>
                                <TableCell>{member.phoneNumber}</TableCell>
                                <TableCell>
                                    {member.street} {member.streetNumber}, {member.city} {member.postalCode}
                                </TableCell>
                                <TableCell>{member.membershipDate}</TableCell>
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

export default MembersPage