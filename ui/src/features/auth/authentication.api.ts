import { QueryClient } from "@tanstack/react-query"

import {
  AuthenticateRequest,
  AuthenticateResponse,
  AuthenticationBackendResponse,
  RefreshTokenBackendResponse,
  RefreshTokenRequest,
  authAxiosInstance,
  handleAxiosError,
  handleAxiosResponse,
} from "@/common"

const authenticateKey = "authenticate"
export const authenticate = async (
  queryClient: QueryClient,
  request: AuthenticateRequest,
): Promise<AuthenticationBackendResponse> => {
  const queryFn = () =>
    authAxiosInstance
      .post<AuthenticateResponse>(`/api/auth`, request)
      .then(handleAxiosResponse)
      .catch(handleAxiosError)

  return queryClient.fetchQuery({
    queryKey: [authenticateKey, request],
    queryFn,
  })
}

const refreshTokenKey = "refreshToken"
export const refresh = async (
  queryClient: QueryClient,
  request: RefreshTokenRequest,
): Promise<RefreshTokenBackendResponse> => {
  const queryFn = () =>
    authAxiosInstance
      .post<RefreshTokenBackendResponse>(`/api/auth/refresh`, request)
      .then(handleAxiosResponse)
      .catch(handleAxiosError)

  return queryClient.fetchQuery({
    queryKey: [refreshTokenKey, request],
    queryFn,
  })
}
