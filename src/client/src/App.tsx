import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'

import Provider from './context/ProjectContext'
import Home from './pages/Home'
import Login from './pages/Login'
import Project from './pages/Project'
import PageWrapper from './components/PageWrapper'
import UserConfigProvider from './context/UserConfigContext'

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
            <Route path="/">
              <Login />
            </Route>
          </Switch>
        </Router>
      </Provider>
    </UserConfigProvider>
  )
}

export default App
