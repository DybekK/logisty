import App from "./App.tsx";
import { createBrowserRouter } from "react-router-dom";
import { NewOrderForm, OrderStatus, OrderTable } from "./features/order";

export enum Routes {
  NEW_ORDER = "/orders/new",
  PENDING_ORDERS = "/orders/pending",
  COMPLETED_ORDERS = "/orders/completed"
}

export const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      {
        path: Routes.PENDING_ORDERS,
        element: <OrderTable orderStatus={OrderStatus.PENDING}/>
      },
      {
        path: Routes.COMPLETED_ORDERS,
        element: <OrderTable orderStatus={OrderStatus.COMPLETED}/>
      },
      {
        path: Routes.NEW_ORDER,
        element: <NewOrderForm />
      }
    ]
  }
]);
