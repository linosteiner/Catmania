import {useQuery} from '@tanstack/react-query'
import {authHeader} from "../auth"

export interface BehaviourItem {
    name: string
}

export interface CatItem {
    id: number
    name: string
    birthDate?: string | null
    breedName?: string | null
    behaviours: { name: string }[]
}

type UseCatsArgs = { breedid?: number; behaviourid?: number }

function mapCat(raw: any): CatItem {
    const birthDate = raw.birthDate ?? raw.localDate ?? raw.birth_date ?? null
    const breedName =
        raw.breedName ?? raw.breed?.name ?? raw.breed ?? raw.breed_name ?? null

    const behavioursRaw =
        raw.behaviours ?? raw.behaviourNames ?? raw.behaviour_names ?? raw.behaviors ?? []

    const behaviours: BehaviourItem[] = Array.isArray(behavioursRaw)
        ? behavioursRaw.map((b: any) =>
            typeof b === 'string' ? {name: b} : {name: b?.name})
        : []

    return {
        id: Number(raw.id ?? raw.pk ?? 0),
        name: raw.name,
        birthDate,
        breedName,
        behaviours
    }
}

export function useCats({breedid, behaviourid}: UseCatsArgs) {
    const params = new URLSearchParams()
    if (breedid != null) params.set('breedId', String(breedid))
    if (behaviourid != null) params.set('behaviourId', String(behaviourid))
    const url = params.toString() ? `/api/cats?${params.toString()}` : '/api/cats'

    return useQuery<CatItem[], Error>({
        queryKey: ['cats', breedid ?? null, behaviourid ?? null],
        queryFn: async () => {
            const res = await fetch(url, {headers: {Accept: 'application/json', ...authHeader()}})
            if (!res.ok) {
                const text = await res.text().catch(() => '')
                throw new Error(`Failed to load cats (${res.status}): ${text || res.statusText}`)
            }
            const data = await res.json()
            return Array.isArray(data) ? data.map(mapCat) : []
        }
    })
}
