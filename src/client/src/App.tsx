import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/home"></Route>
        <Route path="/"></Route>
      </Switch>
    </Router>
  )
}

export default App
