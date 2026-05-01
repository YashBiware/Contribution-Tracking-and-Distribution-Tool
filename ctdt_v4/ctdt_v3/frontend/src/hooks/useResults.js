import{useState,useCallback}from'react'
import{resultApi}from'../api/resultApi'
import{useApp}from'../context/AppContext'
export function useResults(){
  const{selectedProject,setLastRecalculated,toast}=useApp()
  const[results,setResults]=useState([])
  const[history,setHistory]=useState([])
  const[loading,setLoading]=useState(false)
  const[recalcLoading,setRecalcLoading]=useState(false)
  const fetchResults=useCallback(async()=>{
    if(!selectedProject){setResults([]);return}
    setLoading(true)
    try{setResults(await resultApi.getByProject(selectedProject.projectId))}catch(e){toast.addToast(e.message,'error')}
    finally{setLoading(false)}
  },[selectedProject,toast])
  const fetchHistory=useCallback(async(lim=20)=>{
    if(!selectedProject)return
    try{setHistory(await resultApi.getHistory(selectedProject.projectId,lim))}catch(e){toast.addToast(e.message,'error')}
  },[selectedProject,toast])
  const triggerRecalc=useCallback(async()=>{
    if(!selectedProject)throw new Error('No project selected.')
    setRecalcLoading(true)
    try{
      const resp=await resultApi.recalculate(selectedProject.projectId)
      setLastRecalculated(new Date().toISOString())
      setResults(await resultApi.getByProject(selectedProject.projectId))
      toast.addToast(`Recalculation complete — ${resp.contributorsProcessed??''} contributors updated.`,'success')
      return resp
    }catch(e){
      if(e.status===429)toast.addToast('Recalculation already running. Try again shortly.','error')
      else toast.addToast(e.message,'error')
      throw e
    }finally{setRecalcLoading(false)}
  },[selectedProject,setLastRecalculated,toast])
  return{results,history,loading,recalcLoading,fetchResults,fetchHistory,triggerRecalc}
}
