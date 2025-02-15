import { Navigate, useLocation } from "react-router-dom"

import { UserRole, useAppSelector } from "@/common"
import { useAuth } from "@/components"
import { App } from "@/components"
import { Routes, getDefaultRedirect } from "@/router"

const routeRoleMap = {
  [Routes.INVITATIONS]: [UserRole.DISPATCHER],
  [Routes.CREATE_INVITATION]: [UserRole.DISPATCHER],
  [Routes.USERS]: [UserRole.DISPATCHER],
  [Routes.ORDERS]: [UserRole.DISPATCHER],
  [Routes.NEW_ORDER]: [UserRole.DISPATCHER],
  [Routes.DRIVER_ORDERS]: [UserRole.DRIVER],
}

export const ProtectedRoute = () => {
  const location = useLocation()
  const { isAuthenticated } = useAuth()
  const roles = useAppSelector(state => state.auth.user?.roles) ?? []

  if (!isAuthenticated()) {
    return <Navigate to={Routes.LOGIN} replace state={{ from: location }} />
  }

  const requiredRoles =
    routeRoleMap[location.pathname as keyof typeof routeRoleMap]

  if (requiredRoles && !roles?.some(role => requiredRoles.includes(role))) {
    return <Navigate to={getDefaultRedirect(roles)} replace />
  }

  return <App />
}
