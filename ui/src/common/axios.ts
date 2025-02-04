import axios, { AxiosResponse } from "axios"

export const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 2000,
})

export const authAxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 2000,
})

authAxiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken")
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export function handleAxiosResponse<T>(response: AxiosResponse<T, unknown>): T {
  return response.data as T
}
