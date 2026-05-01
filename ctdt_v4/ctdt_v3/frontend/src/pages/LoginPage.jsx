import React,{useState,useEffect}from'react'
import{useNavigate}from'react-router-dom'
import Button from'../components/common/Button'
import Input from'../components/common/Input'
export default function LoginPage(){
  const navigate=useNavigate()
  const[username,setUsername]=useState('')
  const[password,setPassword]=useState('')
  const[loading,setLoading]=useState(false)
  const[error,setError]=useState('')
  useEffect(()=>{if(sessionStorage.getItem('ctdt_session'))navigate('/dashboard',{replace:true})},[navigate])
  function signIn(){
    setError('')
    if(!username.trim()){setError('Username is required.');return}
    if(!password.trim()||password.length<4){setError('Password must be at least 4 characters.');return}
    setLoading(true)
    setTimeout(()=>{sessionStorage.setItem('ctdt_session','1');navigate('/dashboard',{replace:true})},600)
  }
  return<div className="login-page"><div className="login-card">
    <div className="login-brand">
      <div className="login-logo">CT</div>
      <div><div style={{fontWeight:700,fontSize:'1rem'}}>CTDT</div><div style={{fontSize:'.7rem',color:'var(--text-muted)'}}>Contribution Tracking & Distribution Tool</div></div>
    </div>
    <h2 style={{marginBottom:4}}>Admin Sign In</h2>
    <p style={{fontSize:'.875rem',marginBottom:20}}>Access the contribution management dashboard.</p>
    {error&&<div className="banner banner--error"><span>✕</span><span>{error}</span></div>}
    <form onSubmit={e=>{e.preventDefault();signIn()}} noValidate>
      <div className="form-group"><Input label="Username" type="text" value={username} onChange={e=>setUsername(e.target.value)} placeholder="admin" autoFocus disabled={loading}/></div>
      <div className="form-group"><Input label="Password" type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="••••••••" disabled={loading}/></div>
      <Button type="submit" variant="primary" size="lg" loading={loading} block style={{marginTop:8}}>Sign In</Button>
    </form>
    <div className="login-divider"><span>or</span></div>
    <Button variant="ghost" size="md" block onClick={()=>{sessionStorage.setItem('ctdt_session','1');navigate('/dashboard',{replace:true})}} disabled={loading} style={{border:'1.5px dashed var(--border)',color:'var(--text-muted)'}}>
      Bypass — Demo Mode
    </Button>
    <p className="login-footer-note">Authentication is not enforced. Demo access only.</p>
  </div></div>
}
