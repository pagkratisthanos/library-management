export type User = {
    id: string
    username: string
    role: string
}

export type UserInsert = {
    username: string
    password: string
    roleId: number
}

export type UserFilters = {
    search?: string
    role?: string
}

export type RoleOption = {
    id: number
    name: string
}