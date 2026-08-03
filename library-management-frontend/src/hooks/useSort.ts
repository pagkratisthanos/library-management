import { useState } from "react"

export type SortDirection = "asc" | "desc"

export type SortState = {
    field: string
    direction: SortDirection
} | null

export function useSort(initial: SortState = null) {
    const [sort, setSort] = useState<SortState>(initial)

    /** Cycles through ascending, descending and unsorted. */
    const toggleSort = (field: string) => {
        setSort((current) => {
            if (!current || current.field !== field) return { field, direction: "asc" }
            if (current.direction === "asc") return { field, direction: "desc" }
            return null
        })
    }

    const sortParam = sort ? `${sort.field},${sort.direction}` : undefined

    return { sort, toggleSort, sortParam }
}