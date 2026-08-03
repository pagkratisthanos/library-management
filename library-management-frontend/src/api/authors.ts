import { api, buildQuery } from "@/api/client"
import type { Author, AuthorFilters, AuthorPayload } from "@/schemas/authors"
import type { Page, Pagination } from "@/schemas/common"

export function getAuthors(
    filters: AuthorFilters,
    { page, size }: Pagination,
): Promise<Page<Author>> {
    const query = buildQuery({ ...filters, page, size })
    return api.get<Page<Author>>(`/authors${query}`)
}

export function getAuthor(uuid: string): Promise<Author> {
    return api.get<Author>(`/authors/${uuid}`)
}

export function createAuthor(payload: AuthorPayload): Promise<Author> {
    return api.post<Author>("/authors", payload)
}

export function updateAuthor(uuid: string, payload: AuthorPayload): Promise<Author> {
    return api.put<Author>(`/authors/${uuid}`, payload)
}

export function deleteAuthor(uuid: string): Promise<void> {
    return api.delete<void>(`/authors/${uuid}`)
}