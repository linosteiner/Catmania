import {useCats} from '../hooks/useCats'
import {CircularProgress, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Alert} from '@mui/material'

function formatDate(iso?: string | null) {
    if (!iso) return '—'
    try {
        const d = new Date(iso)
        return new Intl.DateTimeFormat('de-CH', { day: '2-digit', month: '2-digit', year: 'numeric' }).format(d)
    } catch {
        return iso
    }
}

export function CatsTable(props: { breedid?: number; behaviourid?: number }) {
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
                            <TableCell>{formatDate(c.birthDate)}</TableCell>
                            <TableCell>{c.breedName ?? '—'}</TableCell>
                            <TableCell>{(c.behaviours && c.behaviours.length > 0) ? c.behaviours.map(b => b.name).join(', ') : '—'}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    )
}
