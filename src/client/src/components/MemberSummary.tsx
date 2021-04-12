import useSuspense from '../utils/useSuspense'
import useProject from '../utils/useProject'
import { IMember } from '../context/ProjectContext'
import { IStatProps } from './Stat'

import StatSummary from '../components/StatSummary'
import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/MemberSummary.module.css'

export interface IMemberSummaryProps {
  memberId: number
}

interface IMemberWithStats extends IMember {
  stats: IStatProps[]
}

const MemberSummary = ({ memberId }: IMemberSummaryProps) => {
  const project = useProject()
  const { Suspense, data, error } = useSuspense<IMemberWithStats>(
    (setData, setError) => {
      if (project?.members[memberId]) {
        const member = project.members[memberId]
        setData({
          ...member,
          stats: [
            {
              name: 'Merge request score',
              value: `${member.soloMrScore} + ${member.sharedMrScore}`,
              rawValue: member.soloMrScore + member.sharedMrScore,
              description:
                'Sum of merge request diff scores for mrs where member was the only committer + sum of commit diff scores within mrs where more than one member committed.',
            },
            {
              name: 'Commit score',
              value: member.commitScore,
              description: 'Sum of commit scores for selected date range',
            },
            {
              name: 'Total commits',
              value: member.numCommits,
              description: 'Number of commits made',
            },
            {
              name: 'Lines of code',
              value: member.numAdditions + member.numDeletions,
              description:
                'Number of additions plus deletions in all merge requests in the date range.',
            },
            {
              name: 'Number of comments',
              value: member.numComments,
            },
            {
              name: 'Comments word count',
              value: member.wordCount,
              description: 'Sum of words in all comments',
            },
          ],
        })
      } else {
        setError(new Error(`could not load member's data`))
      }
    },
    [project, memberId]
  )

  return (
    <div className={styles.container}>
      <Suspense
        fallback={`Analyzing ${data?.displayName} . . . `}
        error={error?.message ?? 'Unknown Error'}
      >
        <ActivityGraph
          graphTitle={`${data?.displayName}'s Summary`}
          mergeRequests={data?.mergeRequests}
        />
      </Suspense>

      {data?.stats && <StatSummary statData={data.stats} />}
    </div>
  )
}

export default MemberSummary
