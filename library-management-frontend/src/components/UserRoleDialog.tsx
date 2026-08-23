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
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { updateUserRole } from "@/api/users"
import { applyServerErrors } from "@/lib/formErrors"
import { type RoleChangeFields, roleChangeSchema, type RoleOption, type User } from "@/schemas/users"

type UserRoleDialogProps = {
    open: boolean
    user: User | null
    roles: RoleOption[]
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const UserRoleDialog = ({ open, user, roles, onOpenChange, onSaved }: UserRoleDialogProps) => {
    const {
        handleSubmit,
        reset,
        watch,
        setValue,
        setError,
        formState: { errors, isSubmitting },
    } = useForm<RoleChangeFields>({
        resolver: zodResolver(roleChangeSchema),
        defaultValues: { roleId: "" },
    })

    const roleId = watch("roleId")

    useEffect(() => {
        if (!open) return

        // the user carries the role name, the select works with ids
        const currentRole = roles.find((option) => option.name === user?.role)
        reset({ roleId: currentRole ? String(currentRole.id) : "" })
    }, [open, user, roles, reset])

    const onSubmit = async (values: RoleChangeFields) => {
        if (!user) return

        try {
            const updated = await updateUserRole(user.id, Number(values.roleId))
            toast.success(`"${user.username}" is now ${updated.role}`)
            onSaved()
            onOpenChange(false)
        } catch (err) {
            if (!applyServerErrors(err, setError)) {
                toast.error(err instanceof Error ? err.message : "Failed to change the role")
            }
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="overflow-hidden sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Change role</DialogTitle>
                    <DialogDescription>
                        {user
                            ? `Choose the role for "${user.username}".`
                            : "Choose a role."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} noValidate className="w-full min-w-0 space-y-4">
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

export default UserRoleDialog