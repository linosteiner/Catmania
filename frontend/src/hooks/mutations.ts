import {useMutation, useQueryClient} from '@tanstack/react-query'
import {authHeader} from "../auth.ts";

export interface CreateCatPayload {
    name: string
    birthDate?: string | null
    fkBreed?: number | null
    behaviourIds?: number[]
}

export interface UpdateCatPayload extends Partial<CreateCatPayload> {
}

export async function doJson<T>(url: string, method: string, body?: any): Promise<T> {
    const res = await fetch(url, {
        method,
        headers: {'Content-Type': 'application/json', Accept: 'application/json', ...authHeader()}, // <-- add
        body: body ? JSON.stringify(body) : undefined
    })
    if (!res.ok) {
        const text = await res.text().catch(() => '')
        throw new Error(text || `${res.status} ${res.statusText}`)
    }
    return res.status === 204 ? (undefined as any) : res.json()
}

export function useCreateCat() {
    const qc = useQueryClient()
    return useMutation({
        mutationFn: (payload: CreateCatPayload) => doJson('/api/cats', 'POST', payload),
        onSuccess: () => {
            qc.invalidateQueries({queryKey: ['cats']})
        }
    })
}

export function useUpdateCat() {
    const qc = useQueryClient()
    return useMutation({
        mutationFn: (args: { id: number; payload: UpdateCatPayload }) =>
            doJson(`/api/cats/${args.id}`, 'PATCH', args.payload),
        onSuccess: (_data, args) => {
            qc.invalidateQueries({queryKey: ['cats']})
            qc.invalidateQueries({queryKey: ['cat', args.id]})
        }
    })
}

export function useDeleteCat() {
    const qc = useQueryClient()
    return useMutation({
        mutationFn: (id: number) => doJson(`/api/cats/${id}`, 'DELETE'),
        onSuccess: () => {
            qc.invalidateQueries({queryKey: ['cats']})
        }
    })
}
