import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import dateConverter from '../utils/dateConverter'
import { onError } from '../utils/suspenseDefaults'
import { ICommentData } from '../types'
import { useContext, useEffect, useState } from 'react'
import { UserConfigContext } from '../context/UserConfigContext'
import { ThemeProvider, Tooltip } from '@material-ui/core'
import tooltipTheme from '../themes/tooltipTheme'

import Table from '../components/Table'
import CommentAccordion from '../components/CommentAccordion'
import ExternalLink from '../components/ExternalLink'

import styles from '../css/CommentTable.module.css'

import warning from '../assets/warning.svg'

export interface ICommentTableProps {
  projectId: string
  memberId: string
}

const isLongComment = (content: string) => {
  return content.length > 60
}

const isInvalidUrl = (url: string) => {
  return url.includes('example')
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
    <ThemeProvider theme={tooltipTheme}>
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
                  context:
                    context === 'MergeRequest' ? 'Merge Request' : context,
                  gitlabUrl: isInvalidUrl(webUrl) ? (
                    <Tooltip
                      title="Unable to retrieve link due to server configuration error. "
                      placement="top"
                      arrow
                    >
                      <img className={styles.icon} src={warning} />
                    </Tooltip>
                  ) : (
                    <ExternalLink link={webUrl} />
                  ),
                }
              }
            ) ?? [{}]
          }
        />
      </Suspense>
    </ThemeProvider>
  )
}

export default CommentTable
