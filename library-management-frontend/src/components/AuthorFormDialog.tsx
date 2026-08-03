import { useEffect } from "react"
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
import { Textarea } from "@/components/ui/textarea"
import { createAuthor, updateAuthor } from "@/api/authors"
import { type Author, type AuthorFields, authorSchema } from "@/schemas/authors"

const emptyValues: AuthorFields = {
    firstname: "",
    lastname: "",
    birthDate: "",
    birthPlace: "",
    bio: "",
}

type AuthorFormDialogProps = {
    open: boolean
    /** The author being edited, or null when creating a new one. */
    author: Author | null
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const AuthorFormDialog = ({
                              open,
                              author,
                              onOpenChange,
                              onSaved,
                          }: AuthorFormDialogProps) => {
    const isEditing = author !== null

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<AuthorFields>({
        resolver: zodResolver(authorSchema),
        defaultValues: emptyValues,
    })

    // fill the form when it opens, either with the author or blank
    useEffect(() => {
        if (!open) return

        reset(
            author
                ? {
                    firstname: author.firstname,
                    lastname: author.lastname,
                    birthDate: author.birthDate,
                    birthPlace: author.birthPlace ?? "",
                    bio: author.bio ?? "",
                }
                : emptyValues,
        )
    }, [open, author, reset])

    const onSubmit = async (values: AuthorFields) => {
        const payload = {
            firstname: values.firstname,
            lastname: values.lastname,
            birthDate: values.birthDate,
            birthPlace: values.birthPlace || undefined,
            bio: values.bio || undefined,
        }

        const fullName = `${values.firstname} ${values.lastname}`

        try {
            if (author) {
                await updateAuthor(author.id, payload)
                toast.success(`"${fullName}" was updated`)
            } else {
                await createAuthor(payload)
                toast.success(`"${fullName}" was created`)
            }

            onSaved()
            onOpenChange(false)
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to save the author")
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-lg overflow-hidden">
                <DialogHeader>
                    <DialogTitle>{isEditing ? "Edit author" : "New author"}</DialogTitle>
                    <DialogDescription>
                        {isEditing
                            ? "Update the author's details."
                            : "Add a new author to the catalogue."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="w-full min-w-0 space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <Field className="min-w-0">
                            <FieldLabel htmlFor="firstname">First name</FieldLabel>
                            <Input id="firstname" className="w-full min-w-0" {...register("firstname")} />
                            {errors.firstname && (
                                <p className="text-sm text-destructive">
                                    {errors.firstname.message}
                                </p>
                            )}
                        </Field>

                        <Field className="min-w-0">
                            <FieldLabel htmlFor="lastname">Last name</FieldLabel>
                            <Input id="lastname" className="w-full min-w-0" {...register("lastname")} />
                            {errors.lastname && (
                                <p className="text-sm text-destructive">
                                    {errors.lastname.message}
                                </p>
                            )}
                        </Field>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <Field className="min-w-0">
                            <FieldLabel htmlFor="birthDate">Birth date</FieldLabel>
                            <Input id="birthDate" type="date" className="w-full min-w-0" {...register("birthDate")} />
                            {errors.birthDate && (
                                <p className="text-sm text-destructive">
                                    {errors.birthDate.message}
                                </p>
                            )}
                        </Field>

                        <Field className="min-w-0">
                            <FieldLabel htmlFor="birthPlace">Birth place</FieldLabel>
                            <Input id="birthPlace" className="w-full min-w-0" {...register("birthPlace")} />
                        </Field>
                    </div>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="bio">Biography</FieldLabel>
                        <Textarea
                            id="bio"
                            rows={4}
                            className="w-full min-w-0 resize-none break-all"
                            {...register("bio")}
                        />
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

export default AuthorFormDialog