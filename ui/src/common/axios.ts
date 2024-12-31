import axios, { AxiosError, AxiosResponse } from "axios"
import { P, match } from "ts-pattern"

import { BackendError, BackendErrors } from "@/common"

export const authAxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 2000,
})

export function handleAxiosResponse<T>(response: AxiosResponse<T, unknown>): T {
  return response.data as T
}

export function handleAxiosError(
  error: AxiosError<BackendErrors<BackendError>, unknown>,
): BackendErrors<BackendError> {
  let data = error.response?.data

  return match(data)
    .with({ errors: P.array(P.string) }, errors => errors)
    .otherwise(() => ({ errors: ["UNKNOWN_ERROR"] }))
}
