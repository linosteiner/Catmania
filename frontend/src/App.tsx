import {Container, Typography} from '@mui/material'
import {CatsTable} from './components/CatsTable'
import * as React from "react";

export default function App() {
    const [filters] = React.useState({
        breedPk: undefined as number | undefined,
        behaviourPk: undefined as number | undefined
    })

    return (
        <Container sx={{py: 3}}>
            <Typography variant="h4" gutterBottom>Catmania</Typography>
            <CatsTable breedPk={filters.breedPk} behaviourPk={filters.behaviourPk}/>
        </Container>
    )
}
