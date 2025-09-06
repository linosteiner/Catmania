import {Box, Chip, Drawer, IconButton, Stack, Typography} from '@mui/material'
import CloseIcon from '@mui/icons-material/Close'
import {useCat, useCatBehaviours, useCatFriends} from '../hooks/useCatDetail'

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
                <Typography sx={{mb: 2}}>{cat?.birthDate ?? '—'}</Typography>

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
