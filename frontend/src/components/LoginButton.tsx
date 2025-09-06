import * as React from 'react'
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    Stack,
    TextField,
    Tooltip
} from '@mui/material'
import LoginIcon from '@mui/icons-material/Login'
import LogoutIcon from '@mui/icons-material/Logout'
import {getToken, login, logout} from '../auth'

export default function LoginButton() {
    const [open, setOpen] = React.useState(false)
    const [username, setUsername] = React.useState('')
    const [password, setPassword] = React.useState('')
    const [error, setError] = React.useState<string | null>(null)
    const [token, setTokenState] = React.useState<string | null>(getToken())

    const doLogin = async () => {
        setError(null)
        try {
            await login(username, password)
            setTokenState(getToken())
            setOpen(false)
            setUsername('')
            setPassword('')
        } catch (e: any) {
            setError(e?.message ?? 'Login failed')
        }
    }

    const doLogout = () => {
        logout()
        setTokenState(null)
    }

    if (token) {
        return (
            <Tooltip title="Logout">
                <IconButton onClick={doLogout} aria-label="logout"><LogoutIcon/></IconButton>
            </Tooltip>
        )
    }

    return (
        <>
            <Tooltip title="Login">
                <IconButton onClick={() => setOpen(true)} aria-label="login"><LoginIcon/></IconButton>
            </Tooltip>
            <Dialog open={open} onClose={() => setOpen(false)}>
                <DialogTitle>Login</DialogTitle>
                <DialogContent>
                    <Stack spacing={2} sx={{mt: 1}}>
                        <TextField label="Username" value={username} onChange={e => setUsername(e.target.value)}
                                   autoFocus/>
                        <TextField label="Password" type="password" value={password}
                                   onChange={e => setPassword(e.target.value)}/>
                        {error && <div style={{color: 'crimson'}}>{error}</div>}
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Cancel</Button>
                    <Button onClick={doLogin} variant="contained">Login</Button>
                </DialogActions>
            </Dialog>
        </>
    )
}
