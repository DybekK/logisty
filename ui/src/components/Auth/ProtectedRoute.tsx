import { Navigate } from "react-router-dom"

import App from "@/App"
import { useAuth } from "@/components"
import { Routes } from "@/router"

export const ProtectedRoute = () => {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated()) {
    return <Navigate to={Routes.LOGIN} />
  }

  return <App />
}
