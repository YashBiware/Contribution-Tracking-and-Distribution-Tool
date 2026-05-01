import React from'react'
import{NavLink,useNavigate}from'react-router-dom'
const NAV=[
  {to:'/dashboard/projects',icon:'▣',label:'Projects'},
  {to:'/dashboard/contributors',icon:'◎',label:'Contributors'},
  {to:'/dashboard/results',icon:'◑',label:'Results & History'},
  {to:'/dashboard/analytics',icon:'◈',label:'Analytics'},
]
export default function Sidebar({mobileOpen,onClose}){
  const navigate=useNavigate()
  return<>
    {mobileOpen&&<div className="sidebar-overlay sidebar-overlay--visible" onClick={onClose} aria-hidden="true"/>}
    <aside className={`sidebar${mobileOpen?' sidebar--open':''}`}>
      <div className="sidebar__brand">
        <div className="sidebar__logo">CT</div>
        <div><div className="sidebar__brand-name">CTDT</div><div className="sidebar__brand-sub">Admin Dashboard</div></div>
      </div>
      <nav className="sidebar__nav">
        <div className="sidebar__section">Management</div>
        {NAV.map(item=><NavLink key={item.to} to={item.to} end={false} onClick={onClose}
          className={({isActive})=>`nav-item${isActive?' nav-item--active':''}`}>
          <span className="nav-item__icon">{item.icon}</span><span>{item.label}</span>
        </NavLink>)}
      </nav>
      <div className="sidebar__footer">
        <button className="nav-item" onClick={()=>{sessionStorage.removeItem('ctdt_session');navigate('/login')}}>
          <span className="nav-item__icon">⎋</span><span>Sign out</span>
        </button>
      </div>
    </aside>
  </>
}
