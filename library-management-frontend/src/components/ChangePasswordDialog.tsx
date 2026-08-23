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
import PasswordInput from "@/components/PasswordInput"
import { changeOwnPassword } from "@/api/users"
import { applyServerErrors } from "@/lib/formErrors"
import { type PasswordChangeFields, passwordChangeSchema } from "@/schemas/users"

const emptyValues: PasswordChangeFields = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
}

type ChangePasswordDialogProps = {
    open: boolean
    onOpenChange: (open: boolean) => void
    /** Called after a successful change, so the app can sign the user out. */
    onChanged: () => void
}

const ChangePasswordDialog = ({ open, onOpenChange, onChanged }: ChangePasswordDialogProps) => {
    const {
        register,
        handleSubmit,
        reset,
        setError,
        formState: { errors, isSubmitting },
    } = useForm<PasswordChangeFields>({
        resolver: zodResolver(passwordChangeSchema),
        defaultValues: emptyValues,
    })

    useEffect(() => {
        if (!open) return

        reset(emptyValues)
    }, [open, reset])

    const onSubmit = async (values: PasswordChangeFields) => {
        try {
            await changeOwnPassword(values.currentPassword, values.newPassword)
            toast.success("Your password was changed. Please sign in again.")
            onOpenChange(false)
            onChanged()
        } catch (err) {
            if (!applyServerErrors(err, setError)) {
                toast.error(err instanceof Error ? err.message : "Failed to change the password")
            }
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="overflow-hidden sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Change password</DialogTitle>
                    <DialogDescription>
                        You will be signed out afterwards.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} noValidate className="w-full min-w-0 space-y-4">
                    <Field className="min-w-0">
                        <FieldLabel htmlFor="currentPassword">Current password</FieldLabel>
                        <PasswordInput
                            id="currentPassword"
                            autoComplete="current-password"
                            {...register("currentPassword")}
                        />
                        {errors.currentPassword && (
                            <p className="text-sm text-destructive">
                                {errors.currentPassword.message}
                            </p>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="newPassword">New password</FieldLabel>
                        <PasswordInput
                            id="newPassword"
                            autoComplete="new-password"
                            {...register("newPassword")}
                        />
                        {errors.newPassword && (
                            <p className="text-sm text-destructive">{errors.newPassword.message}</p>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="confirmPassword">Repeat new password</FieldLabel>
                        <PasswordInput
                            id="confirmPassword"
                            autoComplete="new-password"
                            {...register("confirmPassword")}
                        />
                        {errors.confirmPassword && (
                            <p className="text-sm text-destructive">
                                {errors.confirmPassword.message}
                            </p>
                        )}
                    </Field>

                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                            Cancel
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Saving..." : "Change"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default ChangePasswordDialog