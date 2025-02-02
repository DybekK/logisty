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
  user: User | null
}

const initialState: AuthState = {
  user: null,
}

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setUser: (state, { payload }: PayloadAction<User>) => {
      state.user = payload
    },
    removeUser: state => {
      state.user = null
    },
  },
})

export const { setUser, removeUser } = authSlice.actions

export const authReducer = authSlice.reducer
