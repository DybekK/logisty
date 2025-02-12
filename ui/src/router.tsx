import { RouterProvider, createBrowserRouter } from "react-router-dom"

import { UserTable } from "./features/user"

import { ProtectedRoute } from "@/components"
import { Authenticate } from "@/features/auth/Authenticate"
import {
  AcceptInvitation,
  CreateInvitation,
  InvitationTable,
} from "@/features/invitation"
import { OrderTable } from "@/features/order/filter"
import { NewOrderForm } from "@/features/order/new"

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
          element: <OrderTable />,
        },
        {
          path: Routes.NEW_ORDER,
          element: <NewOrderForm />,
        },
      ],
    },
  ]

  const router = createBrowserRouter([...protectedRoutes, ...publicRoutes])

  return <RouterProvider router={router} />
}
