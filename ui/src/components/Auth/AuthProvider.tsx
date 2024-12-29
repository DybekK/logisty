import { createContext, useContext, useEffect, useMemo, useState } from "react"

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

  const isAuthenticated = () => {
    return !!tokens?.accessToken && !!tokens?.refreshToken
  }

  useEffect(() => {
    if (tokens.accessToken && tokens.refreshToken) {
      localStorage.setItem("accessToken", tokens.accessToken)
      localStorage.setItem("refreshToken", tokens.refreshToken)
    } else {
      localStorage.removeItem("accessToken")
      localStorage.removeItem("refreshToken")
    }
  }, [tokens])

  const contextValue = useMemo(
    () => ({
      tokens,
      isAuthenticated,
      setTokens,
      removeTokens,
    }),
    [tokens, isAuthenticated],
  )

  return (
    <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>
  )
}

export const useAuth = () => {
  return useContext(AuthContext)
}
