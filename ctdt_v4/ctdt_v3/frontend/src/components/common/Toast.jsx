import React,{useState,useEffect,useCallback,useRef}from'react'
import'./Toast.css'
export function useToast(){
  const[toasts,setToasts]=useState([])
  const timers=useRef({})
  const remove=useCallback((id)=>{setToasts(p=>p.filter(t=>t.id!==id));clearTimeout(timers.current[id])},[])
  const add=useCallback((message,type='success')=>{
    const id=Date.now()+Math.random(); setToasts(p=>[...p,{id,message,type}])
    timers.current[id]=setTimeout(()=>remove(id),4500)
  },[remove])
  useEffect(()=>()=>Object.values(timers.current).forEach(clearTimeout),[])
  return{toasts,addToast:add,removeToast:remove}
}
export function ToastContainer({toasts,removeToast}){
  return<div className="toast-container" aria-live="polite">
    {toasts.map(t=><div key={t.id} className={`toast toast--${t.type}`} role="alert">
      <span className="toast__icon" aria-hidden="true">{{success:'✓',error:'✕',info:'ℹ'}[t.type]}</span>
      <span className="toast__message">{t.message}</span>
      <button className="toast__close" onClick={()=>removeToast(t.id)}>×</button>
    </div>)}
  </div>
}
