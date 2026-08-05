import { z } from "zod"

// mirrors the patterns in MemberInsertDTO on the backend
const EMAIL_PATTERN = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const PHONE_PATTERN = /^(\+30|0030)?[0-9]{10}$/

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

// the form keeps the address flat, it is re-nested on submit
export const memberSchema = z.object({
    firstname: z.string().min(2, "First name must be at least 2 characters"),
    lastname: z.string().min(2, "Last name must be at least 2 characters"),
    email: z.string().regex(EMAIL_PATTERN, "Invalid email format"),
    phoneNumber: z.string().regex(PHONE_PATTERN, "Invalid phone number"),
    birthDate: z.string(),
    membershipDate: z.string().min(1, "Membership date is required"),
    street: z.string().min(1, "Street is required"),
    streetNumber: z.string().min(1, "Street number is required"),
    city: z.string().min(1, "City is required"),
    country: z.string().min(1, "Country is required"),
    postalCode: z.string().min(1, "Postal code is required"),
})

export type MemberFields = z.infer<typeof memberSchema>

export type MemberPayload = {
    addressInsertDTO: {
        street: string
        streetNumber: string
        city: string
        country: string
        postalCode: string
    }
    firstname: string
    lastname: string
    phoneNumber: string
    email: string
    birthDate?: string
    membershipDate: string
}