import type { ReactNode } from "react"
import { ArrowDown, ArrowUp, ArrowUpDown } from "lucide-react"
import { TableHead } from "@/components/ui/table"
import { cn } from "@/lib/utils"
import type { SortState } from "@/hooks/useSort"

type SortableTableHeadProps = {
    /** The backend property name, e.g. "title" or "book.title". */
    field: string
    sort: SortState
    onSort: (field: string) => void
    className?: string
    children: ReactNode
}

const SortableTableHead = ({
                               field,
                               sort,
                               onSort,
                               className,
                               children,
                           }: SortableTableHeadProps) => {
    const isActive = sort?.field === field
    const Icon = !isActive ? ArrowUpDown : sort.direction === "asc" ? ArrowUp : ArrowDown

    return (
        <TableHead className={className}>
            <button
                type="button"
                onClick={() => onSort(field)}
                className={cn(
                    "inline-flex items-center gap-1 transition-colors hover:text-foreground",
                    isActive ? "font-medium text-foreground" : "",
                )}
            >
                {children}
                <Icon className="size-3.5" />
            </button>
        </TableHead>
    )
}

export default SortableTableHead