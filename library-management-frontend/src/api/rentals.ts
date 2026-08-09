import { api, buildQuery } from "@/api/client"
import type { Rental, RentalFilters, RentalInsert } from "@/schemas/rentals"
import type { Page, Pagination } from "@/schemas/common"

export function getRentals(
    filters: RentalFilters,
    { page, size, sort }: Pagination,
): Promise<Page<Rental>> {
    const query = buildQuery({ ...filters, page, size, sort })
    return api.get<Page<Rental>>(`/rentals${query}`)
}

export function getRental(uuid: string): Promise<Rental> {
    return api.get<Rental>(`/rentals/${uuid}`)
}

export function createRental(payload: RentalInsert): Promise<Rental> {
    return api.post<Rental>("/rentals", payload)
}

export function returnRental(uuid: string): Promise<Rental> {
    return api.put<Rental>(`/rentals/${uuid}/return`)
}

export function extendRental(uuid: string, dueDate: string): Promise<Rental> {
    return api.put<Rental>(`/rentals/${uuid}/extend`, { dueDate })
}

export function getOverdueRentals({ page, size, sort }: Pagination): Promise<Page<Rental>> {
    const query = buildQuery({ page, size, sort })
    return api.get<Page<Rental>>(`/rentals/overdue${query}`)
}