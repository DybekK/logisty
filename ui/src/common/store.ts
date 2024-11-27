import { configureStore } from "@reduxjs/toolkit";
import createNewOrderReducer from "features/order/slice/create-new-order.slice.ts";
import ordersReducer from "features/order/slice/orders.slice.ts";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";

export const store = configureStore({
  reducer: {
    createNewOrder: createNewOrderReducer,
    orders: ordersReducer
  }
});

type RootState = ReturnType<typeof store.getState>;
type AppDispatch = typeof store.dispatch;
export const useAppDispatch: () => AppDispatch = useDispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
