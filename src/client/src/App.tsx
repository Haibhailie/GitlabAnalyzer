import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'

import Provider from './context/ProjectContext'
import Home from './pages/Home'
import Login from './pages/Login'
import Project from './pages/Project'
import Member from './pages/Member'
import PageWrapper from './components/PageWrapper'

const App = () => {
  return (
    <Provider>
      <Router>
        <Switch>
          <Route path="/home">
            <PageWrapper>
              <Home />
            </PageWrapper>
          </Route>
          <Route path="/project/:id/member/:memberId">
            <PageWrapper>
              <Member />
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
  )
}

export default App
