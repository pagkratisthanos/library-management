import { Button } from "@/components/ui/button"

type TablePaginationProps = {
    page: number
    totalPages: number
    totalElements: number
    onPageChange: (page: number) => void
}

const TablePagination = ({
                             page,
                             totalPages,
                             totalElements,
                             onPageChange,
                         }: TablePaginationProps) => {
    if (totalElements === 0) return null

    return (
        <div className="flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
                {totalElements} result{totalElements === 1 ? "" : "s"}
            </p>

            <div className="flex items-center gap-2">
                <Button
                    variant="outline"
                    size="sm"
                    disabled={page === 0}
                    onClick={() => onPageChange(page - 1)}
                >
                    Previous
                </Button>

                <span className="text-sm text-muted-foreground">
                    Page {page + 1} of {totalPages}
                </span>

                <Button
                    variant="outline"
                    size="sm"
                    disabled={page >= totalPages - 1}
                    onClick={() => onPageChange(page + 1)}
                >
                    Next
                </Button>
            </div>
        </div>
    )
}

export default TablePagination