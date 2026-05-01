import React from'react'
import{Routes,Route,Navigate}from'react-router-dom'
import{AppProvider}from'./context/AppContext'
import Layout from'./components/layout/Layout'
import LoginPage from'./pages/LoginPage'
import ProjectsPage from'./pages/ProjectsPage'
import ContributorsPage from'./pages/ContributorsPage'
import ResultsPage from'./pages/ResultsPage'
import AnalyticsPage from'./pages/AnalyticsPage'
export default function App(){
  return(<AppProvider><Routes>
    <Route path="/" element={<Navigate to="/dashboard" replace/>}/>
    <Route path="/login" element={<LoginPage/>}/>
    <Route path="/dashboard" element={<Layout/>}>
      <Route index element={<Navigate to="projects" replace/>}/>
      <Route path="projects"     element={<ProjectsPage/>}/>
      <Route path="contributors" element={<ContributorsPage/>}/>
      <Route path="results"      element={<ResultsPage/>}/>
      <Route path="analytics"    element={<AnalyticsPage/>}/>
    </Route>
    <Route path="*" element={<Navigate to="/login" replace/>}/>
  </Routes></AppProvider>)
}
