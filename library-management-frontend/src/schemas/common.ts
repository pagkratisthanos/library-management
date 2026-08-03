/** Mirrors Spring Data's Page<T> JSON shape. */
export type Page<T> = {
    content: T[]
    totalElements: number
    totalPages: number
    number: number
    size: number
    first: boolean
    last: boolean
    numberOfElements: number
    empty: boolean
}

export type Pagination = {
    page: number
    size: number
    /** Spring's format, e.g. "title,asc" */
    sort?: string
}