import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux"

import { configureStore } from "@reduxjs/toolkit"

import { authReducer } from "@/features/auth"
import { createNewOrderReducer, ordersReducer } from "@/features/order"

export const store = configureStore({
  reducer: {
    auth: authReducer,
    createNewOrder: createNewOrderReducer,
    orders: ordersReducer,
  },
})

type RootState = ReturnType<typeof store.getState>
type AppDispatch = typeof store.dispatch
export const useAppDispatch: () => AppDispatch = useDispatch
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector
