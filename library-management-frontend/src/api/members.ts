import { api, buildQuery } from "@/api/client"
import type { Member, MemberFilters, MemberPayload } from "@/schemas/members"
import type { Page, Pagination } from "@/schemas/common"

export function getMembers(
    filters: MemberFilters,
    { page, size, sort }: Pagination,
): Promise<Page<Member>> {
    const query = buildQuery({ ...filters, page, size, sort })
    return api.get<Page<Member>>(`/members${query}`)
}

export function getMember(uuid: string): Promise<Member> {
    return api.get<Member>(`/members/${uuid}`)
}

export function createMember(payload: MemberPayload): Promise<Member> {
    return api.post<Member>("/members", payload)
}

export function updateMember(uuid: string, payload: MemberPayload): Promise<Member> {
    return api.put<Member>(`/members/${uuid}`, payload)
}

export function deleteMember(uuid: string): Promise<void> {
    return api.delete<void>(`/members/${uuid}`)
}