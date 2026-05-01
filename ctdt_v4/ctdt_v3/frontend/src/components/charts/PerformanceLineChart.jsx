import React from'react'
import{LineChart,Line,XAxis,YAxis,CartesianGrid,Tooltip,ResponsiveContainer}from'recharts'
import'./Charts.css'
const fmt=ts=>{if(!ts)return'';const d=new Date(ts);return`${d.getMonth()+1}/${d.getDate()} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`}
function Tip({active,payload,label}){if(!active||!payload?.length)return null;return<div className="chart-tooltip"><div className="chart-tooltip__title">{label}</div><div style={{display:'flex',alignItems:'center',gap:7}}><span className="chart-tooltip__dot" style={{background:'#2f5d50'}}/><span className="chart-tooltip__label">Duration</span><span className="chart-tooltip__value">{payload[0].value} ms</span></div></div>}
export default function PerformanceLineChart({data=[],loading=false}){
  if(loading)return<div className="chart-placeholder"><div className="chart-skeleton chart-skeleton--bar"/></div>
  if(!data.length)return<div className="chart-empty"><div className="chart-empty__icon">📈</div><p>No performance history yet.</p></div>
  return<ResponsiveContainer width="100%" height={240}><LineChart data={data.map(d=>({time:fmt(d.timestamp),ms:d.executionTimeMs}))} margin={{top:4,right:16,left:0,bottom:0}}>
    <CartesianGrid strokeDasharray="3 3" stroke="var(--border)"/>
    <XAxis dataKey="time" tick={{fontSize:10,fill:'var(--text-muted)'}} tickLine={false} axisLine={{stroke:'var(--border)'}} interval="preserveStartEnd"/>
    <YAxis tick={{fontSize:10,fill:'var(--text-muted)'}} tickLine={false} axisLine={false} unit=" ms" width={52}/>
    <Tooltip content={<Tip/>}/>
    <Line type="monotone" dataKey="ms" stroke="#2f5d50" strokeWidth={2} dot={{r:3,fill:'#2f5d50',strokeWidth:0}} activeDot={{r:5}} animationDuration={600}/>
  </LineChart></ResponsiveContainer>
}
