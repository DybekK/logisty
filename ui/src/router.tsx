import { RouterProvider, createBrowserRouter } from "react-router-dom"

import { UserTable } from "./features/user"

import { ProtectedRoute } from "@/components"
import { Authenticate } from "@/features/auth/Authenticate"
import {
  AcceptInvitation,
  CreateInvitation,
  InvitationTable,
} from "@/features/invitation"
import { OrderStatus } from "@/features/order"
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
  PENDING_ORDERS = "/orders/pending",
  COMPLETED_ORDERS = "/orders/completed",
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
          path: Routes.PENDING_ORDERS,
          element: <OrderTable orderStatus={OrderStatus.PENDING} />,
        },
        {
          path: Routes.COMPLETED_ORDERS,
          element: <OrderTable orderStatus={OrderStatus.COMPLETED} />,
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
