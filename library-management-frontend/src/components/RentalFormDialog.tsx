import { useEffect, useState } from "react"
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
import SearchableSelect, { type SelectOption } from "@/components/SearchableSelect"
import { createRental } from "@/api/rentals"
import { getMembers } from "@/api/members"
import { getCopies } from "@/api/copies"
import {
    MAX_RENTAL_DAYS,
    type RentalFields,
    rentalSchema,
    toDateInput,
    toEndOfDayInstant,
} from "@/schemas/rentals"

/** How many members and available copies to offer in the pickers. */
const OPTIONS_PAGE_SIZE = 500

const DEFAULT_LOAN_DAYS = 30

const addDays = (days: number) => {
    const date = new Date()
    date.setDate(date.getDate() + days)
    return toDateInput(date)
}

const emptyValues = (): RentalFields => ({
    memberUuid: "",
    copyUuid: "",
    dueDate: addDays(DEFAULT_LOAN_DAYS),
})

type RentalFormDialogProps = {
    open: boolean
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const RentalFormDialog = ({ open, onOpenChange, onSaved }: RentalFormDialogProps) => {
    const [memberOptions, setMemberOptions] = useState<SelectOption[]>([])
    const [copyOptions, setCopyOptions] = useState<SelectOption[]>([])
    const [loadingOptions, setLoadingOptions] = useState(false)

    const {
        register,
        handleSubmit,
        reset,
        watch,
        setValue,
        formState: { errors, isSubmitting },
    } = useForm<RentalFields>({
        resolver: zodResolver(rentalSchema),
        defaultValues: emptyValues(),
    })

    const memberUuid = watch("memberUuid")
    const copyUuid = watch("copyUuid")

    useEffect(() => {
        if (!open) return

        reset(emptyValues())

        let cancelled = false

        const loadOptions = async () => {
            setLoadingOptions(true)

            try {
                const [members, copies] = await Promise.all([
                    getMembers({}, { page: 0, size: OPTIONS_PAGE_SIZE, sort: "lastname,asc" }),
                    getCopies(
                        { available: true },
                        { page: 0, size: OPTIONS_PAGE_SIZE, sort: "book.title,asc" },
                    ),
                ])

                if (cancelled) return

                setMemberOptions(
                    members.content.map((member) => ({
                        value: member.id,
                        label: `${member.lastname} ${member.firstname} · ${member.email}`,
                    })),
                )

                setCopyOptions(
                    copies.content.map((copy) => ({
                        value: copy.id,
                        // several copies of one book look alike, so the id keeps them apart
                        label: `${copy.bookTitle} · ${copy.condition} · ${copy.id.slice(0, 8)}`,
                    })),
                )
            } catch (err) {
                if (!cancelled) {
                    toast.error(err instanceof Error ? err.message : "Failed to load the pickers")
                }
            } finally {
                if (!cancelled) setLoadingOptions(false)
            }
        }

        void loadOptions()

        return () => {
            cancelled = true
        }
    }, [open, reset])

    const onSubmit = async (values: RentalFields) => {
        try {
            await createRental({
                memberUuid: values.memberUuid,
                copyUuid: values.copyUuid,
                dueDate: toEndOfDayInstant(values.dueDate),
            })

            toast.success("The rental was created")
            onSaved()
            onOpenChange(false)
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to create the rental")
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="overflow-hidden sm:max-w-lg">
                <DialogHeader>
                    <DialogTitle>New rental</DialogTitle>
                    <DialogDescription>
                        Lend an available copy to a member.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} noValidate className="w-full min-w-0 space-y-4">
                    <Field className="min-w-0">
                        <FieldLabel htmlFor="memberUuid">Member</FieldLabel>
                        <SearchableSelect
                            id="memberUuid"
                            options={memberOptions}
                            value={memberUuid}
                            onChange={(value) =>
                                setValue("memberUuid", value, { shouldValidate: true })
                            }
                            placeholder={loadingOptions ? "Loading..." : "Select a member"}
                            searchPlaceholder="Search by name or email..."
                            emptyMessage="No member found."
                        />
                        {errors.memberUuid && (
                            <p className="text-sm text-destructive">{errors.memberUuid.message}</p>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="copyUuid">Copy</FieldLabel>
                        <SearchableSelect
                            id="copyUuid"
                            options={copyOptions}
                            value={copyUuid}
                            onChange={(value) =>
                                setValue("copyUuid", value, { shouldValidate: true })
                            }
                            placeholder={loadingOptions ? "Loading..." : "Select an available copy"}
                            searchPlaceholder="Search by book title..."
                            emptyMessage="No available copy found."
                        />
                        {errors.copyUuid && (
                            <p className="text-sm text-destructive">{errors.copyUuid.message}</p>
                        )}
                        {!loadingOptions && copyOptions.length === 0 && (
                            <p className="text-sm text-muted-foreground">
                                There are no available copies right now.
                            </p>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="dueDate">Due date</FieldLabel>
                        <Input
                            id="dueDate"
                            type="date"
                            min={addDays(1)}
                            max={addDays(MAX_RENTAL_DAYS)}
                            className="w-full min-w-0"
                            {...register("dueDate")}
                        />
                        {errors.dueDate ? (
                            <p className="text-sm text-destructive">{errors.dueDate.message}</p>
                        ) : (
                            <p className="text-sm text-muted-foreground">
                                Defaults to {DEFAULT_LOAN_DAYS} days from today.
                            </p>
                        )}
                    </Field>

                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                            Cancel
                        </Button>
                        <Button type="submit" disabled={isSubmitting || loadingOptions}>
                            {isSubmitting ? "Saving..." : "Save"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default RentalFormDialog