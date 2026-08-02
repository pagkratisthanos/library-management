export type CopyCondition = "NEW" | "GOOD" | "FAIR" | "POOR" | "DAMAGED"

export const COPY_CONDITIONS: CopyCondition[] = [
    "NEW",
    "GOOD",
    "FAIR",
    "POOR",
    "DAMAGED",
]

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