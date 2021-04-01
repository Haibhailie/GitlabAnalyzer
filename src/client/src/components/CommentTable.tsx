import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import dateConverter from '../utils/dateConverter'
import { onError } from '../utils/suspenseDefaults'
import { useHistory } from 'react-router-dom'
import { ICommentData } from '../types'

import Table from '../components/Table'

import styles from '../css/CommentTable.module.css'
import ExternalLink from './ExternalLink'

export interface ICommentTableProps {
  projectId: string
  memberId: string
}

const CommentTable = ({ projectId, memberId }: ICommentTableProps) => {
  const history = useHistory()
  const { Suspense, data, error } = useSuspense<ICommentData[], Error>(
    (setData, setError) => {
      jsonFetcher<ICommentData[]>(
        `/api/project/${projectId}/members/${memberId}/notes`
      )
        .then(comments => {
          setData(comments)
        })
        .catch(onError(setError))
    }
  )

  return (
    <Suspense
      fallback="Loading Comments..."
      error={error?.message ?? 'Unknown Error'}
    >
      <Table
        sortable
        headers={['Date', 'Comment', 'Word count', 'Type', 'GitLab link']}
        columnWidths={['2fr', '3fr', '1fr', '1fr', '1fr']}
        classes={{
          container: styles.tableContainer,
          table: styles.table,
          header: styles.theader,
          data: styles.tdata,
        }}
        title={`Code review comments`}
        data={
          data?.map(({ id, wordcount, content, date, context, webUrl }) => {
            return {
              date: dateConverter(date, true),
              content,
              wordcount,
              context,
              gitlabUrl: <ExternalLink link={webUrl} />,
            }
          }) ?? [{}]
        }
      />
    </Suspense>
  )
}

export default CommentTable
