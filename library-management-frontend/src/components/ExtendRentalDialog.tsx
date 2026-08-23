import { useEffect, useMemo } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Field, FieldLabel } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { extendRental } from "@/api/rentals"
import { applyServerErrors } from "@/lib/formErrors"
import {
    buildExtendSchema,
    type ExtendRentalFields,
    MAX_RENTAL_DAYS,
    type Rental,
    toDateInput,
    toEndOfDayInstant,
} from "@/schemas/rentals"

const DEFAULT_EXTENSION_DAYS = 14

/** Adds days to an ISO instant and formats the result for a date input. */
const shift = (isoInstant: string, days: number) => {
    const date = new Date(isoInstant)
    date.setDate(date.getDate() + days)
    return toDateInput(date)
}

type ExtendRentalDialogProps = {
    open: boolean
    rental: Rental | null
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const ExtendRentalDialog = ({
                                open,
                                rental,
                                onOpenChange,
                                onSaved,
                            }: ExtendRentalDialogProps) => {
    // one day after the current due date, and at most 90 days after the rental started
    const minDate = rental ? shift(rental.dueDate, 1) : ""
    const maxDate = rental ? shift(rental.rentalDate, MAX_RENTAL_DAYS) : ""

    const schema = useMemo(() => buildExtendSchema(minDate, maxDate), [minDate, maxDate])

    const {
        register,
        handleSubmit,
        reset,
        setError,
        formState: { errors, isSubmitting },
    } = useForm<ExtendRentalFields>({
        resolver: zodResolver(schema),
        defaultValues: { dueDate: "" },
    })

    useEffect(() => {
        if (!open || !rental) return

        const suggested = shift(rental.dueDate, DEFAULT_EXTENSION_DAYS)
        reset({ dueDate: suggested > maxDate ? maxDate : suggested })
    }, [open, rental, maxDate, reset])

    const onSubmit = async (values: ExtendRentalFields) => {
        if (!rental) return

        try {
            await extendRental(rental.id, toEndOfDayInstant(values.dueDate))
            toast.success(`"${rental.bookTitle}" was extended`)
            onSaved()
            onOpenChange(false)
        } catch (err) {
            if (!applyServerErrors(err, setError)) {
                toast.error(err instanceof Error ? err.message : "Failed to extend the rental")
            }
        }
    }

    const currentDueDate = rental
        ? new Date(rental.dueDate).toLocaleDateString("en-GB")
        : ""

    const maxDateLabel = maxDate
        ? new Date(`${maxDate}T00:00:00`).toLocaleDateString("en-GB")
        : ""

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="overflow-hidden sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Extend rental</DialogTitle>
                    <DialogDescription>
                        {rental
                            ? `"${rental.bookTitle}" is currently due on ${currentDueDate}.`
                            : "Choose a new due date."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} noValidate className="w-full min-w-0 space-y-4">
                    <Field className="min-w-0">
                        <FieldLabel htmlFor="dueDate">New due date</FieldLabel>
                        <Input
                            id="dueDate"
                            type="date"
                            min={minDate}
                            max={maxDate}
                            className="w-full min-w-0"
                            {...register("dueDate")}
                        />
                        {errors.dueDate ? (
                            <p className="text-sm text-destructive">{errors.dueDate.message}</p>
                        ) : (
                            <p className="text-sm text-muted-foreground">
                                At the latest {maxDateLabel}, {MAX_RENTAL_DAYS} days after the loan started.
                            </p>
                        )}
                    </Field>

                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                            Cancel
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Saving..." : "Extend"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default ExtendRentalDialog