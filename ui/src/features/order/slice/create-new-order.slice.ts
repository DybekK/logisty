import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { uniqWith } from "lodash";

import { OSRMRoute, OSRMWaypoint } from "@/common";

export interface CreateNewOrderStep {
  inputValue: string;
  value?: string;
  lat?: number;
  lon?: number;
}

export interface CreateNewOrderLocalization {
  value: string;
  lat?: number;
  lon?: number;
}

export interface CreateNewOrderState {
  latestStepIndex: number;
  steps: CreateNewOrderStep[];
  routes: OSRMRoute[];
  waypoints: OSRMWaypoint[];
  localizationsAutoComplete: CreateNewOrderLocalization[];
}

const emptyStep: CreateNewOrderStep = {
  inputValue: "",
};

const initialState: CreateNewOrderState = {
  latestStepIndex: -1,
  steps: Array.from({ length: 2 }, () => emptyStep),
  routes: [],
  waypoints: [],
  localizationsAutoComplete: [],
};

export const createNewOrderSlice = createSlice({
  name: "createNewOrder",
  initialState,
  reducers: {
    //steps
    addStep: state => {
      state.steps.push(emptyStep);
    },

    removeStep: (state, action: PayloadAction<number>) => {
      if (state.latestStepIndex === action.payload) {
        state.latestStepIndex = -1;
      }
      state.steps.splice(action.payload, 1);
    },

    updateStep: (
      state,
      action: { payload: { index: number; step: CreateNewOrderStep } },
    ) => {
      state.steps[action.payload.index] = action.payload.step;
    },

    updateStepInputValue: (
      state,
      action: { payload: { index: number; inputValue: string } },
    ) => {
      const { index, inputValue } = action.payload;
      state.steps[index].inputValue = inputValue;
    },

    updateLatestStepIndex: (state, action: PayloadAction<number>) => {
      state.latestStepIndex = action.payload;
    },

    //localizations autocomplete
    updateLocalizationAutoComplete: (
      state,
      action: PayloadAction<CreateNewOrderLocalization[]>,
    ) => {
      const uniqueLocalizations = uniqWith(
        action.payload,
        (arrVal, othVal) => arrVal.value === othVal.value,
      );

      state.localizationsAutoComplete = [...uniqueLocalizations];
    },

    clearLocalizationAutoComplete: state => {
      state.localizationsAutoComplete = [];
    },

    //osrm routes and waypoints
    updateRoutesAndWaypoints: (
      state,
      action: PayloadAction<{ routes: OSRMRoute[]; waypoints: OSRMWaypoint[] }>,
    ) => {
      state.routes = action.payload.routes;
      state.waypoints = action.payload.waypoints;
    },
  },
});

export const {
  addStep,
  removeStep,
  updateStep,
  updateStepInputValue,
  updateLatestStepIndex,
  updateLocalizationAutoComplete,
  clearLocalizationAutoComplete,
  updateRoutesAndWaypoints,
} = createNewOrderSlice.actions;

export const createNewOrderReducer = createNewOrderSlice.reducer;
