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
import { Separator } from "@/components/ui/separator"
import { createMember, updateMember } from "@/api/members"
import { type Member, type MemberFields, memberSchema } from "@/schemas/members"

/** Today in the yyyy-MM-dd format the date input expects. */
const today = () => new Date().toISOString().slice(0, 10)

const emptyValues = (): MemberFields => ({
    firstname: "",
    lastname: "",
    email: "",
    phoneNumber: "",
    birthDate: "",
    membershipDate: today(),
    street: "",
    streetNumber: "",
    city: "",
    country: "Greece",
    postalCode: "",
})

type MemberFormDialogProps = {
    open: boolean
    /** The member being edited, or null when creating a new one. */
    member: Member | null
    onOpenChange: (open: boolean) => void
    onSaved: () => void
}

const MemberFormDialog = ({
                              open,
                              member,
                              onOpenChange,
                              onSaved,
                          }: MemberFormDialogProps) => {
    const isEditing = member !== null

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<MemberFields>({
        resolver: zodResolver(memberSchema),
        defaultValues: emptyValues(),
    })

    useEffect(() => {
        if (!open) return

        reset(
            member
                ? {
                    firstname: member.firstname,
                    lastname: member.lastname,
                    email: member.email,
                    phoneNumber: member.phoneNumber,
                    birthDate: member.birthDate ?? "",
                    membershipDate: member.membershipDate,
                    street: member.addressReadOnlyDTO.street,
                    streetNumber: member.addressReadOnlyDTO.streetNumber,
                    city: member.addressReadOnlyDTO.city,
                    country: member.addressReadOnlyDTO.country,
                    postalCode: member.addressReadOnlyDTO.postalCode,
                }
                : emptyValues(),
        )
    }, [open, member, reset])

    const onSubmit = async (values: MemberFields) => {
        // the form keeps the address flat, the API expects it nested
        const payload = {
            addressInsertDTO: {
                street: values.street,
                streetNumber: values.streetNumber,
                city: values.city,
                country: values.country,
                postalCode: values.postalCode,
            },
            firstname: values.firstname,
            lastname: values.lastname,
            phoneNumber: values.phoneNumber,
            email: values.email,
            birthDate: values.birthDate || undefined,
            membershipDate: values.membershipDate,
        }

        const fullName = `${values.firstname} ${values.lastname}`

        try {
            if (member) {
                await updateMember(member.id, payload)
                toast.success(`"${fullName}" was updated`)
            } else {
                await createMember(payload)
                toast.success(`"${fullName}" was created`)
            }

            onSaved()
            onOpenChange(false)
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to save the member")
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-lg">
                <DialogHeader>
                    <DialogTitle>{isEditing ? "Edit member" : "New member"}</DialogTitle>
                    <DialogDescription>
                        {isEditing
                            ? "Update the member's details."
                            : "Register a new library member."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="w-full min-w-0 space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <Field className="min-w-0">
                            <FieldLabel htmlFor="firstname">First name</FieldLabel>
                            <Input id="firstname" className="w-full min-w-0" {...register("firstname")} />
                            {errors.firstname && (
                                <p className="text-sm text-destructive">{errors.firstname.message}</p>
                            )}
                        </Field>

                        <Field className="min-w-0">
                            <FieldLabel htmlFor="lastname">Last name</FieldLabel>
                            <Input id="lastname" className="w-full min-w-0" {...register("lastname")} />
                            {errors.lastname && (
                                <p className="text-sm text-destructive">{errors.lastname.message}</p>
                            )}
                        </Field>
                    </div>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="email">Email</FieldLabel>
                        <Input id="email" type="email" className="w-full min-w-0" {...register("email")} />
                        {errors.email && (
                            <p className="text-sm text-destructive">{errors.email.message}</p>
                        )}
                    </Field>

                    <div className="grid grid-cols-2 gap-4">
                        <Field className="min-w-0">
                            <FieldLabel htmlFor="phoneNumber">Phone</FieldLabel>
                            <Input
                                id="phoneNumber"
                                placeholder="6912345678"
                                className="w-full min-w-0"
                                {...register("phoneNumber")}
                            />
                            {errors.phoneNumber && (
                                <p className="text-sm text-destructive">{errors.phoneNumber.message}</p>
                            )}
                        </Field>

                        <Field className="min-w-0">
                            <FieldLabel htmlFor="birthDate">Birth date</FieldLabel>
                            <Input
                                id="birthDate"
                                type="date"
                                className="w-full min-w-0"
                                {...register("birthDate")}
                            />
                        </Field>
                    </div>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="membershipDate">Member since</FieldLabel>
                        <Input
                            id="membershipDate"
                            type="date"
                            className="w-full min-w-0"
                            {...register("membershipDate")}
                        />
                        {errors.membershipDate && (
                            <p className="text-sm text-destructive">{errors.membershipDate.message}</p>
                        )}
                    </Field>

                    <Separator />

                    <p className="text-sm font-medium">Address</p>

                    <div className="grid grid-cols-[1fr_auto] gap-4">
                        <Field className="min-w-0">
                            <FieldLabel htmlFor="street">Street</FieldLabel>
                            <Input id="street" className="w-full min-w-0" {...register("street")} />
                            {errors.street && (
                                <p className="text-sm text-destructive">{errors.street.message}</p>
                            )}
                        </Field>

                        <Field className="w-24 min-w-0">
                            <FieldLabel htmlFor="streetNumber">Number</FieldLabel>
                            <Input id="streetNumber" className="w-full min-w-0" {...register("streetNumber")} />
                            {errors.streetNumber && (
                                <p className="text-sm text-destructive">{errors.streetNumber.message}</p>
                            )}
                        </Field>
                    </div>

                    <div className="grid grid-cols-3 gap-4">
                        <Field className="col-span-2 min-w-0">
                            <FieldLabel htmlFor="city">City</FieldLabel>
                            <Input id="city" className="w-full min-w-0" {...register("city")} />
                            {errors.city && (
                                <p className="text-sm text-destructive">{errors.city.message}</p>
                            )}
                        </Field>

                        <Field className="min-w-0">
                            <FieldLabel htmlFor="postalCode">Postal code</FieldLabel>
                            <Input id="postalCode" className="w-full min-w-0" {...register("postalCode")} />
                            {errors.postalCode && (
                                <p className="text-sm text-destructive">{errors.postalCode.message}</p>
                            )}
                        </Field>
                    </div>

                    <Field className="min-w-0">
                        <FieldLabel htmlFor="country">Country</FieldLabel>
                        <Input id="country" className="w-full min-w-0" {...register("country")} />
                        {errors.country && (
                            <p className="text-sm text-destructive">{errors.country.message}</p>
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

export default MemberFormDialog
