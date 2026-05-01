import React,{useState,useEffect}from'react'
import{Outlet,useNavigate}from'react-router-dom'
import Sidebar from'./Sidebar'
import Topbar from'./Topbar'
import{ToastContainer}from'../common/Toast'
import{useApp}from'../../context/AppContext'
import{useProjects}from'../../hooks/useProjects'
export default function Layout(){
  const navigate=useNavigate()
  const[mobileOpen,setMobileOpen]=useState(false)
  const{toast}=useApp()
  useProjects()
  useEffect(()=>{if(!sessionStorage.getItem('ctdt_session'))navigate('/login',{replace:true})},[navigate])
  return<div className="app-shell">
    <Sidebar mobileOpen={mobileOpen} onClose={()=>setMobileOpen(false)}/>
    <div className="main-area">
      <Topbar onMenuClick={()=>setMobileOpen(v=>!v)}/>
      <main className="page-content"><Outlet/></main>
    </div>
    <ToastContainer toasts={toast.toasts} removeToast={toast.removeToast}/>
  </div>
}
