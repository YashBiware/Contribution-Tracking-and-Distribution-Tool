import apiClient from'./apiClient'
export const analyticsApi={
  getDistribution:(pid)=>apiClient.get(`/projects/${pid}/analytics/distribution`),
  getPerformance:(pid)=>apiClient.get(`/projects/${pid}/analytics/performance`),
  getSummary:(pid)=>apiClient.get(`/projects/${pid}/analytics/summary`)
}
