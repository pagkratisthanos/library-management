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
}

export type BookFilters = {
    title?: string
    isbn?: string
    language?: string
    description?: string
}