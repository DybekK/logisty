import { RouterProvider, createBrowserRouter } from "react-router-dom"

import { ProtectedRoute } from "@/components"
import { Authenticate } from "@/features/auth/Authenticate"
import { AcceptInvitation } from "@/features/invitation"
import { OrderStatus } from "@/features/order"
import { OrderTable } from "@/features/order/filter"
import { NewOrderForm } from "@/features/order/new"

export enum Routes {
  LOGIN = "/login",
  ACCEPT_INVITATION = "/accept-invitation",
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
      path: `${Routes.ACCEPT_INVITATION}/:invitationId`,
      element: <AcceptInvitation />,
    },
  ]

  const protectedRoutes = [
    {
      path: "/",
      element: <ProtectedRoute />,
      children: [
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
