import{useState,useEffect,useCallback,useRef}from'react'
import{projectApi}from'../api/projectApi'
import{useApp}from'../context/AppContext'
export function useProjects(){
  const{projects,setProjects,setSelectedProject,toast}=useApp()
  const[loading,setLoading]=useState(false)
  const toastRef=useRef(toast);toastRef.current=toast
  const refresh=useCallback(async()=>{
    setLoading(true)
    try{setProjects(await projectApi.getAll())}
    catch(e){toastRef.current.addToast(e.message,'error')}
    finally{setLoading(false)}
  },[setProjects])
  useEffect(()=>{refresh()},[refresh])
  const createProject=useCallback(async(name,directoryPath)=>{
    const p=await projectApi.create({projectName:name,directoryPath:directoryPath||null})
    setProjects(prev=>[...prev,p])
    setSelectedProject(p)
    return p
  },[setProjects,setSelectedProject])
  const removeProject=useCallback((pid)=>{
    setProjects(prev=>prev.filter(p=>p.projectId!==pid))
  },[setProjects])
  return{projects,loading,refresh,createProject,removeProject}
}
