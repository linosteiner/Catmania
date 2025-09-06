import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography } from '@mui/material'

export function ConfirmDeleteDialog({ open, onClose, onConfirm, name } : { open: boolean, onClose: () => void, onConfirm: () => void, name?: string }) {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Delete cat</DialogTitle>
      <DialogContent>
        <Typography>Are you sure you want to delete <strong>{name ?? 'this cat'}</strong>?</Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button color="error" variant="contained" onClick={onConfirm}>Delete</Button>
      </DialogActions>
    </Dialog>
  )
}
