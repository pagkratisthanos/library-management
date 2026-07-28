type PlaceholderPageProps = {
    title: string
    description?: string
}

const PlaceholderPage = ({ title, description }: PlaceholderPageProps) => {
    return (
        <div className="space-y-2">
            <h1 className="text-2xl font-semibold">{title}</h1>
            <p className="text-muted-foreground">
                {description ?? "This section is not implemented yet."}
            </p>
        </div>
    )
}

export default PlaceholderPage