import { useContext, useEffect, useState } from 'react'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import dateConverter from '../utils/dateConverter'
import { onError } from '../utils/suspenseDefaults'
import { UserConfigContext } from '../context/UserConfigContext'
import { ThemeProvider, Tooltip } from '@material-ui/core'
import tooltipTheme from '../themes/tooltipTheme'
import { TCommentData } from '../types'
import { LONG_COMMENT_LEN } from '../utils/constants'

import Table from '../components/Table'
import ExternalLink from '../components/ExternalLink'
import Dropdown from '../components/Dropdown'

import styles from '../css/CommentTable.module.css'

import warning from '../assets/warning.svg'

export interface ICommentTableProps {
  projectId: string
  memberId: string
}

const isLongComment = (content: string) => content.length > LONG_COMMENT_LEN

const isInvalidUrl = (url: string) => url.includes('example')

const formatParentAuthor = (author: string) => {
  if (author === 'self') {
    return 'Self'
  } else if (author === '') {
    return 'Deleted user'
  }
  return author
}

const CommentTable = ({ projectId, memberId }: ICommentTableProps) => {
  const { Suspense, data, error } = useSuspense<TCommentData>(
    (setData, setError) => {
      jsonFetcher<TCommentData>(
        `/api/project/${projectId}/members/${memberId}/notes`
      )
        .then(setData)
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
          headers={[
            'Date',
            'Comment',
            'Word count',
            'Type',
            'By',
            'GitLab link',
          ]}
          columnWidths={['1fr', '5fr', '0.8fr', '1fr', '0.8fr', '0.8fr']}
          classes={{
            container: styles.tableContainer,
            table: styles.table,
            header: styles.theader,
            data: styles.tdata,
          }}
          title={`Code review comments`}
          data={
            selectedRange?.map(
              ({ wordcount, content, date, context, webUrl, parentAuthor }) => {
                return {
                  date: dateConverter(date, true),
                  content: (
                    <div className={styles.commentContainer}>
                      <Dropdown
                        arrowOnLeft
                        fixedCollapsed={!isLongComment(content)}
                        className={styles.comment}
                        header={
                          <div className={styles.commentHeader}>{content}</div>
                        }
                      >
                        <div className={styles.commentBody}>{content}</div>
                      </Dropdown>
                    </div>
                  ),
                  wordcount,
                  context:
                    context === 'MergeRequest' ? 'Merge Request' : context,
                  parentAuthor: formatParentAuthor(parentAuthor),
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
