import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import React from "react"
import ReactDOM from "react-dom/client"
import { Provider } from "react-redux"

import "./index.css"
import { PersistGate } from "redux-persist/integration/react"

import { persistor, store } from "@/common"
import { AuthProvider } from "@/components"
import "@/i18n"
import { Router } from "@/router"

const queryClient = new QueryClient()

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <Provider store={store}>
        <PersistGate loading={null} persistor={persistor}>
          <AuthProvider>
            <Router />
          </AuthProvider>
        </PersistGate>
      </Provider>
    </QueryClientProvider>
  </React.StrictMode>,
)
