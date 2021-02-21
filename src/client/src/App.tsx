import { useEffect } from 'react'
import {
  BrowserRouter as Router,
  Switch,
  Route,
  useHistory,
} from 'react-router-dom'

import Provider from './context/ProjectContext'
import Home from './pages/Home'
import Login from './pages/Login'
import Project from './pages/Project'

const AuthCheck = () => {
  const history = useHistory()
  useEffect(() => {
    fetch('/api/ping', {
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
    <Provider>
      <Router>
        <Switch>
          <Route path="/home">
            <Home />
          </Route>
          <Route path="/project/:id">
            <Project />
          </Route>
          <Route path="/login">
            <Login />
          </Route>
          <Route path="/">
            <AuthCheck />
          </Route>
        </Switch>
      </Router>
    </Provider>
  )
}

export default App
