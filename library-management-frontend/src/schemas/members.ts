export type Address = {
    id: string
    street: string
    streetNumber: string
    city: string
    country: string
    postalCode: string
}

export type Member = {
    id: string
    addressReadOnlyDTO: Address
    firstname: string
    lastname: string
    email: string
    phoneNumber: string
    birthDate: string | null
    membershipDate: string
}

export type MemberFilters = {
    search?: string
    firstname?: string
    lastname?: string
    email?: string
    phoneNumber?: string
}