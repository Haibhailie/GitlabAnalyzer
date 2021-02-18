import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import Home from './pages/Home'

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/home">
          <Home />
        </Route>
        <Route path="/"></Route>
      </Switch>
    </Router>
  )
}

export default App
