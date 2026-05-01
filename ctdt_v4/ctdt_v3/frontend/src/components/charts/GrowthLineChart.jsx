import React from'react'
import{AreaChart,Area,XAxis,YAxis,CartesianGrid,Tooltip,ResponsiveContainer}from'recharts'
import'./Charts.css'
const fmt=ts=>{if(!ts)return'';const d=new Date(ts);return`${d.getMonth()+1}/${d.getDate()} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`}
function Tip({active,payload}){if(!active||!payload?.length)return null;return<div className="chart-tooltip"><div style={{display:'flex',alignItems:'center',gap:7}}><span className="chart-tooltip__dot" style={{background:'#4a7a60'}}/><span className="chart-tooltip__label">Total Weight</span><span className="chart-tooltip__value">{Number(payload[0].value).toFixed(2)}</span></div></div>}
export default function GrowthLineChart({data=[],loading=false}){
  if(loading)return<div className="chart-placeholder"><div className="chart-skeleton chart-skeleton--bar"/></div>
  if(!data.length)return<div className="chart-empty"><div className="chart-empty__icon">📊</div><p>No growth data yet.</p></div>
  return<ResponsiveContainer width="100%" height={240}><AreaChart data={data.map(d=>({time:fmt(d.timestamp),weight:parseFloat(d.totalProjectWeight)}))} margin={{top:4,right:16,left:0,bottom:0}}>
    <defs><linearGradient id="wGrad" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#4a7a60" stopOpacity={0.18}/><stop offset="95%" stopColor="#4a7a60" stopOpacity={0}/></linearGradient></defs>
    <CartesianGrid strokeDasharray="3 3" stroke="var(--border)"/>
    <XAxis dataKey="time" tick={{fontSize:10,fill:'var(--text-muted)'}} tickLine={false} axisLine={{stroke:'var(--border)'}} interval="preserveStartEnd"/>
    <YAxis tick={{fontSize:10,fill:'var(--text-muted)'}} tickLine={false} axisLine={false} width={58}/>
    <Tooltip content={<Tip/>}/>
    <Area type="monotone" dataKey="weight" stroke="#4a7a60" strokeWidth={2} fill="url(#wGrad)" dot={{r:3,fill:'#4a7a60',strokeWidth:0}} activeDot={{r:5}} animationDuration={600}/>
  </AreaChart></ResponsiveContainer>
}
