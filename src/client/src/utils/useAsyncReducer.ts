import { useState, Dispatch } from 'react'

type Reducer<S, A> = (prevState: S, action: A) => Promise<S>

const useAsyncReducer = <State, Action>(
  asyncReducer: Reducer<State, Action>,
  initialState: State
): [State, Dispatch<Action>] => {
  const [state, setState] = useState(initialState)

  const dispatch: Dispatch<Action> = async action => {
    setState(await asyncReducer(state, action))
  }

  return [state, dispatch]
}

export default useAsyncReducer
