import { IDiffData } from '../types'
import jsonFetcher from '../utils/jsonFetcher'
import { onError } from '../utils/suspenseDefaults'
import useSuspense from '../utils/useSuspense'

import styles from '../css/Diff.module.css'

export interface IDiffProps {
  id?: string
  source?: 'mergerequest' | 'commit'
  projectId?: string
}

const Diff = ({ source, id, projectId }: IDiffProps) => {
  const { Suspense, error } = useSuspense<IDiffData>(
    (setData, setError) => {
      if (source && id && projectId) {
        jsonFetcher<IDiffData>(`/api/project/${projectId}/${source}/${id}/diff`)
          .then(setData)
          .catch(onError(setError))
      } else {
        setError(new Error("Woops! We couldn't find this diff..."))
      }
    },
    [source, id, projectId]
  )
  return (
    <div className={styles.container}>
      <Suspense fallback="Getting diff..." error={error?.message}>
        <div className={styles.scrollContainer}>
          The diff will show up here :)
        </div>
      </Suspense>
    </div>
  )
}

export default Diff
