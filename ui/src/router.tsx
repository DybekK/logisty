import { RouterProvider, createBrowserRouter } from "react-router-dom"

import { UserTable } from "./features/user"

import { UserRole } from "@/common"
import { ProtectedRoute } from "@/components"
import { Authenticate } from "@/features/auth/Authenticate"
import {
  AcceptInvitation,
  CreateInvitation,
  InvitationTable,
} from "@/features/invitation"
import { OrderDispatcherTable, OrderDriverTable } from "@/features/order/filter"
import { NewOrderForm } from "@/features/order/new"
import { UpcomingOrderForm } from "@/features/order/upcoming"

export enum Routes {
  LOGIN = "/login",

  // invitation
  INVITATIONS = "/invitations",
  CREATE_INVITATION = "/invitations/create",
  ACCEPT_INVITATION = "/invitations/:invitationId/accept",

  // user
  USERS = "/users",

  // order
  NEW_ORDER = "/orders/new",
  ORDERS = "/orders",
  DRIVER_ORDERS = "/orders/driver",
  UPCOMING_ORDERS = "/orders/upcoming",
}

const defaultRedirects = {
  [UserRole.DISPATCHER]: Routes.ORDERS,
  [UserRole.DRIVER]: Routes.DRIVER_ORDERS,
}

export const getDefaultRedirect = (roles: UserRole[]) => {
  return (
    defaultRedirects[
      roles.find(
        role => role in defaultRedirects,
      ) as keyof typeof defaultRedirects
    ] ?? Routes.LOGIN
  )
}

export const Router = () => {
  const publicRoutes = [
    {
      path: Routes.LOGIN,
      element: <Authenticate />,
    },
    {
      path: Routes.ACCEPT_INVITATION,
      element: <AcceptInvitation />,
    },
  ]

  const protectedRoutes = [
    {
      path: "/",
      element: <ProtectedRoute />,
      children: [
        // invitation
        {
          path: Routes.INVITATIONS,
          element: <InvitationTable />,
        },
        {
          path: Routes.CREATE_INVITATION,
          element: <CreateInvitation />,
        },
        // user
        {
          path: Routes.USERS,
          element: <UserTable />,
        },
        // order
        {
          path: Routes.ORDERS,
          element: <OrderDispatcherTable />,
        },
        {
          path: Routes.DRIVER_ORDERS,
          element: <OrderDriverTable />,
        },
        {
          path: Routes.NEW_ORDER,
          element: <NewOrderForm />,
        },
        {
          path: Routes.UPCOMING_ORDERS,
          element: <UpcomingOrderForm />,
        },
      ],
    },
  ]

  const router = createBrowserRouter([...publicRoutes, ...protectedRoutes])

  return <RouterProvider router={router} />
}
