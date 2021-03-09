import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { round } from 'lodash'
import {
  BarChart,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  Bar,
  ResponsiveContainer,
} from 'recharts'

import { onError } from '../utils/suspenseDefaults'

export interface ICommitData {
  sha: string
  author: string
  time: number
  score: number
}

export interface IMergeData {
  id: string
  author: string
  time: number
  title: string
  score: number
}

export interface IActivityData {
  commits: ICommitData[]
  merges: IMergeData[]
}

export interface IActivityGraphProps {
  mergeUrl: string
  commitUrl: string
  yAxisValue?: 'score' | 'number'
}

export type TGraphData = {
  date: string
  commits: number
  commitScore: number
  merges: number
  mergeScore: number
}[]

const dateRegex = /\d{4}-\d{2}-\d{2}/

const epochToDate = (epoch: number) =>
  new Date(epoch).toISOString().match(dateRegex)?.[0] ?? 'none'

const computeGraphData = (
  commitData: ICommitData[],
  mergeData: IMergeData[]
): TGraphData => {
  const graphObj: Record<
    string,
    {
      commits: number
      commitScore: number
      merges: number
      mergeScore: number
    }
  > = {}

  let oldestCommit = Number.MAX_VALUE

  const fillIfMissing = (date: string) => {
    if (graphObj[date] === undefined) {
      graphObj[date] = {
        commits: 0,
        commitScore: 0,
        mergeScore: 0,
        merges: 0,
      }
    }
  }

  const fillObj = (
    data: ICommitData[] | IMergeData[],
    field: 'commits' | 'merges',
    fieldScore: 'commitScore' | 'mergeScore'
  ) => {
    data.forEach(({ time, score }: { time: number; score: number }) => {
      oldestCommit = Math.min(oldestCommit, time)
      const date = epochToDate(time)
      fillIfMissing(date)

      graphObj[date][fieldScore] += score
      graphObj[date][field] += 1
    })
  }

  oldestCommit = Math.min(oldestCommit, Date.now() - 7 * 24 * 60 * 60 * 1000)

  fillObj(commitData, 'commits', 'commitScore')
  fillObj(mergeData, 'merges', 'mergeScore')

  Object.entries(graphObj).forEach(([key, { commitScore, mergeScore }]) => {
    graphObj[key].commitScore = round(commitScore, 2)
    graphObj[key].mergeScore = round(mergeScore, 2)
  })

  const now = Date.now()
  while (oldestCommit <= now) {
    const date = epochToDate(oldestCommit)

    fillIfMissing(date)

    oldestCommit += 24 * 60 * 60 * 1000
  }

  const graphData = Object.entries(graphObj)
    .map(([date, values]) => {
      return {
        ...values,
        date,
      }
    })
    .sort((a, b) => (a.date < b.date ? -1 : 1))

  return graphData
}

const ActivityGraph = ({
  mergeUrl,
  commitUrl,
  yAxisValue = 'number',
}: IActivityGraphProps) => {
  const { Suspense, data, error } = useSuspense<TGraphData, Error>(
    (setData, setError) => {
      let otherData: ICommitData[] | IMergeData[] | null = null
      jsonFetcher<IMergeData[]>(mergeUrl)
        .then(data => {
          if (otherData) {
            setData(computeGraphData(otherData as ICommitData[], data))
          } else {
            otherData = data
          }
        })
        .catch(onError(setError))
      jsonFetcher<ICommitData[]>(commitUrl)
        .then(data => {
          if (otherData) {
            setData(computeGraphData(data, otherData as IMergeData[]))
          } else {
            otherData = data
          }
        })
        .catch(onError(setError))
    }
  )

  const selectedRange = data?.slice(-7)

  return (
    <Suspense
      fallback="Loading Commit Data..."
      error={error?.message ?? 'Unknown Error'}
    >
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          width={730}
          height={250}
          data={selectedRange}
          margin={{ top: 40, left: 40, bottom: 40, right: 40 }}
        >
          <XAxis dataKey="date" />
          <YAxis
            label={{
              value:
                yAxisValue === 'number'
                  ? 'Number of Commits/Merge Requests'
                  : 'Score of Commits/Merge Requests',
              angle: -90,
              viewBox: { x: -60, y: 150, width: 200, height: 200 },
            }}
          />
          <Tooltip />
          <Legend align="right" verticalAlign="top" layout="horizontal" />
          <Bar
            name="Merge Requests"
            dataKey={yAxisValue === 'number' ? 'merges' : 'mergeScore'}
            fill="var(--color-secondary)"
          />
          <Bar
            name="Commits"
            dataKey={yAxisValue === 'number' ? 'commits' : 'commitScore'}
            fill="var(--color-primary)"
          />
        </BarChart>
      </ResponsiveContainer>
    </Suspense>
  )
}

export default ActivityGraph
