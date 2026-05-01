import React,{useState,useEffect,useCallback}from'react'
import{useApp}from'../context/AppContext'
import{useContributors}from'../hooks/useContributors'
import{projectApi}from'../api/projectApi'
import Button from'../components/common/Button'
import Input from'../components/common/Input'
import Loader from'../components/common/Loader'

export default function ContributorsPage(){
  const{selectedProject,toast,lastLogSubmitted}=useApp()
  const{contributors,loading,addContributor}=useContributors()
  const[name,setName]=useState('')
  const[username,setUsername]=useState('')
  const[password,setPassword]=useState('')
  const[errors,setErrors]=useState({})
  const[adding,setAdding]=useState(false)
  const[files,setFiles]=useState([])
  const[filesLoading,setFilesLoading]=useState(false)
  const[expandedContributor,setExpandedContributor]=useState(null)

  const fetchFiles=useCallback(async()=>{
    if(!selectedProject)return
    setFilesLoading(true)
    try{setFiles(await projectApi.getFiles(selectedProject.projectId))}
    catch{setFiles([])}
    finally{setFilesLoading(false)}
  },[selectedProject])

  useEffect(()=>{setFiles([]);fetchFiles()},[fetchFiles])
  useEffect(()=>{if(lastLogSubmitted>0)fetchFiles()},[lastLogSubmitted,fetchFiles])

  function validate(){
    const e={}
    if(!name.trim())e.name='Name is required.'
    if(!username.trim())e.username='Username is required.'
    if(!password||password.length<4)e.password='Password must be at least 4 characters.'
    setErrors(e);return Object.keys(e).length===0
  }
  async function handleAdd(e){
    e.preventDefault()
    if(!selectedProject){toast.addToast('Select a project first.','error');return}
    if(!validate())return
    setAdding(true)
    try{
      const c=await addContributor(name.trim(),username.trim().toLowerCase(),password)
      setName('');setUsername('');setPassword('');setErrors({})
      toast.addToast(`"${c.contributorName}" added with login @${c.username}.`,'success')
    }catch(err){toast.addToast(err.message,'error')}
    finally{setAdding(false)}
  }

  // Group files by contributor
  const filesByContributor={}
  files.forEach(f=>{
    if(!filesByContributor[f.contributorId])filesByContributor[f.contributorId]=[]
    filesByContributor[f.contributorId].push(f)
  })

  return<div>
    <div className="page-header"><h2>Contributors</h2><p>Add contributors — the agent automatically logs their file activity.</p></div>
    {!selectedProject
      ?<div className="banner banner--info"><span>↑</span><span>Select a project from the top bar first.</span></div>
      :<div style={{display:'flex',flexDirection:'column',gap:16}}>
        <div className="two-col">
          {/* Add contributor form */}
          <div className="card">
            <div className="card-header"><h3>Add Contributor</h3><span className="badge badge--primary">Project #{selectedProject.projectId}</span></div>
            <form onSubmit={handleAdd} noValidate>
              <div className="form-group">
                <Input label="Full Name" value={name} onChange={e=>{setName(e.target.value);setErrors(v=>({...v,name:undefined}))}}
                  placeholder="e.g. Yash Kumar" error={errors.name} disabled={adding} autoFocus/>
              </div>
              <div className="form-group">
                <Input label="Username" value={username} onChange={e=>{setUsername(e.target.value);setErrors(v=>({...v,username:undefined}))}}
                  placeholder="e.g. yash123" error={errors.username} disabled={adding}
                  helpText="Contributor uses this to log in to the agent."/>
              </div>
              <div className="form-group">
                <Input label="Password" type="password" value={password} onChange={e=>{setPassword(e.target.value);setErrors(v=>({...v,password:undefined}))}}
                  placeholder="Min 4 characters" error={errors.password} disabled={adding}
                  helpText="Share this with the contributor along with the Join Code."/>
              </div>
              <Button type="submit" variant="primary" size="md" loading={adding} block>+ Add Contributor</Button>
            </form>
          </div>

          {/* Contributors list */}
          <div className="card">
            <div className="card-header">
              <h3>Contributors — "{selectedProject.projectName}"</h3>
              {loading?<Loader size="sm"/>:<span className="badge badge--neutral">{contributors.length}</span>}
            </div>
            {loading?<Loader center/>:contributors.length===0
              ?<div className="empty-state"><div className="empty-state__icon">◎</div><p>No contributors yet.</p></div>
              :<div className="table-wrap"><table className="data-table">
                <thead><tr><th>ID</th><th>Name</th><th>Username</th><th>Files</th><th>Joined</th></tr></thead>
                <tbody>{contributors.map(c=>{
                  const cFiles=filesByContributor[c.contributorId]||[]
                  const isExp=expandedContributor===c.contributorId
                  return<React.Fragment key={c.contributorId}>
                    <tr className="row--clickable" onClick={()=>setExpandedContributor(isExp?null:c.contributorId)}>
                      <td><span className="mono text-muted text-small">#{c.contributorId}</span></td>
                      <td className="fw-500" style={{color:'var(--text)'}}>{c.contributorName}</td>
                      <td><span className="mono text-small badge badge--neutral">@{c.username||'—'}</span></td>
                      <td><span className="badge badge--neutral">{cFiles.length}</span></td>
                      <td className="text-muted text-small">{c.joinedAt?new Date(c.joinedAt).toLocaleDateString():'—'}</td>
                    </tr>
                    {isExp&&<tr>
                      <td colSpan={5} style={{padding:'0 0 8px 0',background:'var(--surface-2,#f7f8fa)'}}>
                        {cFiles.length===0
                          ?<div style={{padding:'10px 16px',color:'var(--text-muted)',fontSize:'.82rem'}}>No files logged yet.</div>
                          :<table className="data-table" style={{fontSize:'.8rem'}}>
                            <thead><tr><th>File</th><th>Lines</th><th>Weight</th><th>Type</th><th>✓</th><th>Logged At</th></tr></thead>
                            <tbody>{cFiles.map(f=><tr key={f.logId}>
                              <td><span className="mono text-small" title={f.fileName}>{f.fileName}</span></td>
                              <td className="text-small">{f.contentSize}</td>
                              <td><span className="badge badge--primary mono">{f.weightFactor}</span></td>
                              <td><span className={`badge badge--${f.contributionType==='ORIGINAL'?'active':'warning'}`}>{f.contributionType}</span></td>
                              <td style={{color:f.retainedFlag?'var(--success)':'var(--text-muted)'}}>{f.retainedFlag?'✓':'✕'}</td>
                              <td className="mono text-small text-muted">{f.timestamp?f.timestamp.replace('T',' ').substring(0,16):'—'}</td>
                            </tr>)}</tbody>
                          </table>}
                      </td>
                    </tr>}
                  </React.Fragment>
                })}</tbody>
              </table></div>}
          </div>
        </div>

        {/* All files panel */}
        <div className="card">
          <div className="card-header">
            <h3>All Files — {selectedProject.projectName}</h3>
            <div style={{display:'flex',gap:8,alignItems:'center'}}>
              {filesLoading?<Loader size="sm"/>:<span className="badge badge--neutral">{files.length} entries</span>}
              <Button variant="ghost" size="sm" onClick={fetchFiles}>↻</Button>
            </div>
          </div>
          {filesLoading?<Loader center label="Loading files…"/>:files.length===0
            ?<div className="empty-state"><div className="empty-state__icon">≡</div><p>No files logged yet. The agent will populate this automatically.</p></div>
            :<div className="table-wrap"><table className="data-table">
              <thead><tr><th>#</th><th>Contributor</th><th>File Name</th><th>Lines</th><th>Weight</th><th>Type</th><th>Retained</th><th>Logged At</th></tr></thead>
              <tbody>{files.map((f,i)=><tr key={f.logId}>
                <td className="text-muted text-small">{i+1}</td>
                <td className="fw-500 text-small">{f.contributorName}</td>
                <td><span className="mono text-small" title={f.fileName} style={{maxWidth:220,display:'block',overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap'}}>{f.fileName}</span></td>
                <td className="text-small">{f.contentSize}</td>
                <td><span className="badge badge--primary mono">{f.weightFactor}</span></td>
                <td><span className={`badge badge--${f.contributionType==='ORIGINAL'?'active':'warning'}`}>{f.contributionType}</span></td>
                <td style={{color:f.retainedFlag?'var(--success)':'var(--text-muted)',textAlign:'center'}}>{f.retainedFlag?'✓':'✕'}</td>
                <td className="mono text-small text-muted">{f.timestamp?f.timestamp.replace('T',' ').substring(0,16):'—'}</td>
              </tr>)}</tbody>
            </table></div>}
        </div>
      </div>}
  </div>
}
