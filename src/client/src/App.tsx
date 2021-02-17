import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import SideNav from './components/SideNav'

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/"></Route>
        <Route path="/home"></Route>
      </Switch>
    </Router>
  )
}

export default App
