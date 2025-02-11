import {
  AuthenticateRequest,
  AuthenticateResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  User,
  authAxiosInstance,
  axiosInstance,
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

export const fetchCurrentUserAfterAuthentication = async (
  tokens: AuthenticateResponse,
): Promise<User> =>
  axiosInstance
    .get(`/auth/me`, {
      headers: { Authorization: `Bearer ${tokens.accessToken}` },
    })
    .then(handleAxiosResponse)

export const fetchCurrentUser = async (): Promise<User> =>
  authAxiosInstance.get(`/auth/me`).then(handleAxiosResponse)
