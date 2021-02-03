import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import Header from './components/Header'

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/">
          <Header />
        </Route>
        <Route path="/home"></Route>
      </Switch>
    </Router>
  )
}

export default App
