import apiClient from'./apiClient'
export const resultApi={
  getByProject:(pid)=>apiClient.get(`/projects/${pid}/results`),
  recalculate:(pid)=>apiClient.post(`/projects/${pid}/recalculate`),
  getHistory:(pid,lim)=>apiClient.get(`/projects/${pid}/recalculation-history${lim?'?limit='+lim:''}`)
}
