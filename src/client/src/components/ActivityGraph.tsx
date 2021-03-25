import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { round } from 'lodash'
import Chart from 'react-apexcharts'
import { ICommitData, IMergeData } from '../types'
import { PRIMARY_COLOR, SECONDARY_COLOR } from '../utils/constants'

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

export interface ISeriesData {
  name: string
  data: number[]
  type: string
}

const epochToDate = (epoch: number) =>
  new Date(epoch).toDateString().slice(4, 10) ?? 'none'

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

  oldestCommit = Math.min(oldestCommit, Date.now() - 60 * 24 * 60 * 60 * 1000)

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

  return graphData
}

const graphConfig = {
  options: {
    chart: {
      id: 'basic-bar',
      width: '100%',
      stacked: true,
      fontFamily: 'Mulish, sans-serif',
      toolbar: {
        show: true,
        redrawOnParentResize: true,
        redrawOnWindowResize: true,
      },
    },
    title: {
      text: '',
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
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: '85%',
      },
    },
    dataLabels: {
      enabled: false,
    },
    colors: [SECONDARY_COLOR, PRIMARY_COLOR],
    xaxis: {
      categories: [''],
      title: {
        text: 'Date',
      },
      labels: {
        rotate: 0,
        rotateAlways: false,
        hideOverlappingLabels: true,
      },
      tickPlacement: 'on',
    },
    yaxis: {
      title: {
        text: '',
      },
    },
    legend: {
      position: 'top',
      horizontalAlign: 'left',
    },
    animations: {
      enabled: true,
      easing: 'easein',
      speed: 800,
    },
  },
  series: [
    {
      name: 'Merge requests',
      data: [],
      type: 'column',
    },
    {
      name: 'Commits',
      data: [],
      type: 'column',
    },
  ],
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
  const xAxisLabels: string[] = selectedRange?.map(({ date }) => date) ?? []

  console.log(selectedRange?.map(({ date }) => date))

  graphConfig.options.title.text = graphTitle ?? ''

  graphConfig.options.yaxis.title.text =
    yAxis === 'NUMBER'
      ? 'Number of Commits/Merge Requests'
      : 'Score of Commits/Merge Requests'

  const mergeRequestData: ISeriesData[] = graphConfig.series.filter(series => {
    return series.name === 'Merge requests'
  })
  mergeRequestData[0].data =
    (yAxis === 'NUMBER'
      ? selectedRange?.map(merge => merge.merges)
      : selectedRange?.map(merge => merge.mergeScore)) ?? []

  const commitData: ISeriesData[] = graphConfig.series.filter(series => {
    return series.name === 'Commits'
  })
  commitData[0].data =
    (yAxis === 'NUMBER'
      ? selectedRange?.map(commit => commit.commits)
      : selectedRange?.map(commit => commit.commitScore)) ?? []

  graphConfig.options.xaxis.categories = xAxisLabels

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
        />
      </div>
    </Suspense>
  )
}

export default ActivityGraph
