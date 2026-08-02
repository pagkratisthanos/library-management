export type Rental = {
    id: string
    memberUuid: string
    copyUuid: string
    /** ISO instant, e.g. 2026-07-10T09:15:30Z */
    rentalDate: string
    dueDate: string
    returnDate: string | null
    memberFirstname: string
    memberLastname: string
    bookTitle: string
}

export type RentalFilters = {
    search?: string
    memberUuid?: string
    copyUuid?: string
    /** The backend expects the string "true" or "false". */
    active?: "true" | "false"
}

export type RentalInsert = {
    dueDate: string
    memberUuid: string
    copyUuid: string
}