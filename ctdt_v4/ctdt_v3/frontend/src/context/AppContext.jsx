import React,{createContext,useContext,useState,useCallback}from'react'
import{useToast}from'../components/common/Toast'
const Ctx=createContext(null)
export function AppProvider({children}){
  const[selectedProject,setSelectedProject]=useState(null)
  const[projects,setProjects]=useState([])
  const[lastRecalculated,setLastRecalculated]=useState(null)
  const[lastLogSubmitted,setLastLogSubmitted]=useState(0)
  const toast=useToast()
  const notifyLogSubmitted=useCallback(()=>setLastLogSubmitted(n=>n+1),[])
  return<Ctx.Provider value={{
    selectedProject,setSelectedProject,
    projects,setProjects,
    lastRecalculated,setLastRecalculated,
    lastLogSubmitted,notifyLogSubmitted,
    toast
  }}>{children}</Ctx.Provider>
}
export function useApp(){return useContext(Ctx)}
