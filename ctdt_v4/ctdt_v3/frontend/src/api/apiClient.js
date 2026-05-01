import axios from'axios'
const apiClient=axios.create({baseURL:import.meta.env.VITE_API_BASE,headers:{'Content-Type':'application/json'},timeout:12000})
apiClient.interceptors.response.use(res=>res.data,err=>{
  const message=err.response?.data?.message||err.message||'Unexpected error.'
  const status=err.response?.status??0
  return Promise.reject({message,status})
})
export default apiClient
