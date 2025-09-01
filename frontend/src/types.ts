export interface Behaviour {
    name: string
}

export interface Cat {
    name: string
    birthDate?: string
    breedName?: string
    behaviours: Behaviour[]
}
