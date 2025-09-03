import {Container, Typography} from '@mui/material'
import {CatsTable} from './components/CatsTable'
import * as React from "react";

export default function App() {
    const [filters] = React.useState({
        breedid: undefined as number | undefined,
        behaviourid: undefined as number | undefined
    })

    return (
        <Container sx={{py: 3}}>
            <Typography variant="h4" gutterBottom>Catmania</Typography>
            <CatsTable breedid={filters.breedid} behaviourid={filters.behaviourid}/>
        </Container>
    )
}
