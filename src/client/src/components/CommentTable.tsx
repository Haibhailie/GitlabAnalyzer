import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import dateConverter from '../utils/dateConverter'
import { onError } from '../utils/suspenseDefaults'
import { ICommentData } from '../types'
import { useContext, useEffect, useState } from 'react'
import { UserConfigContext } from '../context/UserConfigContext'

import Table from '../components/Table'
import CommentAccordion from '../components/CommentAccordion'

import styles from '../css/CommentTable.module.css'
import ExternalLink from './ExternalLink'

export interface ICommentTableProps {
  projectId: string
  memberId: string
}

const isLongComment = (content: string) => {
  return content.length > 60
}

const CommentTable = ({ projectId, memberId }: ICommentTableProps) => {
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

  const { userConfigs } = useContext(UserConfigContext)
  const [selectedRange, setSelectedRange] = useState(data)

  useEffect(() => {
    const {
      startDate = new Date(Date.now() - 60 * 24 * 60 * 60 * 1000),
      endDate = new Date(),
    } = userConfigs.selected

    const range = data?.filter(
      comment =>
        new Date(comment.date).getTime() >= startDate.getTime() &&
        new Date(comment.date).getTime() <= endDate.getTime()
    )

    setSelectedRange(range?.length !== 0 ? range : data?.slice(-1))
  }, [userConfigs.selected.startDate, userConfigs.selected.endDate, data])

  return (
    <Suspense
      fallback="Loading Comments..."
      error={error?.message ?? 'Unknown Error'}
    >
      <Table
        sortable
        headers={['Date', 'Comment', 'Word count', 'Type', 'GitLab link']}
        columnWidths={['1fr', '6fr', '0.8fr', '1fr', '0.8fr']}
        classes={{
          container: styles.tableContainer,
          table: styles.table,
          header: styles.theader,
          data: styles.tdata,
        }}
        title={`Code review comments`}
        data={
          selectedRange?.map(
            ({ id, wordcount, content, date, context, webUrl }) => {
              return {
                date: dateConverter(date, true),
                content: isLongComment(content) ? (
                  <CommentAccordion comment={content}></CommentAccordion>
                ) : (
                  content
                ),
                wordcount,
                context: context === 'MergeRequest' ? 'Merge Request' : context,
                gitlabUrl: <ExternalLink link={webUrl} />,
              }
            }
          ) ?? [{}]
        }
      />
    </Suspense>
  )
}

export default CommentTable
