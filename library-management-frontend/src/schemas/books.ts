import { z } from "zod"
import type { Author } from "@/schemas/authors"

export type Book = {
    id: string
    title: string
    isbn: string
    publishedDate: string | null
    language: string | null
    dailyCost: number
    description: string | null
    authorReadOnlyDTOs: Author[]
    totalCopies: number
    availableCopies: number
}

export type BookFilters = {
    search?: string
    title?: string
    isbn?: string
    language?: string
    description?: string
}

export const bookSchema = z.object({
    title: z.string().min(1, { error: "Title is required" }),
    isbn: z.string().min(1, { error: "ISBN is required" }),
    publishedDate: z.string(),
    language: z.string(),
    dailyCost: z
        .string()
        .min(1, { error: "Daily cost is required" })
        .refine(
            (value) => !Number.isNaN(Number(value)) && Number(value) >= 0,
            { error: "Daily cost must be a positive number" },
        ),
    description: z.string(),
    authorUuids: z.array(z.string()),
})

/** What the form holds — dailyCost is a string, as every input is. */
export type BookFields = z.infer<typeof bookSchema>

export type BookCreatePayload = {
    title: string
    isbn: string
    publishedDate?: string
    language?: string
    dailyCost: number
    description?: string
    authorUuids?: string[]
}

/** The backend only allows these six fields to change. */
export type BookUpdatePayload = {
    title: string
    isbn: string
    publishedDate?: string
    language?: string
    dailyCost: number
    description?: string
}

