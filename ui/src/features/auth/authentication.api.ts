import {
  AuthenticateRequest,
  AuthenticateResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  User,
  axiosInstance,
  authAxiosInstance,
  handleAxiosResponse,
} from "@/common"

export const authenticate = async (
  request: AuthenticateRequest,
): Promise<AuthenticateResponse> =>
  axiosInstance.post(`/auth`, request).then(handleAxiosResponse)

export const refresh = async (
  request: RefreshTokenRequest,
): Promise<RefreshTokenResponse> =>
  axiosInstance.post(`/auth/refresh`, request).then(handleAxiosResponse)

export const fetchCurrentUser = async (): Promise<User> =>
  authAxiosInstance.get(`/auth/me`).then(handleAxiosResponse)
