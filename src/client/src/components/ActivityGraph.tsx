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
import { ICommitData, IMergeData } from '../types'

import { onError } from '../utils/suspenseDefaults'
import { useContext, useEffect, useState } from 'react'
import { UserConfigContext } from '../context/UserConfigContext'

export interface IActivityData {
  commits: ICommitData[]
  merges: IMergeData[]
}

export interface IActivityGraphProps {
  mergeUrl: string
  commitUrl: string
}

export type TGraphData = {
  date: string
  time: number
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
      time: number
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
        time: 0,
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
      graphObj[date].time = time
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
    graphObj[date].time = oldestCommit

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

const ActivityGraph = ({ mergeUrl, commitUrl }: IActivityGraphProps) => {
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

  const { userConfigs } = useContext(UserConfigContext)
  const [selectedRange, setSelectedRange] = useState(data?.slice(-7))
  const { yAxis } = userConfigs.selected

  useEffect(() => {
    const {
      startDate = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
      endDate = new Date(),
    } = userConfigs.selected

    console.log(startDate)

    const range = data?.filter(
      date => date.time >= startDate.getTime() && date.time <= endDate.getTime()
    )

    setSelectedRange(range?.length !== 0 ? range : data?.slice(-1))
  }, [userConfigs.selected.startDate, userConfigs.selected.endDate, data])

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
                yAxis === 'NUMBER'
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
            dataKey={yAxis === 'NUMBER' ? 'merges' : 'mergeScore'}
            fill="var(--color-secondary)"
          />
          <Bar
            name="Commits"
            dataKey={yAxis === 'NUMBER' ? 'commits' : 'commitScore'}
            fill="var(--color-primary)"
          />
        </BarChart>
      </ResponsiveContainer>
    </Suspense>
  )
}

export default ActivityGraph
