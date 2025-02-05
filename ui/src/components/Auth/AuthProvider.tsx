import { useMutation } from "@tanstack/react-query"
import { createContext, useContext, useEffect, useMemo, useState } from "react"
import { useDispatch } from "react-redux"

import { P, match } from "ts-pattern"

import {
  AccessTokenErrorTypes,
  AxiosBackendError,
  authAxiosInstance,
  patternErrors,
} from "@/common"
import { removeUser, setUser } from "@/features/auth"
import { fetchCurrentUser, refresh } from "@/features/auth/authentication.api"

interface AuthProviderProps {
  children: React.ReactNode
}

interface Tokens {
  accessToken: string | null
  refreshToken: string | null
}

interface AuthContextType {
  tokens: Tokens | null
  isAuthenticated: () => boolean
  setTokens: (tokens: Tokens) => void
  removeTokens: () => void
}

const fallback = () => console.warn("AuthContext not initialized")
export const AuthContext = createContext<AuthContextType>({
  tokens: null,
  isAuthenticated: () => false,
  setTokens: fallback,
  removeTokens: fallback,
})

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const dispatch = useDispatch()

  const [tokens, setTokens_] = useState<Tokens>({
    accessToken: localStorage.getItem("accessToken"),
    refreshToken: localStorage.getItem("refreshToken"),
  })

  const setTokens = (tokens: Tokens) => {
    setTokens_(tokens)
  }

  const removeTokens = () => {
    setTokens_({
      accessToken: null,
      refreshToken: null,
    })
  }

  const { mutateAsync: refreshMutate } = useMutation({
    mutationFn: refresh,
    onSuccess: (refreshedTokens: Tokens) => {
      match(refreshedTokens)
        .with({ accessToken: P.string, refreshToken: P.string }, setTokens)
        .otherwise(removeTokens)
    },
    onError: () => {
      removeTokens()
    },
  })

  const refreshTokenInterceptor = () => {
    authAxiosInstance.interceptors.response.use(
      response => response,
      async (error: AxiosBackendError) => {
        const errors = error.response?.data ?? { errors: [] }

        return match(errors)
          .with(patternErrors(AccessTokenErrorTypes), async () => {
            if (!tokens.refreshToken) {
              return Promise.reject(error)
            }

            await refreshMutate({ refreshToken: tokens.refreshToken })
            return await Promise.resolve(error)
          })
          .otherwise(() => Promise.reject(error))
      },
    )
  }

  const isAuthenticated = () => {
    return !!tokens?.accessToken && !!tokens?.refreshToken
  }

  useEffect(() => {
    refreshTokenInterceptor()

    if (tokens.accessToken && tokens.refreshToken) {
      localStorage.setItem("accessToken", tokens.accessToken)
      localStorage.setItem("refreshToken", tokens.refreshToken)

      fetchCurrentUser()
        .then(user => dispatch(setUser(user)))
        .catch(() => dispatch(removeUser()))
    } else {
      localStorage.removeItem("accessToken")
      localStorage.removeItem("refreshToken")
      dispatch(removeUser())
    }
  }, [tokens])

  const contextValue = useMemo(
    () => ({
      tokens,
      isAuthenticated,
      setTokens,
      removeTokens,
    }),
    [tokens],
  )

  return (
    <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>
  )
}

export const useAuth = () => {
  return useContext(AuthContext)
}
