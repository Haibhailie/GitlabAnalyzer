import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import Table from './components/Table'

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/">
          <Table
            sortable
            columnWidths={['1fr', '4fr', '2fr']}
            data={[
              {
                name: 'test1',
                email: 'test2',
                role: 'owner3',
              },
              {
                name: 'test2',
                email: 'test3',
                role: 'owner1',
              },
              {
                name: 'test3',
                email: 'test1',
                role: 'owner2',
              },
            ]}
          />
        </Route>
      </Switch>
    </Router>
  )
}

export default App
