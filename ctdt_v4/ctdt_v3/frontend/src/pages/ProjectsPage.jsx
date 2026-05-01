import React,{useState}from'react'
import{useApp}from'../context/AppContext'
import{useProjects}from'../hooks/useProjects'
import{projectApi}from'../api/projectApi'
import Button from'../components/common/Button'
import Input from'../components/common/Input'
import Loader from'../components/common/Loader'

async function pickFolder(){
  if(!window.showDirectoryPicker)return prompt('Paste your project folder path (e.g. C:\\\\Projects\\\\MyApp):')
  try{const h=await window.showDirectoryPicker({mode:'read'});return h.name}catch{return null}
}

export default function ProjectsPage(){
  const{selectedProject,setSelectedProject,toast}=useApp()
  const{projects,loading,createProject,refresh,removeProject}=useProjects()
  const[name,setName]=useState('')
  const[dir,setDir]=useState('')
  const[nameErr,setNameErr]=useState('')
  const[creating,setCreating]=useState(false)
  const[joinToken,setJoinToken]=useState('')
  const[joining,setJoining]=useState(false)
  const[deletingId,setDeletingId]=useState(null)
  const[copiedId,setCopiedId]=useState(null)

  async function handlePickFolder(){
    const path=await pickFolder()
    if(path){setDir(path);if(!name)setName(path.split(/[/\\]/).pop()||path)}
  }

  async function handleCreate(e){
    e.preventDefault()
    const t=name.trim()
    if(!t){setNameErr('Project name is required.');return}
    setNameErr('');setCreating(true)
    try{
      const p=await createProject(t,dir||null)
      setName('');setDir('')
      toast.addToast(`Project "${p.projectName}" created. Share its Join Code with contributors.`,'success')
    }catch(err){toast.addToast(err.message,'error')}
    finally{setCreating(false)}
  }

  async function handleJoin(e){
    e.preventDefault()
    const t=joinToken.trim()
    if(!t){toast.addToast('Paste a Join Code first.','error');return}
    setJoining(true)
    try{
      const p=await projectApi.resolveJoinToken(t)
      setSelectedProject(p)
      if(!projects.find(x=>x.projectId===p.projectId))await refresh()
      setJoinToken('')
      toast.addToast(`Switched to project "${p.projectName}".`,'success')
    }catch{toast.addToast('Invalid Join Code. Check the code and try again.','error')}
    finally{setJoining(false)}
  }

  async function handleDelete(p){
    if(!window.confirm(`Delete project "${p.projectName}"?\n\nThis will permanently remove ALL contributors, logs, and results for this project. This cannot be undone.`))return
    setDeletingId(p.projectId)
    try{
      await projectApi.deleteProject(p.projectId)
      removeProject(p.projectId)
      if(selectedProject?.projectId===p.projectId)setSelectedProject(null)
      toast.addToast(`Project "${p.projectName}" deleted.`,'success')
    }catch(err){toast.addToast(err.message,'error')}
    finally{setDeletingId(null)}
  }

  function handleCopyToken(p){
    navigator.clipboard.writeText(p.joinToken||'').then(()=>{
      setCopiedId(p.projectId)
      setTimeout(()=>setCopiedId(null),2000)
    })
  }

  return<div>
    <div className="page-header"><h2>Projects</h2><p>Create projects and share the Join Code with contributors.</p></div>
    <div className="two-col">
      <div style={{display:'flex',flexDirection:'column',gap:14}}>

        {/* Create new project */}
        <div className="card">
          <div className="card-header"><h3>Create New Project</h3></div>
          <form onSubmit={handleCreate} noValidate>
            <div className="form-group">
              <Input label="Project Name" value={name} onChange={e=>{setName(e.target.value);setNameErr('')}}
                placeholder="e.g. Team Alpha OS Project" error={nameErr} disabled={creating} autoFocus/>
            </div>
            <div className="form-group">
              <label>Project Folder <span style={{color:'var(--text-muted)',fontWeight:400}}>(optional)</span></label>
              <div style={{display:'flex',gap:8}}>
                <input className="input-field" style={{flex:1}} value={dir}
                  onChange={e=>setDir(e.target.value)} placeholder="e.g. C:\Projects\MyApp" disabled={creating}/>
                <Button type="button" variant="ghost" size="sm" onClick={handlePickFolder} disabled={creating}>📁 Browse</Button>
              </div>
              {dir&&<span style={{fontSize:'.75rem',color:'var(--text-muted)',marginTop:4,display:'block'}}>📂 {dir}</span>}
            </div>
            <Button type="submit" variant="primary" size="md" loading={creating} block>+ Create Project</Button>
          </form>
        </div>

        {/* Join existing project by token */}
        <div className="card">
          <div className="card-header"><h3>Open Project by Join Code</h3></div>
          <p style={{fontSize:'.855rem',marginBottom:14}}>Paste the Join Code shared by the project admin to switch to that project.</p>
          <form onSubmit={handleJoin} noValidate>
            <div className="form-group">
              <Input label="Join Code" value={joinToken} onChange={e=>setJoinToken(e.target.value)}
                placeholder="e.g. a3f8c2d1-…" disabled={joining}/>
            </div>
            <Button type="submit" variant="outline" size="md" loading={joining} block>🔗 Open Project</Button>
          </form>
        </div>
      </div>

      {/* Projects list */}
      <div className="card">
        <div className="card-header">
          <h3>All Projects</h3>
          <div style={{display:'flex',gap:8,alignItems:'center'}}>
            <span className="badge badge--neutral">{projects.length}</span>
            <Button variant="ghost" size="sm" onClick={refresh}>↻</Button>
          </div>
        </div>
        {loading?<Loader center label="Loading…"/>:projects.length===0
          ?<div className="empty-state"><div className="empty-state__icon">▣</div><p>No projects yet.</p></div>
          :<div className="table-wrap"><table className="data-table">
            <thead><tr><th>ID</th><th>Name</th><th>Status</th><th>Join Code</th><th></th></tr></thead>
            <tbody>{projects.map(p=><tr key={p.projectId}
              className={`row--clickable${selectedProject?.projectId===p.projectId?' row--selected':''}`}
              onClick={()=>setSelectedProject(p)}>
              <td><span className="mono text-muted text-small">#{p.projectId}</span></td>
              <td className="fw-600" style={{color:'var(--text)'}}>{p.projectName}
                {p.directoryPath&&<div className="mono text-small text-muted" style={{maxWidth:160,overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap'}}>📂 {p.directoryPath}</div>}
              </td>
              <td><span className={`badge badge--${p.status==='ACTIVE'?'active':'neutral'}`}>{p.status}</span></td>
              <td onClick={e=>e.stopPropagation()}>
                <Button variant="ghost" size="sm" onClick={()=>handleCopyToken(p)}
                  title={p.joinToken||''} style={{fontSize:'.72rem',padding:'3px 8px'}}>
                  {copiedId===p.projectId?'✓ Copied':'📋 Copy'}
                </Button>
              </td>
              <td onClick={e=>e.stopPropagation()}>
                <Button variant="ghost" size="sm" loading={deletingId===p.projectId}
                  onClick={()=>handleDelete(p)}
                  style={{color:'var(--error,#d44)',fontSize:'.8rem',padding:'3px 8px'}}>
                  🗑
                </Button>
              </td>
            </tr>)}</tbody>
          </table></div>}
      </div>
    </div>
  </div>
}
