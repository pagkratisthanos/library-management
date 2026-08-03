import { api, buildQuery } from "@/api/client"
import type { Copy, CopyFilters } from "@/schemas/copies"
import type { Page, Pagination } from "@/schemas/common"

export function getCopies(
    filters: CopyFilters,
    { page, size, sort }: Pagination,
): Promise<Page<Copy>> {
    const query = buildQuery({ ...filters, page, size, sort })
    return api.get<Page<Copy>>(`/copies${query}`)
}

export function getCopy(uuid: string): Promise<Copy> {
    return api.get<Copy>(`/copies/${uuid}`)
}

export function deleteCopy(uuid: string): Promise<void> {
    return api.delete<void>(`/copies/${uuid}`)
}