import React from'react'
import{PieChart,Pie,Cell,Tooltip,Legend,ResponsiveContainer}from'recharts'
import'./Charts.css'
const P=['#2f5d50','#4a7a60','#6a9e7f','#3d7060','#254d42','#527a65','#7aaa8f','#1e3d33']
const R=Math.PI/180
function Label({cx,cy,midAngle,innerRadius,outerRadius,percent}){
  if(percent<0.045)return null
  const r=innerRadius+(outerRadius-innerRadius)*.55
  return<text x={cx+r*Math.cos(-midAngle*R)} y={cy+r*Math.sin(-midAngle*R)} fill="#fff" textAnchor="middle" dominantBaseline="central" fontSize={11} fontWeight={600}>{`${(percent*100).toFixed(1)}%`}</text>
}
function Tip({active,payload}){
  if(!active||!payload?.length)return null; const d=payload[0]
  return<div className="chart-tooltip"><div style={{display:'flex',alignItems:'center',gap:7}}><span className="chart-tooltip__dot" style={{background:d.payload.fill}}/><span className="chart-tooltip__label">{d.name}</span><span className="chart-tooltip__value">{d.value}%</span></div></div>
}
export default function ContributionPieChart({data=[],loading=false}){
  if(loading)return<div className="chart-placeholder"><div className="chart-skeleton chart-skeleton--circle"/></div>
  if(!data.length)return<div className="chart-empty"><div className="chart-empty__icon">◑</div><p>No data yet. Run recalculation first.</p></div>
  const cd=data.map(d=>({name:d.name,value:parseFloat(d.percentage)}))
  return<ResponsiveContainer width="100%" height={300}>
    <PieChart><Pie data={cd} cx="50%" cy="50%" innerRadius={70} outerRadius={120} paddingAngle={2} dataKey="value" labelLine={false} label={Label} animationBegin={0} animationDuration={700}>
      {cd.map((_,i)=><Cell key={i} fill={P[i%P.length]} stroke="none"/>)}
    </Pie>
    <Tooltip content={<Tip/>}/>
    <Legend iconType="circle" iconSize={9} wrapperStyle={{fontSize:'0.8rem',paddingTop:12}}/>
    </PieChart>
  </ResponsiveContainer>
}
