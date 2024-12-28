import { createBrowserRouter } from "react-router-dom";

import App from "@/App";
import { OrderStatus } from "@/features/order";
import { OrderTable } from "@/features/order/filter";
import { NewOrderForm } from "@/features/order/new";

export enum Routes {
  NEW_ORDER = "/orders/new",
  PENDING_ORDERS = "/orders/pending",
  COMPLETED_ORDERS = "/orders/completed",
}

export const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
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
]);
