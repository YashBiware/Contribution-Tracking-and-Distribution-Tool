import React from'react'
import{useLocation}from'react-router-dom'
import{useApp}from'../../context/AppContext'
import{useResults}from'../../hooks/useResults'
import Button from'../common/Button'
const LABELS={projects:'Projects',contributors:'Contributors',results:'Results & History',analytics:'Analytics'}
export default function Topbar({onMenuClick}){
  const loc=useLocation()
  const label=LABELS[loc.pathname.split('/').pop()]??'Dashboard'
  const{projects,selectedProject,setSelectedProject,lastRecalculated}=useApp()
  const{recalcLoading,triggerRecalc}=useResults()
  const time=lastRecalculated?new Date(lastRecalculated).toLocaleTimeString([],{hour:'2-digit',minute:'2-digit',second:'2-digit'}):null
  return<header className="topbar">
    <div className="topbar__left">
      <button className="hamburger-btn" onClick={onMenuClick} aria-label="Open menu"><span/><span/><span/></button>
      <span className="topbar__title">{label}</span>
    </div>
    <div className="topbar__right">
      {time&&<span className="topbar__recalc-time">Recalc: {time}</span>}
      <select className="topbar__project-select" value={selectedProject?.projectId??''} onChange={e=>setSelectedProject(projects.find(p=>p.projectId===Number(e.target.value))??null)}>
        <option value="" disabled>Select project…</option>
        {projects.map(p=><option key={p.projectId} value={p.projectId}>[{p.projectId}] {p.projectName}</option>)}
      </select>
      <Button variant="primary" size="sm" loading={recalcLoading} disabled={!selectedProject||recalcLoading} onClick={async()=>{try{await triggerRecalc()}catch{}}}>
        {recalcLoading?'Calculating…':'↻ Recalculate'}
      </Button>
    </div>
  </header>
}
