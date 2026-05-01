import apiClient from'./apiClient'
export const projectApi={
  getAll:()=>apiClient.get('/projects'),
  create:(d)=>apiClient.post('/projects',d),
  findByPath:(path)=>apiClient.get('/projects/find-by-path',{params:{path}}),
  resolveJoinToken:(token)=>apiClient.get(`/projects/join/${encodeURIComponent(token)}`),
  deleteProject:(pid)=>apiClient.delete(`/projects/${pid}`),
  getFiles:(pid)=>apiClient.get(`/projects/${pid}/files`),
}
