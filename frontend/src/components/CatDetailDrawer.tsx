import {Box, Chip, Drawer, IconButton, Stack, Typography} from '@mui/material'
import CloseIcon from '@mui/icons-material/Close'
import {useCat, useCatBehaviours, useCatFriends} from '../hooks/useCatDetail'

function formatDate(val?: string | null) {
    if (!val) return '—'
    const m = String(val).match(/^(\d{2})\.(\d{2})\.(\d{4})$/)
    if (m) return val
    try {
        const d = new Date(val)
        if (!Number.isNaN(+d)) {
            return new Intl.DateTimeFormat('de-CH', {day: '2-digit', month: '2-digit', year: 'numeric'}).format(d)
        }
    } catch {
    }
    return val || '—'
}

export function CatDetailDrawer({id, open, onClose}: { id?: number, open: boolean, onClose: () => void }) {
    const {data: cat} = useCat(id)
    const {data: behaviours = []} = useCatBehaviours(id)
    const {data: friends = []} = useCatFriends(id)

    return (
        <Drawer anchor="right" open={open} onClose={onClose}>
            <Box sx={{width: 380, p: 2}} role="presentation">
                <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{mb: 2}}>
                    <Typography variant="h6">Cat details</Typography>
                    <IconButton onClick={onClose}><CloseIcon/></IconButton>
                </Stack>

                <Typography variant="subtitle2">Name</Typography>
                <Typography sx={{mb: 2}}>{cat?.name ?? '—'}</Typography>

                <Typography variant="subtitle2">Birth date</Typography>
                <Typography sx={{mb: 2}}>{formatDate(cat?.birthDate)}</Typography>

                <Typography variant="subtitle2">Breed</Typography>
                <Typography sx={{mb: 2}}>{cat?.breedName ?? '—'}</Typography>

                <Typography variant="subtitle2">Behaviours</Typography>
                <Stack direction="row" spacing={1} sx={{mb: 2, flexWrap: 'wrap'}}>
                    {behaviours.length ? behaviours.map((n, i) => <Chip key={i} label={n}/>) :
                        <Typography>—</Typography>}
                </Stack>

                <Typography variant="subtitle2">Friends</Typography>
                <Stack direction="row" spacing={1} sx={{flexWrap: 'wrap'}}>
                    {friends.length ? friends.map(f => <Chip key={f.id} label={f.name}/>) : <Typography>—</Typography>}
                </Stack>
            </Box>
        </Drawer>
    )
}
