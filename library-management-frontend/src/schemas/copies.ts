import { z } from "zod"

export const COPY_CONDITIONS = ["NEW", "GOOD", "FAIR", "POOR", "DAMAGED"] as const

export type CopyCondition = (typeof COPY_CONDITIONS)[number]

export type Copy = {
    id: string
    bookUuid: string
    bookTitle: string
    available: boolean
    condition: CopyCondition
}

export type CopyFilters = {
    bookTitle?: string
    available?: boolean
    condition?: CopyCondition
}

export const copySchema = z.object({
    bookUuid: z.string().min(1, { error: "Book is required" }),
    available: z.boolean(),
    condition: z.enum(COPY_CONDITIONS),
})

export type CopyFields = z.infer<typeof copySchema>

export type CopyCreatePayload = {
    bookUuid: string
    available: boolean
    condition: CopyCondition
}

/** The backend does not allow moving a copy to another book. */
export type CopyUpdatePayload = {
    available: boolean
    condition: CopyCondition
}