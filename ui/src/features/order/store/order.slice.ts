import { PayloadAction, createSlice } from "@reduxjs/toolkit"
import { uniqWith } from "lodash"

import { OSRMRoute, OSRMWaypoint } from "@/common"

export interface CreateNewOrderStep {
  inputValue: string
  value?: string
  lat?: number
  lon?: number
  estimatedArrivalAt?: string
}

export interface CreateNewOrderLocalization {
  value: string
  lat?: number
  lon?: number
}

export interface CreateNewOrderState {
  latestStepIndex: number
  startDate?: string
  estimatedEndedAt?: string
  selectedDriverId?: string
  searchByEmail?: string
  steps: CreateNewOrderStep[]
  routes: OSRMRoute[]
  waypoints: OSRMWaypoint[]
  localizationsAutoComplete: CreateNewOrderLocalization[]
}

const emptyStep: CreateNewOrderStep = {
  inputValue: "",
}

const initialState: CreateNewOrderState = {
  latestStepIndex: -1,
  steps: Array.from({ length: 2 }, () => emptyStep),
  routes: [],
  waypoints: [],
  localizationsAutoComplete: [],
}

export const createNewOrderSlice = createSlice({
  name: "createNewOrder",
  initialState,
  reducers: {
    reset: state => {
      state.latestStepIndex = -1
      state.startDate = undefined
      state.estimatedEndedAt = undefined
      state.steps = Array.from({ length: 2 }, () => emptyStep)
      state.routes = []
      state.waypoints = []
      state.localizationsAutoComplete = []
    },
    setStartDate: (state, action: PayloadAction<string>) => {
      state.startDate = action.payload
    },

    //steps
    addStep: state => {
      state.steps.push(emptyStep)
    },

    removeStep: (state, action: PayloadAction<number>) => {
      if (state.latestStepIndex === action.payload) {
        state.latestStepIndex = -1
      }
      state.steps.splice(action.payload, 1)
    },

    updateStep: (
      state,
      action: { payload: { index: number; step: CreateNewOrderStep } },
    ) => {
      state.steps[action.payload.index] = action.payload.step
    },

    updateStepInputValue: (
      state,
      action: { payload: { index: number; inputValue: string } },
    ) => {
      const { index, inputValue } = action.payload
      state.steps[index].inputValue = inputValue
    },

    updateLatestStepIndex: (state, action: PayloadAction<number>) => {
      state.latestStepIndex = action.payload
    },

    //localizations autocomplete
    updateLocalizationAutoComplete: (
      state,
      action: PayloadAction<CreateNewOrderLocalization[]>,
    ) => {
      const uniqueLocalizations = uniqWith(
        action.payload,
        (arrVal, othVal) => arrVal.value === othVal.value,
      )

      state.localizationsAutoComplete = [...uniqueLocalizations]
    },

    clearLocalizationAutoComplete: state => {
      state.localizationsAutoComplete = []
    },

    //osrm routes and waypoints
    updateRoutesAndWaypoints: (
      state,
      action: PayloadAction<{ routes: OSRMRoute[]; waypoints: OSRMWaypoint[] }>,
    ) => {
      state.routes = action.payload.routes
      state.waypoints = action.payload.waypoints
    },

    updateEstimatedTimes: (
      state,
      action: PayloadAction<{
        estimatedArrivalAt: string[]
        estimatedEndedAt: string
      }>,
    ) => {
      state.steps.slice(1).forEach((step, index) => {
        step.estimatedArrivalAt = action.payload.estimatedArrivalAt[index]
      })

      state.estimatedEndedAt = action.payload.estimatedEndedAt
    },

    // drivers
    selectDriver: (state, action: PayloadAction<string>) => {
      state.selectedDriverId = action.payload
    },

    unselectDriver: state => {
      state.selectedDriverId = undefined
    },

    updateSearchByEmail: (state, action: PayloadAction<string>) => {
      state.searchByEmail = action.payload
    },
  },
})

export const {
  reset,
  setStartDate,
  addStep,
  removeStep,
  updateStep,
  updateStepInputValue,
  updateLatestStepIndex,
  updateLocalizationAutoComplete,
  clearLocalizationAutoComplete,
  updateRoutesAndWaypoints,
  updateEstimatedTimes,
  selectDriver,
  unselectDriver,
  updateSearchByEmail,
} = createNewOrderSlice.actions

export const createNewOrderReducer = createNewOrderSlice.reducer
