import { useMutation, useQuery } from "@tanstack/react-query"
import { createContext, useContext, useEffect, useMemo, useState } from "react"

import { P, match } from "ts-pattern"

import {
  AccessTokenErrorTypes,
  AxiosBackendError,
  authAxiosInstance,
  patternErrors,
  useAppDispatch,
  useAppSelector,
} from "@/common"
import { removeUser, setUser } from "@/features/auth"
import { fetchCurrentUser, refresh } from "@/features/auth/authentication.api"
import {
  fetchNotifications,
  fetchNotificationsKey,
  prependNotifications,
  refetchNotifications,
  refetchNotificationsKey,
} from "@/features/notification"
import { useTranslationWithPrev } from "@/i18n"

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
  const dispatch = useAppDispatch()
  const { i18n, hasLanguageChanged } = useTranslationWithPrev()

  const user = useAppSelector(state => state.auth.user)
  const { lastUpdatedAt, firstUpdatedAt } = useAppSelector(
    state => state.notification,
  )

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

  const isAuthenticated = () => !!tokens?.accessToken && !!tokens?.refreshToken

  const fetchNotificationsEnabled = isAuthenticated() && !!user?.fleetId

  useQuery({
    queryKey: [fetchNotificationsKey, i18n.language],
    queryFn: () =>
      fetchNotifications(user?.fleetId!, lastUpdatedAt, i18n.language).then(
        ({ notifications }) => dispatch(prependNotifications(notifications)),
      ),
    refetchInterval: 5000,
    enabled: fetchNotificationsEnabled,
  })

  useQuery({
    queryKey: [refetchNotificationsKey, i18n.language],
    queryFn: () =>
      fetchNotifications(user?.fleetId!, firstUpdatedAt, i18n.language).then(
        ({ notifications }) => dispatch(refetchNotifications(notifications)),
      ),
    refetchOnMount: false,
    refetchOnWindowFocus: false,
    enabled: fetchNotificationsEnabled && hasLanguageChanged,
  })

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
