import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import Header from './components/Header'
import Login from './pages/Login'

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/">
          <Login />
        </Route>
        <Route path="/home">
          <Header />
        </Route>
      </Switch>
    </Router>
  )
}

export default App
