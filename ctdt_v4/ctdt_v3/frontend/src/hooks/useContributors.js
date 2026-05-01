import{useState,useEffect,useCallback}from'react'
import{contributorApi}from'../api/contributorApi'
import{useApp}from'../context/AppContext'
export function useContributors(){
  const{selectedProject,toast}=useApp()
  const[contributors,setContributors]=useState([])
  const[loading,setLoading]=useState(false)
  const refresh=useCallback(async()=>{
    if(!selectedProject){setContributors([]);return}
    setLoading(true)
    try{setContributors(await contributorApi.getByProject(selectedProject.projectId))}
    catch(e){toast.addToast(e.message,'error')}
    finally{setLoading(false)}
  },[selectedProject?.projectId])
  useEffect(()=>{refresh()},[refresh])
  const addContributor=useCallback(async(name,username,password)=>{
    if(!selectedProject)throw new Error('No project selected.')
    const c=await contributorApi.add(selectedProject.projectId,{contributorName:name,username,password})
    setContributors(p=>[...p,c]); return c
  },[selectedProject?.projectId])
  return{contributors,loading,refresh,addContributor}
}
