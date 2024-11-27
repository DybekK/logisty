import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { OSRMRoute, OSRMWaypoint } from "../../../common";
import { uniqWith } from "lodash";

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
  latestStageIndex: number;
  steps: CreateNewOrderStep[];
  routes: OSRMRoute[];
  waypoints: OSRMWaypoint[];
  localizationsAutoComplete: CreateNewOrderLocalization[];
}

const emptyStep: CreateNewOrderStep = {
  inputValue: "",
};

const initialState: CreateNewOrderState = {
  latestStageIndex: -1,
  steps: Array.from({ length: 2 }, () => emptyStep),
  routes: [],
  waypoints: [],
  localizationsAutoComplete: [],
};

export const createNewOrderSlice = createSlice({
  name: "createNewOrder",
  initialState,
  reducers: {
    //stages
    addStage: state => {
      state.steps.push(emptyStep);
    },

    removeStage: (state, action: PayloadAction<number>) => {
      if (state.latestStageIndex === action.payload) {
        state.latestStageIndex = -1;
      }
      state.steps.splice(action.payload, 1);
    },

    updateStage: (
      state,
      action: { payload: { index: number; stage: CreateNewOrderStep } },
    ) => {
      state.steps[action.payload.index] = action.payload.stage;
    },

    updateStageInputValue: (
      state,
      action: { payload: { index: number; inputValue: string } },
    ) => {
      const { index, inputValue } = action.payload;
      state.steps[index].inputValue = inputValue;
    },

    updateLatestStageIndex: (state, action: PayloadAction<number>) => {
      state.latestStageIndex = action.payload;
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
  addStage,
  removeStage,
  updateStage,
  updateStageInputValue,
  updateLatestStageIndex,
  updateLocalizationAutoComplete,
  clearLocalizationAutoComplete,
  updateRoutesAndWaypoints,
} = createNewOrderSlice.actions;

export default createNewOrderSlice.reducer;
