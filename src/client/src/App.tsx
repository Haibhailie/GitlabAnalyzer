import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'

import Provider from './context/ProjectContext'
import Home from './pages/Home'
import Login from './pages/Login'

const App = () => {
  return (
    <Provider>
      <Router>
        <Switch>
          <Route path="/home">
            <Home />
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
