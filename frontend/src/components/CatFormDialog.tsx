import * as React from 'react'
import {
    Autocomplete,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    FormLabel,
    MenuItem,
    Stack,
    TextField,
    Typography
} from '@mui/material'
import {type Option, useBehaviours, useBreeds} from '../hooks/useCatalog'
import {doJson, useCreateCat, useUpdateCat} from '../hooks/mutations'
import {useCat, useCatBehaviours} from '../hooks/useCatDetail'
import {useCats} from '../hooks/useCats'

type Props = {
    open: boolean
    onClose: () => void
    initial?: {
        id?: number
        name?: string
        birthDate?: string | null
        fkBreed?: number | null
        behaviourIds?: number[]
    }
}

function toIsoDateInput(d?: string | null) {
    if (!d) return '—'

    if (/^\d{2}\.\d{2}\.\d{4}$/.test(d)) return d

    const m = d.match(/^(\d{4})-(\d{2})-(\d{2})$/)
    if (m) return `${m[3]}.${m[2]}.${m[1]}`

    const dt = new Date(d)
    if (!Number.isNaN(+dt)) {
        const dd = String(dt.getDate()).padStart(2, '0')
        const mm = String(dt.getMonth() + 1).padStart(2, '0')
        return `${dd}.${mm}.${dt.getFullYear()}`
    }
    return '—'
}

