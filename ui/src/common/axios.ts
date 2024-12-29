import axios, { AxiosError, AxiosResponse } from "axios"

import { BackendError, BackendErrors } from "@/common"

export function handleAxiosResponse<T>(response: AxiosResponse<T, unknown>): T {
  return response.data as T
}

export function handleAxiosError<E extends BackendError>(
  error: AxiosError<E, unknown>,
): BackendErrors<E> {
  if (axios.isAxiosError(error)) {
    return error.response?.data as unknown as BackendErrors<E>
  }

  throw error
}
