import { createMuiTheme } from '@material-ui/core/styles'

const globalTheme = createMuiTheme({
  typography: {
    fontFamily: 'Poppins',
  },
  components: {
    MuiSelect: {
      styleOverrides: {
        root: { padding: '10px', fontSize: '14px' },
      },
    },
  },
})

export default globalTheme
