import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import ActivityGraph from './components/ActivityGraph'

import Provider from './context/ProjectContext'
import Home from './pages/Home'

const App = () => {
  return (
    <Provider>
      <Router>
        <Switch>
          <Route path="/home">
            <Home />
          </Route>
          <Route path="/">
            <ActivityGraph mergeUrl="/t" commitUrl="/tt" />
          </Route>
        </Switch>
      </Router>
    </Provider>
  )
}

export default App
