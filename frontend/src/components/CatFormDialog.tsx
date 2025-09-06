import * as React from 'react'
import {
    Autocomplete,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    MenuItem,
    Stack,
    TextField,
    FormControl,
    FormLabel,
    Typography
} from '@mui/material'
import { type Option, useBehaviours, useBreeds } from '../hooks/useCatalog'
import { useCreateCat, useUpdateCat } from '../hooks/mutations'
import { useCat, useCatBehaviourIds } from '../hooks/useCatDetail'

type Props = {
    open: boolean
    onClose: () => void
    initial?: {
        id?: number
        name?: string
        birthDate?: string | null
        breedId?: number | null
        behaviourIds?: number[]
    }
}

function toDateInput(d?: string | null) {
    if (!d) return ''
    if (/^\d{4}-\d{2}-\d{2}$/.test(d)) return d
    const m = d.match(/^(\d{2})\.(\d{2})\.(\d{4})$/)
    if (m) return `${m[3]}-${m[2]}-${m[1]}`
    const dt = new Date(d)
    if (Number.isNaN(+dt)) return ''
    const mm = String(dt.getMonth() + 1).padStart(2, '0')
    const dd = String(dt.getDate()).padStart(2, '0')
    return `${dt.getFullYear()}-${mm}-${dd}`
}

export function CatFormDialog({ open, onClose, initial }: Props) {
    const isEdit = !!initial?.id

    const { data: behaviours = [], isLoading: behLoading } = useBehaviours(open)
    const { data: breeds = [], isLoading: breedsLoading } = useBreeds(open && !isEdit)

    const { data: catDetail } = useCat(isEdit && open ? initial!.id : undefined)

    const behIdsQ = useCatBehaviourIds(isEdit && open ? initial!.id : undefined)
    const serverBehIds = behIdsQ.data ?? []

    const [name, setName] = React.useState(initial?.name ?? '')
    const [nameTouched, setNameTouched] = React.useState(false)

    const [birthDate, setBirthDate] = React.useState<string>(toDateInput(initial?.birthDate ?? ''))

    const [fkBreed, setFkBreed] = React.useState<number | ''>('')

    const [behIds, setBehIds] = React.useState<number[]>([])
    const [behTouched, setBehTouched] = React.useState(false)

    React.useEffect(() => {
        if (!open) {
            setNameTouched(false)
            setBehTouched(false)
            setBehIds([])
        }
    }, [open])

    React.useEffect(() => {
        if (!open) return

        if (!nameTouched) {
            setName(initial?.name ?? catDetail?.name ?? '')
        }

        setBirthDate(toDateInput(initial?.birthDate ?? catDetail?.birthDate ?? ''))

        if (!isEdit) {
            if (typeof initial?.breedId === 'number') {
                setFkBreed(initial.breedId)
            } else if (catDetail?.breedName && breeds.length) {
                const m = breeds.find(b => b.name === catDetail.breedName)
                setFkBreed(m ? m.id : '')
            } else {
                setFkBreed('')
            }
        } else {
            setFkBreed('')
        }
    }, [open, isEdit, initial, catDetail, breeds, nameTouched])

    React.useEffect(() => {
        if (!open || !isEdit) return
        if (!behIdsQ.isSuccess) return
        if (behTouched) return
        setBehIds(serverBehIds)
    }, [open, isEdit, behIdsQ.isSuccess, serverBehIds, behTouched])

    const create = useCreateCat()
    const update = useUpdateCat()

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        const payload = {
            name: name.trim(),
            birthDate: isEdit ? null : (birthDate || null),
            fkBreed: isEdit ? null : (fkBreed === '' ? 0 : Number(fkBreed)),
            behaviourIds: behIds
        }
        if (isEdit && initial?.id != null) {
            await update.mutateAsync({ id: initial.id, payload })
        } else {
            await create.mutateAsync(payload as any)
        }
        onClose()
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <form onSubmit={onSubmit}>
                <DialogTitle>{isEdit ? 'Edit Cat' : 'New Cat'}</DialogTitle>
                <DialogContent>
                    <Stack spacing={2} sx={{ mt: 1 }}>
                        <TextField
                            label="Name"
                            value={name}
                            onChange={e => { setName(e.target.value); setNameTouched(true) }}
                            required
                            fullWidth
                        />

                        {}
                        {isEdit ? (
                            <FormControl>
                                <FormLabel>Birth Date</FormLabel>
                                <Typography>{birthDate || '—'}</Typography>
                            </FormControl>
                        ) : (
                            <TextField
                                label="Birth Date"
                                type="date"
                                value={birthDate}
                                onChange={e => setBirthDate(e.target.value)}
                                InputLabelProps={{ shrink: true }}
                                fullWidth
                            />
                        )}

                        {}
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

                        {}
                        <Autocomplete
                            multiple
                            options={behaviours}
                            loading={behLoading}
                            getOptionLabel={(o: Option) => o.name}
                            value={behaviours.filter(b => behIds.includes(b.id))}
                            onChange={(_, vals: Option[]) => {
                                setBehIds(vals.map(v => v.id))
                                setBehTouched(true)
                            }}
                            renderInput={(params) => (
                                <TextField {...params} label="Behaviours" placeholder="Select..." />
                            )}
                        />
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained">{isEdit ? 'Save' : 'Create'}</Button>
                </DialogActions>
            </form>
        </Dialog>
    )
}
