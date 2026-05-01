import React,{useEffect,useState}from'react'
import{useApp}from'../context/AppContext'
import{useResults}from'../hooks/useResults'
import Button from'../components/common/Button'
import Loader from'../components/common/Loader'
const C=['#2f5d50','#4a7a60','#6a8c74','#3a6b5c','#527a65','#254d42']
export default function ResultsPage(){
  const{selectedProject,lastLogSubmitted}=useApp()
  const{results,history,loading,fetchResults,fetchHistory}=useResults()
  const[tab,setTab]=useState('results')

  useEffect(()=>{
    fetchResults()
    fetchHistory(20)
  },[fetchResults,fetchHistory,lastLogSubmitted])

  const total=results.reduce((s,r)=>s+parseFloat(r.percentage||'0'),0).toFixed(2)
  const balanced=parseFloat(total)>=99.95
  return<div>
    <div className="page-header"><h2>Results & History</h2><p>Updates automatically when logs are submitted.</p></div>
    {!selectedProject?<div className="banner banner--info"><span>↑</span><span>Select a project from the top bar.</span></div>:loading?<Loader center size="lg"/>:
      <>
        <div style={{display:'flex',gap:4,marginBottom:16}}>
          {['results','history'].map(t=><button key={t} onClick={()=>setTab(t)} style={{padding:'7px 16px',borderRadius:'var(--r)',border:'1.5px solid',fontSize:'.82rem',fontWeight:600,cursor:'pointer',fontFamily:'var(--font)',background:tab===t?'var(--primary)':'var(--surface)',color:tab===t?'#fff':'var(--text-2)',borderColor:tab===t?'var(--primary)':'var(--border)'}}>
            {t==='results'?'Contribution Results':'Recalculation History'}
          </button>)}
          <Button variant="ghost" size="sm" onClick={()=>{fetchResults();fetchHistory(20)}} style={{marginLeft:'auto'}}>↻ Refresh</Button>
        </div>
        {tab==='results'&&<div className="card">
          <div className="card-header"><h3>Breakdown — {selectedProject.projectName}</h3><span className={`badge badge--${balanced?'active':'warning'}`}>Total: {total}%</span></div>
          {results.length===0?<div className="empty-state"><div className="empty-state__icon">◑</div><p>No results yet. Submit a contribution log to see results here.</p></div>:
            <div style={{display:'flex',flexDirection:'column',gap:20,paddingTop:4}}>
              {results.map((r,i)=>{const c=C[i%C.length];const pct=parseFloat(r.percentage||'0');return<div key={i}>
                <div style={{display:'flex',alignItems:'center',gap:11,marginBottom:8}}>
                  <div style={{width:30,height:30,borderRadius:'50%',background:c,display:'flex',alignItems:'center',justifyContent:'center',color:'#fff',fontSize:'.7rem',fontWeight:700,flexShrink:0}}>{r.contributorName.charAt(0).toUpperCase()}</div>
                  <span className="fw-600" style={{flex:1,fontSize:'.9rem'}}>{r.contributorName}</span>
                  <span className="mono fw-600 text-small" style={{color:c}}>{r.percentage}%</span>
                </div>
                <div className="progress-bar"><div className="progress-bar__fill" style={{width:`${Math.min(pct,100)}%`,background:c}}/></div>
              </div>})}
            </div>}
        </div>}
        {tab==='history'&&<div className="card">
          <div className="card-header"><h3>Recalculation Audit Log</h3><span className="badge badge--neutral">{history.length} records</span></div>
          {history.length===0?<div className="empty-state"><p>No recalculations yet.</p></div>:
            <div className="table-wrap"><table className="data-table">
              <thead><tr><th>#</th><th>Triggered At</th><th>Total Weight</th><th>Duration</th></tr></thead>
              <tbody>{history.map((h,i)=><tr key={i}><td className="text-muted text-small">{history.length-i}</td><td className="mono text-small">{h.triggeredAt?.replace('T',' ')}</td><td className="mono text-small">{h.totalProjectWeight}</td><td><span className={`badge badge--${h.executionTimeMs<=200?'active':h.executionTimeMs<=500?'warning':'error'}`}>{h.executionTimeMs} ms</span></td></tr>)}</tbody>
            </table></div>}
        </div>}
      </>}
  </div>
}
