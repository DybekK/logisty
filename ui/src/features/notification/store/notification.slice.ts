import { PayloadAction, createSlice } from "@reduxjs/toolkit"

export enum NotificationType {
  INFO = "INFO",
  WARNING = "WARNING",
  ERROR = "ERROR",
}

export interface Notification {
  eventId: string
  title: string
  message: string
  eventType: string
  notificationType: NotificationType
  appendedAt: string
}

interface NotificationState {
  notifications: Notification[]
  read: string[]
  firstUpdatedAt: string
  lastUpdatedAt: string
}

const initialState: NotificationState = {
  notifications: [],
  read: [],
  firstUpdatedAt: new Date().toISOString(),
  lastUpdatedAt: new Date().toISOString(),
}

export const notificationSlice = createSlice({
  name: "notification",
  initialState,
  reducers: {
    prependNotifications: (
      state,
      { payload }: PayloadAction<Notification[]>,
    ) => {
      state.notifications = [...payload, ...state.notifications]
      state.lastUpdatedAt = new Date().toISOString()
    },
    refetchNotifications: (
      state,
      { payload }: PayloadAction<Notification[]>,
    ) => {
      const existingNotifications = state.notifications.filter(
        existing => !payload.some(p => p.eventId === existing.eventId),
      )
      state.notifications = [...existingNotifications, ...payload]
      state.lastUpdatedAt = new Date().toISOString()
    },
    markAsRead: (state, { payload }: PayloadAction<string>) => {
      state.read.push(payload)
    },
    markAllAsRead: state => {
      state.read = state.notifications.map(notification => notification.eventId)
    },
    clearNotifications: state => {
      state.notifications = []
      state.read = []
      state.firstUpdatedAt = new Date().toISOString()
      state.lastUpdatedAt = new Date().toISOString()
    },
  },
})

export const {
  prependNotifications,
  refetchNotifications,
  markAsRead,
  markAllAsRead,
  clearNotifications,
} = notificationSlice.actions

export const notificationReducer = notificationSlice.reducer
