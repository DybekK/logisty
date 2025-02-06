import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux"

import { combineReducers, configureStore } from "@reduxjs/toolkit"
import { PERSIST, REHYDRATE, persistReducer, persistStore } from "redux-persist"
import storage from "redux-persist/lib/storage"

import { authReducer } from "@/features/auth/store/auth.slice"
import { createNewOrderReducer } from "@/features/order"
import { ordersReducer } from "@/features/order/store/orders.slice"

const authPersistConfig = {
  key: "auth",
  storage,
  whitelist: ["user"],
}

const rootReducer = combineReducers({
  auth: persistReducer(authPersistConfig, authReducer),
  createNewOrder: createNewOrderReducer,
  orders: ordersReducer,
})

export const store = configureStore({
  reducer: rootReducer,
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [PERSIST, REHYDRATE],
      },
    }),
})

export const persistor = persistStore(store)

type RootState = ReturnType<typeof store.getState>
type AppDispatch = typeof store.dispatch
export const useAppDispatch: () => AppDispatch = useDispatch
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector
