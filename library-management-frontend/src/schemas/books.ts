export type Author = {
    id: string
    firstname: string
    lastname: string
    birthDate: string
    birthPlace: string | null
    bio: string | null
}

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