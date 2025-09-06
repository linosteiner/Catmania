import {useCats} from '../hooks/useCats'
import {
    CircularProgress, Paper, Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Alert, IconButton, Tooltip, Stack, Button, TableSortLabel
} from '@mui/material'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import InfoIcon from '@mui/icons-material/Info'
import AddIcon from '@mui/icons-material/Add'
import * as React from 'react'
import { useDeleteCat } from '../hooks/mutations'
import { CatFormDialog } from './CatFormDialog'
import { CatDetailDrawer } from './CatDetailDrawer'
import { ConfirmDeleteDialog } from './ConfirmDeleteDialog'

// If you have the CatItem type exported from useCats, you can import it.
// import type { CatItem } from '../hooks/useCats'

type Order = 'asc' | 'desc'

// Local copy of the shape we need for sorting (adjust if you import CatItem)
type CatRow = {
    id: number
    name: string
    birthDate?: string | null
    breedName?: string | null
    behaviours: { name: string }[]
}

function formatDate(iso?: string | null) {
    if (!iso) return '—'
    try {
        const d = new Date(iso)
        return new Intl.DateTimeFormat('de-CH', { day: '2-digit', month: '2-digit', year: 'numeric' }).format(d)
    } catch {
        return iso || '—'
    }
}

function makeBehavioursText(bhs: {name: string}[]) {
    if (!bhs || bhs.length === 0) return ''
    return bhs.map(b => b.name).join(', ')
}

export function CatsTable(props: { breedid?: number; behaviourid?: number }) {
    const {data, isLoading, isError, error} = useCats(props)
    const del = useDeleteCat()

    const [openForm, setOpenForm] = React.useState(false)
    const [editRow, setEditRow] = React.useState<any | null>(null)
    const [detailId, setDetailId] = React.useState<number | undefined>(undefined)
    const [deleteRow, setDeleteRow] = React.useState<{ id: number; name: string } | null>(null)

    // --- Sorting state ---
    const [order, setOrder] = React.useState<Order>('asc')
    const [orderBy, setOrderBy] = React.useState<keyof CatRow>('name')

    const handleRequestSort = (property: keyof CatRow) => {
        const isAsc = orderBy === property && order === 'asc'
        setOrder(isAsc ? 'desc' : 'asc')
        setOrderBy(property)
    }

    const sorted: CatRow[] = React.useMemo(() => {
        if (!data) return []
        const arr = [...data] as CatRow[]
        return arr.sort((a, b) => {
            let av: any
            let bv: any

            switch (orderBy) {
                case 'name':
                    av = (a.name ?? '').toLocaleLowerCase()
                    bv = (b.name ?? '').toLocaleLowerCase()
                    break
                case 'breedName':
                    av = (a.breedName ?? '').toLocaleLowerCase()
                    bv = (b.breedName ?? '').toLocaleLowerCase()
                    break
                case 'birthDate':
                    av = a.birthDate ? new Date(a.birthDate).getTime() : 0
                    bv = b.birthDate ? new Date(b.birthDate).getTime() : 0
                    break
                case 'behaviours':
                    av = makeBehavioursText(a.behaviours).toLocaleLowerCase()
                    bv = makeBehavioursText(b.behaviours).toLocaleLowerCase()
                    break
                default:
                    av = a[orderBy] as any
                    bv = b[orderBy] as any
            }

            if (av < bv) return order === 'asc' ? -1 : 1
            if (av > bv) return order === 'asc' ? 1 : -1
            return 0
        })
    }, [data, order, orderBy])

    if (isLoading) return <CircularProgress/>
    if (isError)   return <Alert severity="error">{(error as Error)?.message ?? 'Failed to load cats'}</Alert>

    return (
        <>
            <Stack direction="row" justifyContent="space-between" sx={{ mb: 1 }}>
                <div />
                <Button startIcon={<AddIcon />} variant="contained" onClick={() => { setEditRow(null); setOpenForm(true) }}>
                    New Cat
                </Button>
            </Stack>

            <TableContainer component={Paper}>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell sortDirection={orderBy === 'name' ? order : false}>
                                <TableSortLabel
                                    active={orderBy === 'name'}
                                    direction={orderBy === 'name' ? order : 'asc'}
                                    onClick={() => handleRequestSort('name')}
                                >
                                    Name
                                </TableSortLabel>
                            </TableCell>

                            <TableCell sortDirection={orderBy === 'birthDate' ? order : false}>
                                <TableSortLabel
                                    active={orderBy === 'birthDate'}
                                    direction={orderBy === 'birthDate' ? order : 'asc'}
                                    onClick={() => handleRequestSort('birthDate')}
                                >
                                    Birth Date
                                </TableSortLabel>
                            </TableCell>

                            <TableCell sortDirection={orderBy === 'breedName' ? order : false}>
                                <TableSortLabel
                                    active={orderBy === 'breedName'}
                                    direction={orderBy === 'breedName' ? order : 'asc'}
                                    onClick={() => handleRequestSort('breedName')}
                                >
                                    Breed
                                </TableSortLabel>
                            </TableCell>

                            <TableCell sortDirection={orderBy === 'behaviours' ? order : false}>
                                <TableSortLabel
                                    active={orderBy === 'behaviours'}
                                    direction={orderBy === 'behaviours' ? order : 'asc'}
                                    onClick={() => handleRequestSort('behaviours')}
                                >
                                    Behaviours
                                </TableSortLabel>
                            </TableCell>

                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {sorted.map((c, idx) => (
                            <TableRow key={idx} hover>
                                <TableCell>{c.name}</TableCell>
                                <TableCell>{formatDate(c.birthDate)}</TableCell>
                                <TableCell>{c.breedName ?? '—'}</TableCell>
                                <TableCell>{c.behaviours?.length ? c.behaviours.map(b => b.name).join(', ') : '—'}</TableCell>
                                <TableCell align="right">
                                    <Tooltip title="Details">
                                        <IconButton onClick={() => setDetailId((c as any).id)}><InfoIcon /></IconButton>
                                    </Tooltip>
                                    <Tooltip title="Edit">
                                        <IconButton onClick={() => { setEditRow(c as any); setOpenForm(true) }}><EditIcon /></IconButton>
                                    </Tooltip>
                                    <Tooltip title="Delete">
                                        <IconButton
                                            color="error"
                                            onClick={() => setDeleteRow({ id: (c as any).id, name: c.name })}
                                        >
                                            <DeleteIcon />
                                        </IconButton>
                                    </Tooltip>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>

                </Table>
            </TableContainer>

            {/* Create / Edit */}
            <CatFormDialog
                open={openForm}
                onClose={() => setOpenForm(false)}
                initial={editRow ? {
                    id: (editRow as any).id,
                    name: editRow.name,
                    birthDate: editRow.birthDate ?? null
                } : undefined}
            />

            {/* Details */}
            <CatDetailDrawer id={detailId} open={!!detailId} onClose={() => setDetailId(undefined)} />

            {/* Confirm Delete */}
            <ConfirmDeleteDialog
                open={!!deleteRow}
                name={deleteRow?.name}
                onClose={() => setDeleteRow(null)}
                onConfirm={async () => {
                    if (deleteRow) {
                        try {
                            await del.mutateAsync(deleteRow.id)
                            if (detailId === deleteRow.id) setDetailId(undefined)
                        } finally {
                            setDeleteRow(null)
                        }
                    }
                }}
            />
        </>
    )
}
