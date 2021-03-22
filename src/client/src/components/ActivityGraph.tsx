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
  ReferenceLine,
  CartesianGrid,
} from 'recharts'
import Chart from 'react-apexcharts'
import { ICommitData, IMergeData } from '../types'

import { onError } from '../utils/suspenseDefaults'
import { useContext, useEffect, useState } from 'react'
import { UserConfigContext } from '../context/UserConfigContext'

import styles from '../css/ActivityGraph.module.css'

export interface IActivityData {
  commits: ICommitData[]
  merges: IMergeData[]
}

export interface IActivityGraphProps {
  mergeUrl: string
  commitUrl: string
  graphTitle?: string
}

export type TGraphData = {
  date: string
  time: number
  commits: number
  commitScore: number
  merges: number
  mergeScore: number
}[]

//const dateRegex = /\d{4}-\d{2}-\d{2}/

const epochToDate = (epoch: number) =>
  //new Date(epoch).toISOString().match(dateRegex)?.[0] ?? 'none'
  new Date(epoch).toDateString().slice(4, 10) ?? 'none'

const computeGraphData = (
  commitData: ICommitData[],
  mergeData: IMergeData[]
): TGraphData => {
  console.log(commitData)
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

  oldestCommit = Math.min(oldestCommit, Date.now() - 100 * 24 * 60 * 60 * 1000)

  fillObj(commitData, 'commits', 'commitScore')
  fillObj(mergeData, 'merges', 'mergeScore')

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

  console.log(graphData.map(data => data.date))

  return graphData
}

const ActivityGraph = ({
  mergeUrl,
  commitUrl,
  graphTitle,
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

  const { userConfigs } = useContext(UserConfigContext)
  const [selectedRange, setSelectedRange] = useState(data)
  const { yAxis } = userConfigs.selected

  const graphConfig = {
    options: {
      chart: {
        id: 'basic-bar',
        stacked: true,
        toolbar: {
          show: true,
          tools: {
            download: true,
            selection: true,
            zoom: true,
            zoomin: true,
            zoomout: true,
            pan: true,
          },
          redrawOnParentResize: true,
          redrawOnWindowResize: true,
          fontFamily: 'Poppins, sans-serif',
        },
      },
      title: {
        text: graphTitle ?? '',
        align: 'left',
        margin: 10,
        offsetX: 0,
        offsetY: 0,
        floating: false,
        style: {
          fontSize: '14px',
          fontWeight: 'bold',
          fontFamily: 'Poppins',
          color: '#000000',
        },
      },
      responsive: [
        {
          breakpoint: 480,
          options: {
            width: 500,
          },
        },
      ],
      plotOptions: {
        bar: {
          horizontal: false,
          columnWidth: '70%',
        },
      },
      dataLabels: {
        enabled: false,
      },
      colors: ['#ffa94d', '#364fc7'],
      xaxis: {
        categories: selectedRange?.map(data => data.date),
        title: {
          text: 'Date',
        },
        tickPlacement: 'on',
      },
      yaxis: {
        title: {
          text:
            yAxis === 'NUMBER'
              ? 'Number of Commits/Merge Requests'
              : 'Score of Commits/Merge Requests',
        },
      },
      legend: {
        position: 'bottom',
        horizontalAlign: 'left',
      },
      animations: {
        enabled: true,
        easing: 'easeinout',
        speed: 700,
        animateGradually: {
          enabled: true,
          delay: 160,
        },
        dynamicAnimation: {
          enabled: true,
          speed: 500,
        },
      },
    },
    series: [
      {
        name: 'Merge requests',
        data:
          yAxis === 'NUMBER'
            ? selectedRange?.map(merge => merge.merges)
            : selectedRange?.map(merge => merge.mergeScore),
        type: 'column',
      },
      {
        name: 'Commits',
        data:
          yAxis === 'NUMBER'
            ? selectedRange?.map(commit => commit.commits)
            : selectedRange?.map(commit => commit.commitScore),
        type: 'column',
      },
    ],
  }

  useEffect(() => {
    const {
      startDate = new Date(Date.now() - 100 * 24 * 60 * 60 * 1000),
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
      <div className={styles.graphContainer}>
        <Chart
          options={graphConfig.options}
          series={graphConfig.series}
          type="bar"
          width={720}
        ></Chart>
      </div>

      {/* <ResponsiveContainer width="100%" height="100%">
        <BarChart
          width={730}
          height={250}
          data={selectedRange}
          margin={{ top: 40, left: 40, bottom: 40, right: 40 }}
          stackOffset="sign"
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
          <ReferenceLine y={0} stroke="#000" />
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
      </ResponsiveContainer> */}
    </Suspense>
  )
}

export default ActivityGraph
