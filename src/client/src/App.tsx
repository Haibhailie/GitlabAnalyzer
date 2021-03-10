import { useEffect } from 'react'
import {
  BrowserRouter as Router,
  Switch,
  Route,
  useHistory,
} from 'react-router-dom'
import { URLBASE } from './utils/constants'

import Provider from './context/ProjectContext'
import Home from './pages/Home'
import Login from './pages/Login'
import Project from './pages/Project'
import PageWrapper from './components/PageWrapper'
import UserConfigProvider from './context/UserConfigContext'

const AuthCheck = () => {
  const history = useHistory()
  useEffect(() => {
    fetch(`${URLBASE}/api/ping`, {
      credentials: 'include',
    }).then(res => {
      if (res.status === 200) {
        history.push('/home')
      } else {
        history.push('/login')
      }
    })
  }, [])
  return <></>
}

const App = () => {
  return (
    <UserConfigProvider>
      <Provider>
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
          </Switch>
          <Switch>
            <Route path="/login">
              <Login />
            </Route>
            <Route path="/">
              <AuthCheck />
            </Route>
          </Switch>
        </Router>
      </Provider>
    </UserConfigProvider>
  )
}

export default App
