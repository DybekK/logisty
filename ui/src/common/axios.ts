import axios, { AxiosResponse } from "axios"

export const authAxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 2000,
})

export function handleAxiosResponse<T>(response: AxiosResponse<T, unknown>): T {
  return response.data as T
}
