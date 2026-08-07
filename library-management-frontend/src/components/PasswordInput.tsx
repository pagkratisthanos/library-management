import { useState } from "react"
import { Eye, EyeOff } from "lucide-react"
import { Input } from "@/components/ui/input"
import { cn } from "@/lib/utils"

type PasswordInputProps = React.ComponentProps<typeof Input>

/** A password field with a button that toggles between hidden and visible. */
const PasswordInput = ({ className, ...props }: PasswordInputProps) => {
    const [visible, setVisible] = useState(false)

    return (
        <div className="relative">
            <Input
                type={visible ? "text" : "password"}
                className={cn("w-full min-w-0 pr-10", className)}
                {...props}
            />
            <button
                type="button"
                tabIndex={-1}
                aria-label={visible ? "Hide password" : "Show password"}
                onClick={() => setVisible((current) => !current)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
            >
                {visible ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
            </button>
        </div>
    )
}

export default PasswordInput