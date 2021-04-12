import useSuspense from '../utils/useSuspense'
import { round } from 'lodash'
import { ICommitData, IMergeData } from '../types'
import { TCommits, TMergeRequests } from '../context/ProjectContext'
import { useContext, useEffect, useState } from 'react'
import { UserConfigContext } from '../context/UserConfigContext'

import {
  BarChart,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  Bar,
  ResponsiveContainer,
  ReferenceLine,
  CartesianGrid,
} from 'recharts'

import styles from '../css/ActivityGraph.module.css'

export interface IActivityData {
  commits: ICommitData[]
  merges: IMergeData[]
}

export type TGraphData = {
  date: string
  time: number
  commits: number
  commitScore: number
  merges: number
  mergeScore: number
}[]

const epochToDate = (epoch: number) =>
  new Date(epoch).toDateString().slice(4, 10) ?? 'none'

const computeGraphData = (mergeData: TMergeRequests): TGraphData => {
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
    data: TCommits | TMergeRequests,
    field: 'commits' | 'merges',
    fieldScore: 'commitScore' | 'mergeScore'
  ) => {
    Object.values(data).forEach(
      ({ time, score }: { time: number; score: number }) => {
        oldestCommit = Math.min(oldestCommit, time)
        const date = epochToDate(time)
        fillIfMissing(date)
        graphObj[date].time = time
        graphObj[date][fieldScore] += score
        graphObj[date][field] += 1
      }
    )
  }

  oldestCommit = Math.min(oldestCommit, Date.now() - 60 * 24 * 60 * 60 * 1000)

  fillObj(mergeData, 'merges', 'mergeScore')
  Object.values(mergeData).forEach(mr => {
    fillObj(mr.commits, 'commits', 'commitScore')
  })

  Object.entries(graphObj).forEach(
    ([key, { commitScore, mergeScore, merges }]) => {
      graphObj[key].commitScore = round(commitScore, 2)
      graphObj[key].mergeScore = round(mergeScore * -1, 2)
      graphObj[key].merges = merges * -1
    }
  )

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

export interface IActivityGraphProps {
  graphTitle: string
  mergeRequests?: TMergeRequests
}

const ActivityGraph = ({ graphTitle, mergeRequests }: IActivityGraphProps) => {
  const { Suspense, data, error } = useSuspense<TGraphData>(
    setData => mergeRequests && setData(computeGraphData(mergeRequests)),
    [mergeRequests]
  )
  const { userConfigs } = useContext(UserConfigContext)
  const [selectedRange, setSelectedRange] = useState(data)
  const { yAxis } = userConfigs.selected

  useEffect(() => {
    const {
      startDate = new Date(Date.now() - 60 * 24 * 60 * 60 * 1000),
      endDate = new Date(),
    } = userConfigs.selected

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
      <h1 className={styles.graphTitle}>{graphTitle}</h1>
      <ResponsiveContainer width="100%" height="90%">
        <BarChart
          width={730}
          height={200}
          data={selectedRange}
          margin={{ top: 10, left: 40, bottom: 20, right: 40 }}
          stackOffset="sign"
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey="date"
            style={{
              font: 'var(--font-body-large-mulish)',
            }}
          />
          <YAxis
            label={{
              value:
                yAxis === 'NUMBER'
                  ? 'Number of Commits/Merge Requests'
                  : 'Score of Commits/Merge Requests',
              angle: -90,
              viewBox: { x: -60, y: 150, width: 200, height: 200 },
              style: {
                font: 'var(--font-body-large-mulish)',
              },
            }}
            style={{
              font: 'var(--font-body-large-mulish)',
            }}
          />
          <Tooltip
            wrapperStyle={{
              font: 'var(--font-body-large-mulish)',
            }}
          />
          <Legend
            align="right"
            verticalAlign="top"
            layout="horizontal"
            wrapperStyle={{ font: 'var(--font-body-large-mulish)' }}
          />
          <ReferenceLine y={0} stroke="#000000" />
          <Bar
            name="Merge Requests"
            dataKey={yAxis === 'NUMBER' ? 'merges' : 'mergeScore'}
            fill="var(--color-secondary)"
            stackId="stack"
          />
          <Bar
            name="Commits"
            dataKey={yAxis === 'NUMBER' ? 'commits' : 'commitScore'}
            fill="var(--color-primary)"
            stackId="stack"
          />
        </BarChart>
      </ResponsiveContainer>
    </Suspense>
  )
}

export default ActivityGraph
