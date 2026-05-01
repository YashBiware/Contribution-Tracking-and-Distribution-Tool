import apiClient from'./apiClient'
export const contributorApi={getByProject:(pid)=>apiClient.get(`/projects/${pid}/contributors`),add:(pid,d)=>apiClient.post(`/projects/${pid}/contributors`,d)}
