import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import PageWrapper from './components/PageWrapper'

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/home"></Route>
        <Route path="/">
          <PageWrapper />
        </Route>
      </Switch>
    </Router>
  )
}

export default App
