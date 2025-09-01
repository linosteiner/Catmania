import { useQuery } from '@tanstack/react-query'

export interface BehaviourItem { name: string }
export interface CatItem {
    name: string
    birthDate?: string
    breedName?: string | null
    behaviours: BehaviourItem[]
}

// raw server shape
interface RawCat {
    name: string
    localDate?: string
    breedName?: string
    behaviours: string[]
}

function formatDate(dateString?: string): string | undefined {
    if (!dateString) return undefined
    const date = new Date(dateString)
    // format as DD.MM.YYYY
    return date.toLocaleDateString('de-CH')
}

export function useCats(filters: { breedPk?: number; behaviourPk?: number } = {}) {
    const params = new URLSearchParams()
    if (filters.breedPk) params.append('breedPk', String(filters.breedPk))
    if (filters.behaviourPk) params.append('behaviourPk', String(filters.behaviourPk))

    return useQuery({
        queryKey: ['cats', filters],
        queryFn: async (): Promise<CatItem[]> => {
            const res = await fetch(`/api?${params.toString()}`)
            if (!res.ok) {
                throw new Error(`HTTP ${res.status} fetching cats`)
            }

            const raw: RawCat[] = await res.json()

            return raw.map(c => ({
                name: c.name,
                birthDate: formatDate(c.localDate),
                breedName: c.breedName ?? null,
                behaviours: (c.behaviours ?? []).map(b => ({ name: b })),
            }))
        },
        staleTime: 30_000,
    })
}
