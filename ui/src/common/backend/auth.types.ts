export interface AuthenticateRequest {
  email: string
  password: string
}

export interface AuthenticateResponse {
  accessToken: string
  refreshToken: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface RefreshTokenResponse {
  accessToken: string
  refreshToken: string
}

export enum AuthenticationErrors {
  BAD_CREDENTIALS = "BAD_CREDENTIALS",
  INVALID_TOKEN_STRUCTURE = "INVALID_TOKEN_STRUCTURE",
  TOKEN_EXPIRED_OR_NOT_FOUND = "TOKEN_EXPIRED_OR_NOT_FOUND",
}

export const AuthenticationErrorTypes = [
  AuthenticationErrors.BAD_CREDENTIALS,
] as const

export const RefreshTokenErrorTypes = [
  AuthenticationErrors.INVALID_TOKEN_STRUCTURE,
  AuthenticationErrors.TOKEN_EXPIRED_OR_NOT_FOUND,
] as const
