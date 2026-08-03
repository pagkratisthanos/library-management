import { z } from "zod"
import type { Book } from "@/schemas/books"

export type Author = {
    id: string
    firstname: string
    lastname: string
    birthDate: string
    birthPlace: string | null
    bio: string | null
    /** Null when the author appears nested inside a book. */
    bookReadOnlyDTOs: Book[] | null
}

export type AuthorFilters = {
    search?: string
    firstname?: string
    lastname?: string
    birthPlace?: string
}

export const authorSchema = z.object({
    firstname: z.string().min(1, { error: "First name is required" }),
    lastname: z.string().min(1, { error: "Last name is required" }),
    birthDate: z.string().min(1, { error: "Birth date is required" }),
    birthPlace: z.string(),
    bio: z.string(),
})

/** What the form holds — every field is a string, empty when unset. */
export type AuthorFields = z.infer<typeof authorSchema>

/** What we send to the API — optional fields are omitted when empty. */
export type AuthorPayload = {
    firstname: string
    lastname: string
    birthDate: string
    birthPlace?: string
    bio?: string
}