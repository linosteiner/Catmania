import {useCats} from '../hooks/useCats'
import {CircularProgress, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Alert} from '@mui/material'

export function CatsTable(props: { breedPk?: number; behaviourPk?: number }) {
    const {data, isLoading, isError, error} = useCats(props)
    if (isLoading) return <CircularProgress/>
    if (isError)   return <Alert severity="error">{(error as Error)?.message ?? 'Failed to load cats'}</Alert>

    return (
        <TableContainer component={Paper}>
            <Table size="small">
                <TableHead>
                    <TableRow>
                        <TableCell>Name</TableCell>
                        <TableCell>Birth Date</TableCell>
                        <TableCell>Breed</TableCell>
                        <TableCell>Behaviours</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data?.map((c, idx) => (
                        <TableRow key={idx} hover>
                            <TableCell>{c.name}</TableCell>
                            <TableCell>{c.birthDate ?? '—'}</TableCell>
                            <TableCell>{c.breedName ?? '—'}</TableCell>
                            <TableCell>{c.behaviours?.map(b => b.name).join(', ') || '—'}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    )
}
