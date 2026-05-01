import React,{useState,useEffect,useCallback}from'react'
import{useApp}from'../context/AppContext'
import{analyticsApi}from'../api/analyticsApi'
import MetricCard from'../components/cards/MetricCard'
import ContributionPieChart from'../components/charts/ContributionPieChart'
import PerformanceLineChart from'../components/charts/PerformanceLineChart'
import GrowthLineChart from'../components/charts/GrowthLineChart'
import Button from'../components/common/Button'
import'./AnalyticsPage.css'
export default function AnalyticsPage(){
  const{selectedProject,toast,lastLogSubmitted}=useApp()
  const[summary,setSummary]=useState(null)
  const[dist,setDist]=useState([])
  const[perf,setPerf]=useState([])
  const[lS,setLS]=useState(false)
  const[lD,setLD]=useState(false)
  const[lP,setLP]=useState(false)
  const[refreshing,setRefreshing]=useState(false)
  const fetchAll=useCallback(async(silent=false)=>{
    if(!selectedProject)return
    const pid=selectedProject.projectId
    if(!silent){setLS(true);setLD(true);setLP(true)}
    const[sR,dR,pR]=await Promise.allSettled([analyticsApi.getSummary(pid),analyticsApi.getDistribution(pid),analyticsApi.getPerformance(pid)])
    if(sR.status==='fulfilled')setSummary(sR.value);else if(!silent)toast.addToast('Could not load summary.','error')
    setLS(false)
    if(dR.status==='fulfilled')setDist(dR.value.contributors??[]);else if(!silent)toast.addToast('Could not load distribution.','error')
    setLD(false)
    if(pR.status==='fulfilled')setPerf(pR.value.history??[]);else if(!silent)toast.addToast('Could not load performance.','error')
    setLP(false)
    setRefreshing(false)
  },[selectedProject,toast])
  useEffect(()=>{setSummary(null);setDist([]);setPerf([]);fetchAll()},[fetchAll])
  useEffect(()=>{if(lastLogSubmitted>0)fetchAll(true)},[lastLogSubmitted,fetchAll])
  const avg=perf.length>0?Math.round(perf.reduce((s,d)=>s+d.executionTimeMs,0)/perf.length):null
  if(!selectedProject)return<div>
    <div className="page-header"><h2>Analytics</h2><p>Visual insights for contribution distribution and performance.</p></div>
    <div className="banner banner--info"><span>↑</span><span>Select a project from the top bar.</span></div>
  </div>
  return<div>
    <div className="page-header analytics-page-header">
      <div><h2>Analytics</h2><p>Auto-updates on every agent submission. Project: <strong>{selectedProject.projectName}</strong></p></div>
      <Button variant="ghost" size="sm" loading={refreshing} disabled={refreshing} onClick={()=>{setRefreshing(true);fetchAll(true)}}>↻ Refresh</Button>
    </div>
    <div className="analytics-metric-grid">
      <MetricCard label="Total Contributors" value={summary?.totalContributors??null} sub="in this project" loading={lS}/>
      <MetricCard label="Total Logs" value={summary?.totalLogs??null} sub="all submissions" loading={lS}/>
      <MetricCard label="Last Recalculation" value={summary?.lastExecutionTimeMs!=null?`${summary.lastExecutionTimeMs} ms`:null} sub={avg!=null?`avg ${avg} ms over ${perf.length} runs`:'no history yet'} loading={lS} accent/>
    </div>
    <div className="analytics-charts-row">
      <div className="card analytics-chart-card">
        <div className="card-header"><h3>Contribution Distribution</h3><span className="badge badge--neutral">{dist.length} contributors</span></div>
        <ContributionPieChart data={dist} loading={lD}/>
      </div>
      <div className="card analytics-chart-card">
        <div className="card-header"><h3>Recalculation Duration</h3><span className="badge badge--neutral">{perf.length} runs</span></div>
        <PerformanceLineChart data={perf} loading={lP}/>
      </div>
    </div>
    <div className="card" style={{marginTop:16}}>
      <div className="card-header"><h3>Contribution Growth Over Time</h3></div>
      <GrowthLineChart data={perf} loading={lP}/>
    </div>
    {dist.length>0&&<div className="card" style={{marginTop:16}}>
      <div className="card-header"><h3>Distribution Breakdown</h3></div>
      <div className="table-wrap"><table className="data-table">
        <thead><tr><th>#</th><th>Contributor</th><th>Percentage</th><th>Visual</th></tr></thead>
        <tbody>{dist.map((d,i)=><tr key={d.name}><td className="text-muted text-small">{i+1}</td><td className="fw-500" style={{color:'var(--text)'}}>{d.name}</td><td><span className="mono fw-600" style={{color:'var(--primary)'}}>{d.percentage}%</span></td><td style={{width:180}}><div className="progress-bar"><div className="progress-bar__fill" style={{width:`${Math.min(parseFloat(d.percentage),100)}%`}}/></div></td></tr>)}</tbody>
      </table></div>
    </div>}
  </div>
}
