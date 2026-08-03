import { api, buildQuery } from "@/api/client"
import type { Page, Pagination } from "@/schemas/common"
import type {
    Book,
    BookCreatePayload,
    BookFilters,
    BookUpdatePayload,
} from "@/schemas/books"

export function getBooks(
    filters: BookFilters,
    { page, size, sort }: Pagination,
): Promise<Page<Book>> {
    const query = buildQuery({ ...filters, page, size, sort })
    return api.get<Page<Book>>(`/books${query}`)
}

export function getBook(uuid: string): Promise<Book> {
    return api.get<Book>(`/books/${uuid}`)
}

export function createBook(payload: BookCreatePayload): Promise<Book> {
    return api.post<Book>("/books", payload)
}

export function updateBook(uuid: string, payload: BookUpdatePayload): Promise<Book> {
    return api.put<Book>(`/books/${uuid}`, payload)
}

export function deleteBook(uuid: string): Promise<void> {
    return api.delete<void>(`/books/${uuid}`)
}