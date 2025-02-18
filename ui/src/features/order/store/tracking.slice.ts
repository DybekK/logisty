import { PayloadAction, createSlice } from "@reduxjs/toolkit"

import { GeoLineString } from "@/features/order"

interface TrackingState {
  route: GeoLineString
}

const initialState: TrackingState = {
  route: {
    type: "LineString",
    coordinates: [],
  },
}

const trackingSlice = createSlice({
  name: "tracking",
  initialState,
  reducers: {
    addCoordinate: (state, action: PayloadAction<[number, number]>) => {
      state.route.coordinates.push(action.payload)
    },
    clearTrack: state => {
      state.route.coordinates = []
    },
  },
})

export const { addCoordinate, clearTrack } = trackingSlice.actions
export const trackingReducer = trackingSlice.reducer
