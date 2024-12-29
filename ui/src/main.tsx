import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import React from "react"
import ReactDOM from "react-dom/client"
import { Provider } from "react-redux"

import "./index.css"

import { store } from "@/common"
import { AuthProvider } from "@/components"
import "@/i18n"
import { Router } from "@/router"

const queryClient = new QueryClient()

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <AuthProvider>
      <QueryClientProvider client={queryClient}>
        <Provider store={store}>
          <Router />
        </Provider>
      </QueryClientProvider>
    </AuthProvider>
  </React.StrictMode>,
)
