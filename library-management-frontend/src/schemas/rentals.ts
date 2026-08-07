import { z } from "zod"

/** Mirrors MAX_RENTAL_DAYS in RentalServiceImpl. */
export const MAX_RENTAL_DAYS = 90

/** The date input works in yyyy-MM-dd, the backend expects an instant. */
export function toEndOfDayInstant(date: string): string {
    return new Date(`${date}T23:59:59`).toISOString()
}

/** Formats a Date as yyyy-MM-dd in the local time zone, not in UTC. */
export function toDateInput(date: Date): string {
    const offsetMs = date.getTimezoneOffset() * 60_000
    return new Date(date.getTime() - offsetMs).toISOString().slice(0, 10)
}

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

export const rentalSchema = z
    .object({
        memberUuid: z.string().min(1, { error: "Member is required" }),
        copyUuid: z.string().min(1, { error: "Copy is required" }),
        dueDate: z.string().min(1, { error: "Due date is required" }),
    })
    .refine(
        (values) => {
            if (!values.dueDate) return true
            const due = new Date(toEndOfDayInstant(values.dueDate)).getTime()
            const days = (due - Date.now()) / 86_400_000
            return days > 0 && days <= MAX_RENTAL_DAYS
        },
        {
            error: `Due date must be in the future and within ${MAX_RENTAL_DAYS} days`,
            path: ["dueDate"],
        },
    )

export type RentalFields = z.infer<typeof rentalSchema>