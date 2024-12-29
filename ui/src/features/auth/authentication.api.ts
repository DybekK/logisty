import { QueryClient } from "@tanstack/react-query"

import axios from "axios"

import {
  AuthenticateRequest,
  AuthenticateResponse,
  AuthenticationBackendResponse,
  RefreshTokenBackendResponse,
  RefreshTokenRequest,
  handleAxiosError,
  handleAxiosResponse,
} from "@/common"

const { VITE_BACKEND_URL } = import.meta.env

const DEFAULT_TIMEOUT = 2000

const authenticateKey = "authenticate"
export const authenticate = async (
  queryClient: QueryClient,
  request: AuthenticateRequest,
): Promise<AuthenticationBackendResponse> => {
  const queryFn = () =>
    axios
      .post<AuthenticateResponse>(`${VITE_BACKEND_URL}/api/auth`, request, {
        timeout: DEFAULT_TIMEOUT,
      })
      .then(handleAxiosResponse)
      .catch(handleAxiosError)

  return queryClient.fetchQuery({
    queryKey: [authenticateKey, request],
    queryFn,
  })
}

const refreshTokenKey = "refreshToken"
export const refreshToken = async (
  queryClient: QueryClient,
  request: RefreshTokenRequest,
): Promise<RefreshTokenBackendResponse> => {
  const queryFn = () =>
    axios
      .post<RefreshTokenBackendResponse>(
        `${VITE_BACKEND_URL}/api/auth/refresh`,
        request,
        { timeout: DEFAULT_TIMEOUT },
      )
      .then(handleAxiosResponse)
      .catch(handleAxiosError)

  return queryClient.fetchQuery({
    queryKey: [refreshTokenKey, request],
    queryFn,
  })
}
