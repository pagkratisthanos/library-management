import { api, buildQuery } from "@/api/client"
import type { Page, Pagination } from "@/schemas/common"
import type { Book, BookFilters } from "@/schemas/books"

export function getBooks(
    filters: BookFilters,
    { page, size }: Pagination,
): Promise<Page<Book>> {
    const query = buildQuery({ ...filters, page, size })
    return api.get<Page<Book>>(`/books${query}`)
}

export function getBook(uuid: string): Promise<Book> {
    return api.get<Book>(`/books/${uuid}`)
}

export function deleteBook(uuid: string): Promise<void> {
    return api.delete<void>(`/books/${uuid}`)
}