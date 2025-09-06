import * as React from 'react'
import { createTheme, ThemeProvider, CssBaseline } from '@mui/material'
import { Container, Typography, IconButton, Stack, Tooltip } from '@mui/material'
import DarkModeIcon from '@mui/icons-material/DarkMode'
import LightModeIcon from '@mui/icons-material/LightMode'
import { CatsTable } from './components/CatsTable'

type Mode = 'light' | 'dark'

function useDarkMode(): [Mode, () => void] {
    const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
    const [mode, setMode] = React.useState<Mode>(() => {
        const saved = (localStorage.getItem('themeMode') as Mode | null)
        return saved ?? (prefersDark ? 'dark' : 'light')
    })

    React.useEffect(() => {
        const handler = (e: MediaQueryListEvent) => {
            if (!localStorage.getItem('themeMode')) {
                setMode(e.matches ? 'dark' : 'light')
            }
        }
        const mq = window.matchMedia('(prefers-color-scheme: dark)')
        mq.addEventListener?.('change', handler)
        return () => mq.removeEventListener?.('change', handler)
    }, [])

    const toggle = React.useCallback(() => {
        const next: Mode = mode === 'dark' ? 'light' : 'dark'
        setMode(next)
        localStorage.setItem('themeMode', next)
    }, [mode])

    return [mode, toggle]
}

export default function App() {
    const [mode, toggleMode] = useDarkMode()
    const theme = React.useMemo(() => createTheme({ palette: { mode } }), [mode])

    const [filters] = React.useState({
        breedid: undefined as number | undefined,
        behaviourid: undefined as number | undefined
    })

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Container sx={{ py: 3 }}>
                <Stack direction="row" alignItems="center" justifyContent="space-between">
                    <Typography variant="h4" gutterBottom>Catmania</Typography>
                    <Tooltip title={mode === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}>
                        <IconButton onClick={toggleMode} size="large">
                            {mode === 'dark' ? <LightModeIcon /> : <DarkModeIcon />}
                        </IconButton>
                    </Tooltip>
                </Stack>
                <CatsTable breedid={filters.breedid} behaviourid={filters.behaviourid} />
            </Container>
        </ThemeProvider>
    )
}
