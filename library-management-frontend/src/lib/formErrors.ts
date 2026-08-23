import type { FieldValues, Path, UseFormSetError } from "react-hook-form"
import { ApiError } from "@/api/client"

/**
 * Puts server side validation messages on the matching form fields.
 * Returns true when the error was handled, so the caller can skip the toast.
 *
 * @param mapField translates a backend field name into a form field name,
 *                 for DTOs whose shape differs from the form's
 */
export function applyServerErrors<T extends FieldValues>(
    error: unknown,
    setError: UseFormSetError<T>,
    mapField: (field: string) => string = (field) => field,
): boolean {
    if (!(error instanceof ApiError) || !error.errors) return false

    Object.entries(error.errors).forEach(([field, message]) => {
        setError(mapField(field) as Path<T>, { type: "server", message })
    })

    return true
}

/** The member DTO nests the address; the form keeps it flat. */
export const stripAddressPrefix = (field: string) =>
    field.replace("addressInsertDTO.", "")