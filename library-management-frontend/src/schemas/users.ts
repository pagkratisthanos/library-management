import { z } from "zod"

export type User = {
    id: string
    username: string
    role: string
}

export type UserInsert = {
    username: string
    password: string
    roleId: number
}

export type UserFilters = {
    search?: string
    role?: string
}

export type RoleOption = {
    id: number
    name: string
}

// mirrors the @Pattern in UserInsertDTO
const PASSWORD_PATTERN = /(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])^.{8,}$/

const PASSWORD_MESSAGE =
    "At least 8 characters, with an uppercase, a lowercase, a digit and one of ! @ # $ % ^ & + ="

export const userSchema = z.object({
    username: z
        .string()
        .min(3, { error: "Username must be at least 3 characters" })
        .max(20, { error: "Username must be at most 20 characters" }),
    password: z.string().regex(PASSWORD_PATTERN, { error: PASSWORD_MESSAGE }),
    // the Select works with strings, the backend wants a number
    roleId: z.string().min(1, { error: "Role is required" }),
})

export type UserFields = z.infer<typeof userSchema>

export const roleChangeSchema = z.object({
    roleId: z.string().min(1, { error: "Role is required" }),
})

export type RoleChangeFields = z.infer<typeof roleChangeSchema>

export const passwordResetSchema = z.object({
    password: z.string().regex(PASSWORD_PATTERN, { error: PASSWORD_MESSAGE }),
})

export type PasswordResetFields = z.infer<typeof passwordResetSchema>

export const passwordChangeSchema = z
    .object({
        currentPassword: z.string().min(1, { error: "The current password is required" }),
        newPassword: z.string().regex(PASSWORD_PATTERN, { error: PASSWORD_MESSAGE }),
        confirmPassword: z.string().min(1, { error: "Please repeat the new password" }),
    })
    .refine((values) => values.newPassword === values.confirmPassword, {
        error: "The two passwords do not match",
        path: ["confirmPassword"],
    })
    .refine((values) => values.newPassword !== values.currentPassword, {
        error: "The new password must be different from the current one",
        path: ["newPassword"],
    })

export type PasswordChangeFields = z.infer<typeof passwordChangeSchema>