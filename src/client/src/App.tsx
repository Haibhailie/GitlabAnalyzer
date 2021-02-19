import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'

import Provider from './context/ProjectContext'
import Home from './pages/Home'
import Project from './pages/Project'

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
          <Route path="/"></Route>
        </Switch>
      </Router>
    </Provider>
  )
}

export default App
