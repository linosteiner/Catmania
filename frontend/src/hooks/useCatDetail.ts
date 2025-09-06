import {useQuery} from '@tanstack/react-query'
import type {CatItem} from './useCats.ts'
import {authHeader} from "../auth.ts";

async function fetchJson<T>(url: string): Promise<T> {
    const res = await fetch(url, { headers: { Accept: 'application/json', ...authHeader() }})
    if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
    return res.json()
}

export function useCat(id?: number) {
    return useQuery<CatItem, Error>({
        queryKey: ['cat', id],
        enabled: !!id,
        queryFn: async () => fetchJson<CatItem>(`/api/cats/${id}`)
    })
}

export function useCatBehaviours(id?: number) {
    return useQuery<string[], Error>({
        queryKey: ['cat', id, 'behaviours'],
        enabled: !!id,
        queryFn: async () => {
            const list = await fetchJson<any[]>(`/api/cats/${id}/behaviours`)
            return (Array.isArray(list) ? list : []).map((b: any) => b.name ?? String(b))
        }
    })
}


export function useCatBehaviourIds(id?: number) {
    return useQuery<number[], Error>({
        queryKey: ['cat', id, 'behaviour-ids'],
        enabled: !!id,
        queryFn: async () => {
            const list = await fetchJson<any[]>(`/api/cats/${id}/behaviours`)
            return (Array.isArray(list) ? list : [])
                .map(b => Number(b?.id))
                .filter(n => Number.isFinite(n))
        }
    })
}

export interface FriendItem {
    id: number;
    name: string
}

export function useCatFriends(id?: number) {
    return useQuery<FriendItem[], Error>({
        queryKey: ['cat', id, 'friends'],
        enabled: !!id,
        queryFn: async () => {
            const list = await fetchJson<any[]>(`/api/cats/${id}/friends`)
            return (Array.isArray(list) ? list : []).map((c: any) => ({id: c.id, name: c.name}))
        }
    })
}
