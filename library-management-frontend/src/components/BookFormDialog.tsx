import { useEffect, useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Search } from "lucide-react"
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
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { getAuthors } from "@/api/authors"
import { createBook, updateBook } from "@/api/books"
import { applyServerErrors } from "@/lib/formErrors"
import type { Author } from "@/schemas/authors"
import { type Book, type BookFields, bookSchema } from "@/schemas/books"

const emptyValues: BookFields = {
    title: "",
    isbn: "",
    publishedDate: "",
    language: "",
    dailyCost: "",
    description: "",
    authorUuids: [],
}

type BookFormDialogProps = {
    open: boolean
    /** The book being edited, or null when creating a new one. */
    book: Book | null
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const BookFormDialog = ({ open, book, onOpenChange, onSaved }: BookFormDialogProps) => {
    const isEditing = book !== null
    const [authors, setAuthors] = useState<Author[]>([])
    const [authorSearch, setAuthorSearch] = useState("")

    const {
        register,
        handleSubmit,
        reset,
        watch,
        setValue,
        setError,
        formState: { errors, isSubmitting },
    } = useForm<BookFields>({
        resolver: zodResolver(bookSchema),
        defaultValues: emptyValues,
    })

    const selectedAuthorUuids = watch("authorUuids")

    // the full author list is only needed when creating
    useEffect(() => {
        if (!open || isEditing) return

        setAuthorSearch("")

        getAuthors({}, { page: 0, size: 200, sort: "lastname,asc" })
            .then((result) => setAuthors(result.content))
            .catch(() => toast.error("Failed to load authors"))
    }, [open, isEditing])

    useEffect(() => {
        if (!open) return

        reset(
            book
                ? {
                    title: book.title,
                    isbn: book.isbn,
                    publishedDate: book.publishedDate ?? "",
                    language: book.language ?? "",
                    dailyCost: String(book.dailyCost),
                    description: book.description ?? "",
                    authorUuids: book.authorReadOnlyDTOs.map((author) => author.id),
                }
                : emptyValues,
        )
    }, [open, book, reset])

    const toggleAuthor = (uuid: string) => {
        const current = selectedAuthorUuids ?? []
        setValue(
            "authorUuids",
            current.includes(uuid)
                ? current.filter((id) => id !== uuid)
                : [...current, uuid],
        )
    }

    // filtered locally — the list is small enough not to need another request
    const visibleAuthors = authors.filter((author) =>
        `${author.firstname} ${author.lastname}`
            .toLowerCase()
            .includes(authorSearch.toLowerCase()),
    )

    const selectedCount = selectedAuthorUuids?.length ?? 0

    const onSubmit = async (values: BookFields) => {
        try {
            if (book) {
                await updateBook(book.id, {
                    title: values.title,
                    isbn: values.isbn,
                    publishedDate: values.publishedDate || undefined,
                    language: values.language || undefined,
                    dailyCost: Number(values.dailyCost),
                    description: values.description || undefined,
                })
                toast.success(`"${values.title}" was updated`)
            } else {
                await createBook({
                    title: values.title,
                    isbn: values.isbn,
                    publishedDate: values.publishedDate || undefined,
                    language: values.language || undefined,
                    dailyCost: Number(values.dailyCost),
                    description: values.description || undefined,
                    authorUuids: values.authorUuids.length ? values.authorUuids : undefined,
                })
                toast.success(`"${values.title}" was created`)
            }

            onSaved()
            onOpenChange(false)
        } catch (err) {
            if (!applyServerErrors(err, setError)) {
                toast.error(err instanceof Error ? err.message : "Failed to save the book")
            }
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-lg overflow-hidden">
                <DialogHeader>
                    <DialogTitle>{isEditing ? "Edit book" : "New book"}</DialogTitle>
                    <DialogDescription>
                        {isEditing
                            ? "Authors cannot be changed after creation."
                            : "Add a new book to the catalogue."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} noValidate className="w-full min-w-0 space-y-4">
                    <Field className="min-w-0">
                        <FieldLabel htmlFor="title">Title</FieldLabel>
                        <Input id="title" className="w-full min-w-0" {...register("title")} />
                        {errors.title && (
                            <p className="text-sm text-destructive">{errors.title.message}</p>
                        )}
                    </Field>

                    <div className="grid grid-cols-2 gap-4">
                        <Field className="min-w-0">
                            <FieldLabel htmlFor="isbn">ISBN</FieldLabel>
                            <Input id="isbn" className="w-full min-w-0" {...register("isbn")} />
                            {errors.isbn && (
                                <p className="text-sm text-destructive">{errors.isbn.message}</p>
                            )}
                        </Field>

                        <Field className="min-w-0">
                            <FieldLabel htmlFor="publishedDate">Published date</FieldLabel>
                            <Input
                                id="publishedDate"
                                type="date"
                                className="w-full min-w-0"
                                {...register("publishedDate")}
                            />
                            {errors.publishedDate && (
                                <p className="text-sm text-destructive">
                                    {errors.publishedDate.message}
                                </p>
                            )}
                        </Field>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <Field className="min-w-0">
                            <FieldLabel htmlFor="language">Language</FieldLabel>
                            <Input
                                id="language"
                                className="w-full min-w-0"
                                {...register("language")}
                            />
                            {errors.language && (
                                <p className="text-sm text-destructive">
                                    {errors.language.message}
                                </p>
                            )}
                        </Field>

                        <Field className="min-w-0">
                            <FieldLabel htmlFor="dailyCost">Daily cost (€)</FieldLabel>
                            <Input
                                id="dailyCost"
                                type="number"
                                step="0.01"
                                min="0"
                                className="w-full min-w-0"
                                {...register("dailyCost")}
                            />
                            {errors.dailyCost && (
                                <p className="text-sm text-destructive">
                                    {errors.dailyCost.message}
                                </p>
                            )}
                        </Field>
                    </div>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="description">Description</FieldLabel>
                        <Textarea
                            id="description"
                            rows={3}
                            className="w-full min-w-0 resize-none break-all"
                            {...register("description")}
                        />
                        {errors.description && (
                            <p className="text-sm text-destructive">
                                {errors.description.message}
                            </p>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel>
                            Authors
                            {!isEditing && selectedCount > 0 && (
                                <span className="ml-2 text-xs font-normal text-muted-foreground">
                                    {selectedCount} selected
                                </span>
                            )}
                        </FieldLabel>

                        {isEditing ? (
                            <p className="text-sm text-muted-foreground">
                                {book.authorReadOnlyDTOs.length > 0
                                    ? book.authorReadOnlyDTOs
                                        .map((author) => `${author.firstname} ${author.lastname}`)
                                        .join(", ")
                                    : "No authors linked."}
                            </p>
                        ) : (
                            <div className="space-y-2">
                                <div className="relative">
                                    <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                                    <Input
                                        placeholder="Filter authors..."
                                        className="w-full min-w-0 pl-9"
                                        value={authorSearch}
                                        onChange={(event) => setAuthorSearch(event.target.value)}
                                    />
                                </div>

                                <div className="max-h-40 space-y-2 overflow-y-auto rounded-md border p-3">
                                    {visibleAuthors.length === 0 && (
                                        <p className="text-sm text-muted-foreground">
                                            {authors.length === 0
                                                ? "No authors available yet."
                                                : "No authors match your filter."}
                                        </p>
                                    )}

                                    {visibleAuthors.map((author) => (
                                        <label
                                            key={author.id}
                                            className="flex items-center gap-2 text-sm"
                                        >
                                            <Checkbox
                                                checked={selectedAuthorUuids?.includes(author.id)}
                                                onCheckedChange={() => toggleAuthor(author.id)}
                                            />
                                            {author.firstname} {author.lastname}
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}

                        {errors.authorUuids && (
                            <p className="text-sm text-destructive">
                                {errors.authorUuids.message}
                            </p>
                        )}
                    </Field>

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

export default BookFormDialog