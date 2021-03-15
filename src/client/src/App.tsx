import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import { ThemeProvider } from '@material-ui/styles'
import globalTheme from './themes/globalTheme'
import LocalizationProvider from '@material-ui/lab/LocalizationProvider'
import AdapterDateFns from '@material-ui/lab/AdapterDateFns'

import UserConfigProvider from './context/UserConfigContext'
import ProjectProvider from './context/ProjectContext'
import Home from './pages/Home'
import Login from './pages/Login'
import Project from './pages/Project'
import PageWrapper from './components/PageWrapper'

const App = () => {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <UserConfigProvider>
        <ProjectProvider>
          <ThemeProvider theme={globalTheme}>
            <Router>
              <Switch>
                <Route path="/home">
                  <PageWrapper>
                    <Home />
                  </PageWrapper>
                </Route>
                <Route path="/project/:id">
                  <PageWrapper>
                    <Project />
                  </PageWrapper>
                </Route>
                <Route path="/">
                  <Login />
                </Route>
              </Switch>
            </Router>
          </ThemeProvider>
        </ProjectProvider>
      </UserConfigProvider>
    </LocalizationProvider>
  )
}

export default App