export function CatFormDialog({open, onClose, initial}: Props) {
    const isEdit = !!initial?.id
    const {data: breeds = [], isLoading: breedsLoading} = useBreeds(open)
    const {data: behaviours = [], isLoading: behLoading} = useBehaviours(open)
    const {data: catDetail} = useCat(isEdit && open ? initial!.id : undefined)
    const {data: catBehaviours = []} = useCatBehaviours(isEdit && open ? initial!.id : undefined)
    const {data: allCats = []} = useCats({})
    const [name, setName] = React.useState(initial?.name ?? '')
    const [birthDate, setBirthDate] = React.useState<string>(toIsoDateInput(initial?.birthDate ?? ''))
    const [fkBreed, setFkBreed] = React.useState<number | ''>('')
    const [behIds, setBehIds] = React.useState<number[]>([])
    const [friendIds, setFriendIds] = React.useState<number[]>([])
    const originalFriendIdsRef = React.useRef<number[]>([])

    const initBasicRef = React.useRef<number | null>(null)
    const initBehRef = React.useRef<number | null>(null)
    const loadedFriendsForRef = React.useRef<number | null>(null)

    React.useEffect(() => {
        if (!open) {
            initBasicRef.current = null
            initBehRef.current = null
            loadedFriendsForRef.current = null
            return
        }
        if (initBasicRef.current === (initial?.id ?? 0)) return
        setName(initial?.name ?? (catDetail?.name ?? ''))
        setBirthDate(toIsoDateInput(initial?.birthDate ?? (catDetail?.birthDate ?? '')))
        if (typeof initial?.fkBreed === 'number') {
            setFkBreed(initial.fkBreed)
        } else if (catDetail?.breedName && breeds.length) {
            const match = breeds.find(b => b.name === catDetail.breedName)
            setFkBreed(match ? match.id : '')
        } else {
            setFkBreed('')
        }
        initBasicRef.current = initial?.id ?? 0
    }, [open, initial, catDetail, breeds])

    React.useEffect(() => {
        if (!open) return

        if (initBehRef.current === (initial?.id ?? -1)) return
        if (Array.isArray(initial?.behaviourIds)) {
            setBehIds(initial.behaviourIds.filter(n => Number.isFinite(n)))
            initBehRef.current = initial?.id ?? -1
            return
        }

        if (isEdit && Array.isArray(catBehaviours)) {

            if (behLoading) return
            const nameToId = new Map(behaviours.map(b => [b.name, b.id] as const))
            const ids = catBehaviours
                .map((b: any) => {
                    if (typeof b === 'object' && b && 'id' in b) return Number(b.id)
                    if (typeof b === 'string') return nameToId.get(b) ?? NaN
                    return NaN
                })
                .filter(n => Number.isFinite(n)) as number[]
            setBehIds(ids)
            initBehRef.current = initial?.id ?? -1
            return
        }

        if (!isEdit) {
            setBehIds([])
            initBehRef.current = -1
        }
    }, [open, isEdit, initial, catBehaviours, behaviours, behLoading])

    React.useEffect(() => {
        if (!open || !isEdit || !initial?.id) return
        if (loadedFriendsForRef.current === initial.id) return

        async function loadFriends(catId: number) {
            const res = await fetch(`/api/cats/${catId}/friends`, {headers: {Accept: 'application/json'}})
            if (res.ok) {
                const friends = await res.json()
                const ids = Array.isArray(friends)
                    ? friends.map((f: any) => Number(f.id)).filter(n => Number.isFinite(n) && n > 0)
                    : []
                originalFriendIdsRef.current = ids
                setFriendIds(ids)
            } else {
                originalFriendIdsRef.current = []
                setFriendIds([])
            }
        }

        loadedFriendsForRef.current = initial.id
        loadFriends(initial.id).catch(() => {
            originalFriendIdsRef.current = []
            setFriendIds([])
        })
    }, [open, isEdit, initial?.id])

    const selectedBehaviourOptions = React.useMemo(() => {
        const byId = new Map(behaviours.map(b => [b.id, b] as const))
        return behIds.map(id => byId.get(id)).filter(Boolean) as Option[]
    }, [behaviours, behIds])

    const friendOptions = React.useMemo(
        () =>
            (allCats as any[])
                .map(c => ({id: Number(c.id), name: String(c.name ?? '')}))
                .filter(o => Number.isFinite(o.id) && o.id > 0 && (!initial?.id || o.id !== initial.id))
                .sort((a, b) => a.name.localeCompare(b.name)),
        [allCats, initial?.id]
    )
    const create = useCreateCat()
    const update = useUpdateCat()
    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        if (isEdit && initial?.id) {
            await update.mutateAsync({
                id: initial.id,
                payload: {
                    name: name.trim(),
                    birthDate: null,
                    fkBreed: null,
                    behaviourIds: behIds
                }
            })
            const prev = originalFriendIdsRef.current
            const next = friendIds.filter(n => Number.isFinite(n) && n > 0)
            const toAdd = next.filter(id => !prev.includes(id))
            const toRemove = prev.filter(id => !next.includes(id))
            if (toAdd.length || toRemove.length) {
                await Promise.all([
                    ...toAdd.map(fid => doJson<void>(`/api/cats/${initial.id}/friends/${fid}`, 'PUT')),
                    ...toRemove.map(fid => doJson<void>(`/api/cats/${initial.id}/friends/${fid}`, 'DELETE'))
                ])
            }
        } else {
            const created: any = await create.mutateAsync({
                name: name.trim(),
                birthDate: birthDate || null,
                fkBreed: fkBreed === '' ? (undefined as any) : Number(fkBreed),
                behaviourIds: behIds
            } as any)
            const newId = Number(created?.id)
            const next = friendIds.filter(n => Number.isNaN(n) === false && n > 0)
            if (Number.isFinite(newId) && newId > 0 && next.length) {
                await Promise.all(next.map(fid => doJson<void>(`/api/cats/${newId}/friends/${fid}`, 'PUT')))
            }
        }
        onClose()
    }
    const canSubmit = name.trim().length > 0 && (!isEdit ? typeof fkBreed === 'number' : true)
    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <form onSubmit={onSubmit}>
                <DialogTitle>{isEdit ? 'Edit Cat' : 'New Cat'}</DialogTitle>
                <DialogContent>
                    <Stack spacing={2} sx={{mt: 1}}>
                        <TextField
                            label="Name"
                            value={name}
                            onChange={e => setName(e.target.value)}
                            required
                            fullWidth
                        />
                        {isEdit ? (
                            <FormControl>
                                <FormLabel>Birth Date</FormLabel>
                                <Typography>{toIsoDateInput(catDetail?.birthDate ?? initial?.birthDate) || '—'}</Typography>
                            </FormControl>
                        ) : (
                            <TextField
                                label="Birth Date"
                                type="date"
                                value={birthDate}
                                onChange={e => setBirthDate(e.target.value)}
                                InputLabelProps={{shrink: true}}
                                fullWidth
                            />
                        )}
                        {isEdit ? (
                            <FormControl>
                                <FormLabel>Breed</FormLabel>
                                <Typography>{catDetail?.breedName ?? '—'}</Typography>
                            </FormControl>
                        ) : (
                            <TextField
                                select
                                label="Breed"
                                value={fkBreed}
                                onChange={e => setFkBreed(e.target.value === '' ? '' : Number(e.target.value))}
                                helperText={breedsLoading ? 'Loading…' : 'Leave empty to clear'}
                                fullWidth
                                disabled={breedsLoading}
                            >
                                <MenuItem value="">— none —</MenuItem>
                                {breeds.map((b: Option) => (
                                    <MenuItem key={b.id} value={b.id}>{b.name}</MenuItem>
                                ))}
                            </TextField>
                        )}
                        <Autocomplete
                            multiple
                            options={behaviours}
                            loading={behLoading}
                            getOptionLabel={(o: Option) => o.name}
                            isOptionEqualToValue={(o, v) => o.id === v.id}
                            value={selectedBehaviourOptions}
                            onChange={(_, vals: Option[]) => setBehIds(vals.map(v => v.id))}
                            renderInput={(params) => (
                                <TextField {...params} label="Behaviours" placeholder="Select..."/>
                            )}
                        />
                        <Autocomplete
                            multiple
                            options={friendOptions}
                            getOptionLabel={(o) => o.name}
                            isOptionEqualToValue={(o, v) => o.id === v.id}
                            value={friendOptions.filter(o => friendIds.includes(o.id))}
                            onChange={(_, vals) => setFriendIds(vals.map(v => v.id).filter(n => Number.isFinite(n) && n > 0))}
                            renderInput={(params) => (
                                <TextField {...params} label="Friends" placeholder="Select friends..."/>
                            )}
                        />
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained" disabled={!canSubmit}>
                        {isEdit ? 'Save' : 'Create'}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    )
}
