import { api, buildQuery } from "@/api/client"
import type { RoleOption, User, UserFilters, UserInsert } from "@/schemas/users"
import type { Page, Pagination } from "@/schemas/common"

export function getUsers(
    filters: UserFilters,
    { page, size, sort }: Pagination,
): Promise<Page<User>> {
    const query = buildQuery({ ...filters, page, size, sort })
    return api.get<Page<User>>(`/users${query}`)
}

export function getUser(uuid: string): Promise<User> {
    return api.get<User>(`/users/${uuid}`)
}

export function createUser(payload: UserInsert): Promise<User> {
    return api.post<User>("/users", payload)
}

export function deleteUser(uuid: string): Promise<void> {
    return api.delete<void>(`/users/${uuid}`)
}

export function getRoles(): Promise<RoleOption[]> {
    return api.get<RoleOption[]>("/roles")
}