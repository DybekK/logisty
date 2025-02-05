import { PayloadAction, createSlice } from "@reduxjs/toolkit"

enum UserRole {
  DRIVER = "DRIVER",
  DISPATCHER = "DISPATCHER",
}

interface User {
  userId: string
  fleetId: string
  email: string
  firstName: string
  lastName: string
  roles: UserRole[]
}

interface AuthState {
  user?: User
}

const initialState: AuthState = {
  user: undefined,
}

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setUser: (state, { payload }: PayloadAction<User>) => {
      state.user = payload
    },
    removeUser: state => {
      state.user = undefined
    },
  },
})

export const { setUser, removeUser } = authSlice.actions

export const authReducer = authSlice.reducer
