import {useQuery} from '@tanstack/react-query'

export interface Option {
    id: number;
    name: string
}

async function fetchJson<T>(url: string): Promise<T> {
    const res = await fetch(url, {headers: {Accept: 'application/json'}})
    if (!res.ok) throw new Error(`${res.status} ${res.statusText} @ ${url}`)
    return res.json()
}

async function fetchFirstAvailable<T>(paths: string[]): Promise<T> {
    let lastErr: any
    for (const p of paths) {
        try {
            return await fetchJson<T>(p)
        } catch (e) {
            lastErr = e
        }
    }
    throw lastErr ?? new Error('No endpoint available')
}

export function useBreeds(enabled = true) {
    return useQuery<Option[], Error>({
        queryKey: ['breeds'],
        enabled,
        queryFn: async () => {
            const list = await fetchFirstAvailable<any[]>(['/api/breeds', '/api/cats/catalog/breeds'])
            return (Array.isArray(list) ? list : []).map((b: any) => ({id: Number(b.id), name: String(b.name)}))
        }
    })
}

export function useBehaviours(enabled = true) {
    return useQuery<Option[], Error>({
        queryKey: ['behaviours'],
        enabled,
        queryFn: async () => {
            const list = await fetchFirstAvailable<any[]>(['/api/behaviours', '/api/cats/catalog/behaviours'])
            return (Array.isArray(list) ? list : []).map((b: any) => ({id: Number(b.id), name: String(b.name)}))
        }
    })
}
