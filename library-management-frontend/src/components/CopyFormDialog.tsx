import { useEffect, useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Field, FieldLabel } from "@/components/ui/field"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { getBooks } from "@/api/books"
import { createCopy, updateCopy } from "@/api/copies"
import type { Book } from "@/schemas/books"
import {
    COPY_CONDITIONS,
    type Copy,
    type CopyCondition,
    type CopyFields,
    copySchema,
} from "@/schemas/copies"
import SearchableSelect from "@/components/SearchableSelect.tsx";

const emptyValues: CopyFields = {
    bookUuid: "",
    available: true,
    condition: "NEW",
}

type CopyFormDialogProps = {
    open: boolean
    /** The copy being edited, or null when creating a new one. */
    copy: Copy | null
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const CopyFormDialog = ({ open, copy, onOpenChange, onSaved }: CopyFormDialogProps) => {
    const isEditing = copy !== null
    const [books, setBooks] = useState<Book[]>([])

    const {
        handleSubmit,
        reset,
        watch,
        setValue,
        formState: { errors, isSubmitting },
    } = useForm<CopyFields>({
        resolver: zodResolver(copySchema),
        defaultValues: emptyValues,
    })

    const bookUuid = watch("bookUuid")
    const available = watch("available")
    const condition = watch("condition")

    // the book list is only needed when creating — a copy cannot move to another book
    useEffect(() => {
        if (!open || isEditing) return

        getBooks({}, { page: 0, size: 200, sort: "title,asc" })
            .then((result) => setBooks(result.content))
            .catch(() => toast.error("Failed to load books"))
    }, [open, isEditing])

    useEffect(() => {
        if (!open) return

        reset(
            copy
                ? {
                    bookUuid: copy.bookUuid,
                    available: copy.available,
                    condition: copy.condition,
                }
                : emptyValues,
        )
    }, [open, copy, reset])

    const onSubmit = async (values: CopyFields) => {
        try {
            if (copy) {
                await updateCopy(copy.id, {
                    available: values.available,
                    condition: values.condition,
                })
                toast.success("Copy was updated")
            } else {
                await createCopy({
                    bookUuid: values.bookUuid,
                    available: values.available,
                    condition: values.condition,
                })
                toast.success("Copy was created")
            }

            onSaved()
            onOpenChange(false)
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to save the copy")
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-md overflow-hidden">
                <DialogHeader>
                    <DialogTitle>{isEditing ? "Edit copy" : "New copy"}</DialogTitle>
                    <DialogDescription>
                        {isEditing
                            ? "A copy cannot be moved to another book."
                            : "Register a new physical copy of a book."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="w-full min-w-0 space-y-4">
                    <Field className="min-w-0">
                        <FieldLabel htmlFor="bookUuid">Book</FieldLabel>

                        {isEditing ? (
                            <p className="text-sm text-muted-foreground">{copy.bookTitle}</p>
                        ) : (
                            <>
                                <SearchableSelect
                                    id="bookUuid"
                                    value={bookUuid}
                                    onChange={(value) => setValue("bookUuid", value)}
                                    options={books.map((book) => ({
                                        value: book.id,
                                        label: `${book.title} · ${book.isbn}`,
                                    }))}
                                    placeholder="Select a book"
                                    searchPlaceholder="Search by title or ISBN..."
                                    emptyMessage="No books found."
                                />
                                {errors.bookUuid && (
                                    <p className="text-sm text-destructive">
                                        {errors.bookUuid.message}
                                    </p>
                                )}
                            </>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="condition">Condition</FieldLabel>
                        <Select
                            value={condition}
                            onValueChange={(value) =>
                                setValue("condition", value as CopyCondition)
                            }
                        >
                            <SelectTrigger id="condition" className="w-full min-w-0">
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                {COPY_CONDITIONS.map((option) => (
                                    <SelectItem key={option} value={option}>
                                        {option}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </Field>

                    <label className="flex items-center gap-2 text-sm">
                        <Checkbox
                            checked={available}
                            onCheckedChange={(checked) => setValue("available", checked === true)}
                        />
                        Available for rental
                    </label>

                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                            Cancel
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Saving..." : "Save"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default CopyFormDialog