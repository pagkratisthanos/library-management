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
import { resetUserPassword } from "@/api/users"
import { applyServerErrors } from "@/lib/formErrors"
import { type PasswordResetFields, passwordResetSchema, type User } from "@/schemas/users"

type ResetPasswordDialogProps = {
    open: boolean
    user: User | null
    onOpenChange: (open: boolean) => void
}

const ResetPasswordDialog = ({ open, user, onOpenChange }: ResetPasswordDialogProps) => {
    const {
        register,
        handleSubmit,
        reset,
        setError,
        formState: { errors, isSubmitting },
    } = useForm<PasswordResetFields>({
        resolver: zodResolver(passwordResetSchema),
        defaultValues: { password: "" },
    })

    useEffect(() => {
        if (!open) return

        reset({ password: "" })
    }, [open, reset])

    const onSubmit = async (values: PasswordResetFields) => {
        if (!user) return

        try {
            await resetUserPassword(user.id, values.password)
            toast.success(`The password of "${user.username}" was reset`)
            onOpenChange(false)
        } catch (err) {
            if (!applyServerErrors(err, setError)) {
                toast.error(err instanceof Error ? err.message : "Failed to reset the password")
            }
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="overflow-hidden sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Reset password</DialogTitle>
                    <DialogDescription>
                        {user
                            ? `Set a new password for "${user.username}" and hand it over to them.`
                            : "Set a new password."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} noValidate className="w-full min-w-0 space-y-4">
                    <Field className="min-w-0">
                        <FieldLabel htmlFor="password">New password</FieldLabel>
                        <PasswordInput
                            id="password"
                            autoComplete="new-password"
                            {...register("password")}
                        />
                        {errors.password && (
                            <p className="text-sm text-destructive">{errors.password.message}</p>
                        )}
                    </Field>

                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                            Cancel
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Saving..." : "Reset"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default ResetPasswordDialog