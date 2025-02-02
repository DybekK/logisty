import {
  AuthenticateRequest,
  AuthenticateResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  authAxiosInstance,
  handleAxiosResponse,
} from "@/common"

export const authenticate = async (
  request: AuthenticateRequest,
): Promise<AuthenticateResponse> =>
  authAxiosInstance.post(`/auth`, request).then(handleAxiosResponse)

export const refresh = async (
  request: RefreshTokenRequest,
): Promise<RefreshTokenResponse> =>
  authAxiosInstance.post(`/auth/refresh`, request).then(handleAxiosResponse)
