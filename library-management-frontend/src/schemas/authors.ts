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