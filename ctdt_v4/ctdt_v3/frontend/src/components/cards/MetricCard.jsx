import React from'react'
import'./MetricCard.css'
export default function MetricCard({label,value,sub,accent=false,loading=false}){
  return<div className={`metric-card${accent?' metric-card--accent':''}`}>
    <div className="metric-card__label">{label}</div>
    {loading?<div className="metric-card__skeleton" aria-hidden="true"/>:<>
      <div className="metric-card__value">{value??'—'}</div>
      {sub&&<div className="metric-card__sub">{sub}</div>}
    </>}
  </div>
}
