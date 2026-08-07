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
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import PasswordInput from "@/components/PasswordInput"
import { createUser } from "@/api/users"
import { type RoleOption, type UserFields, userSchema } from "@/schemas/users"

const emptyValues: UserFields = {
    username: "",
    password: "",
    roleId: "",
}

type UserFormDialogProps = {
    open: boolean
    /** Loaded once by the page, passed down so the dialog does not refetch. */
    roles: RoleOption[]
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const UserFormDialog = ({ open, roles, onOpenChange, onSaved }: UserFormDialogProps) => {
    const {
        register,
        handleSubmit,
        reset,
        watch,
        setValue,
        formState: { errors, isSubmitting },
    } = useForm<UserFields>({
        resolver: zodResolver(userSchema),
        defaultValues: emptyValues,
    })

    const roleId = watch("roleId")

    useEffect(() => {
        if (!open) return

        reset(emptyValues)
    }, [open, reset])

    const onSubmit = async (values: UserFields) => {
        try {
            await createUser({
                username: values.username,
                password: values.password,
                roleId: Number(values.roleId),
            })

            toast.success(`"${values.username}" was created`)
            onSaved()
            onOpenChange(false)
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to create the user")
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="overflow-hidden sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>New user</DialogTitle>
                    <DialogDescription>
                        Create an account that can sign in to the system.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="w-full min-w-0 space-y-4">
                    <Field className="min-w-0">
                        <FieldLabel htmlFor="username">Username</FieldLabel>
                        <Input
                            id="username"
                            autoComplete="off"
                            className="w-full min-w-0"
                            {...register("username")}
                        />
                        {errors.username && (
                            <p className="text-sm text-destructive">{errors.username.message}</p>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="password">Password</FieldLabel>
                        <PasswordInput
                            id="password"
                            autoComplete="new-password"
                            {...register("password")}
                        />
                        {errors.password && (
                            <p className="text-sm text-destructive">{errors.password.message}</p>
                        )}
                    </Field>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="roleId">Role</FieldLabel>
                        <Select
                            value={roleId}
                            onValueChange={(value) =>
                                setValue("roleId", value, { shouldValidate: true })
                            }
                        >
                            <SelectTrigger id="roleId" className="w-full min-w-0">
                                <SelectValue placeholder="Select a role" />
                            </SelectTrigger>
                            <SelectContent>
                                {roles.map((option) => (
                                    <SelectItem key={option.id} value={String(option.id)}>
                                        {option.name}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                        {errors.roleId && (
                            <p className="text-sm text-destructive">{errors.roleId.message}</p>
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

export default UserFormDialog