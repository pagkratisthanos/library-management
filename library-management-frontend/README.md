# Library Management — Frontend

The React client for the [Library Management API](../README.md). Two interfaces behind a
single login: administrators get everything, librarians get everything except user
management.

---

## Tech Stack

- **React** 19 + **TypeScript**
- **Vite** 8
- **Tailwind CSS** v4 (via `@tailwindcss/vite`) + **shadcn/ui** on Radix UI
- **react-router** 7
- **react-hook-form** + **zod** + `@hookform/resolvers`
- **js-cookie** + **jwt-decode** — token storage and reading
- **sonner** — toasts
- **lucide-react** — icons
- Native `fetch` — no axios

---

## Prerequisites

- Node.js 20+ and npm
- The backend running on `http://localhost:8080`

There is no mock data. Every screen reads from the API.

---

## Setup

```bash
cd library-management-frontend
cp .env.example .env
npm install
npm run dev
```

The only variable is the API address:

```
VITE_API_URL=http://localhost:8080/api/v1
```

The real `.env` is git ignored; `.env.example` is committed as the template.

---

## Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Dev server on `http://localhost:5173` with hot module replacement |
| `npm run build` | Type check with `tsc -b`, then build to `dist/` |
| `npm run preview` | Serve the built output locally |
| `npm run lint` | ESLint over the whole project |

`npm run build` fails on a type error, so it is the check to run before committing.

---

## Deployment

```bash
npm run build
```

The output in `dist/` is plain static files — any static host will do.

Three things to get right:

**Set `VITE_API_URL` before building.** Vite inlines environment variables at build time.
Changing the variable afterwards has no effect on files that are already built.

**Configure a history fallback.** The router uses the browser history API, so the host
must serve `index.html` for unknown paths. Without it, refreshing on `/admin/books`
returns 404.

**Allow the deployed origin on the backend.** `SecurityConfiguration.corsConfigurationSource()`
currently permits `http://localhost:5173` only.

---

## Project Structure

```
src/
├── api/          # One module per resource, plus the shared fetch client
├── components/   # Dialogs and shared widgets
│   └── ui/       # shadcn/ui primitives
├── context/      # AuthContext and AuthProvider
├── hooks/        # useAuth, useRole, useDebounce, useSort
├── lib/          # Form error helpers, class name utility
├── pages/        # One component per route
├── schemas/      # Types and zod validation schemas
└── utils/        # Cookie helpers
```

---

## How It Works

### Authentication

`POST /api/v1/auth/authenticate` returns a JWT. `AuthProvider` stores it in a cookie and
decodes it with `jwt-decode` to read the username from `sub` and the role from `role`.
A token whose `exp` has passed is treated as no token at all.

`api/client.ts` attaches `Authorization: Bearer <token>` to every request. When a request
answers **401 while a token was present**, it calls a handler that `AuthProvider`
registered: the cookie is cleared, a toast explains that the session expired, and
`ProtectedRoute` redirects to the login page on the next render. There is no explicit
navigation call — the redirect falls out of the state change.

The `&& token` condition matters: a wrong password at login also answers 401, but there
is no session to expire there.

### Routing and code splitting

`ProtectedRoute` is a layout route. It sends anonymous visitors to `/login` and sends a
signed in user to their own home if they ask for the other role's tree. `/admin/*` and
`/librarian/*` share the same child routes, except that `users` exists only under
`/admin`.

Each page is loaded with `React.lazy`, so it ships as its own chunk and is fetched the
first time it is visited. The `Suspense` boundary sits around the `Outlet` inside
`AppLayout` rather than around the whole router, so the sidebar stays on screen during a
page transition.

### Data fetching

Every list page follows one shape: `useState` for filters and paging, a `useEffect` that
fetches with a `cancelled` flag so a slow response cannot overwrite a newer one, and a
`reloadKey` counter that dialogs bump after saving.

Search is debounced by 400 ms through `useDebounce`. Sorting goes through `useSort`,
which cycles a column between ascending, descending and unsorted, and produces the
`field,direction` string Spring Data expects — including nested paths such as
`copy.book.title` and computed ones such as `availableCopies`.

### Forms

Dialogs use react-hook-form with `zodResolver`. The zod schemas mirror the bean
validation constraints on the backend so a mistake is caught before a request leaves the
browser. Forms carry `noValidate`, otherwise the browser's own HTML5 validation blocks
submission first and shows its message in the browser's language instead of ours.

When the server rejects a request anyway, `applyServerErrors` in `lib/formErrors.ts`
reads the `errors` map from the response and places each message on the matching field.
The member form passes a small translation function, because the backend nests the
address under `addressInsertDTO` while the form keeps it flat.

Errors that belong to no field — a taken username, a copy that is already lent — are
shown as a toast, with the dialog left open so nothing typed is lost.

### Role awareness

`useRole` reads the role from the token and the UI hides what the user cannot do. This is
convenience, not security: every rule is enforced again by Spring Security, and the
client only decides what to render.