import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { OSRMRoute, OSRMWaypoint } from "common";
import { uniqWith } from "lodash";

export interface OrderStage {
  inputValue: string;
  value?: string;
  lat?: number;
  lon?: number;
}

export interface Localization {
  value: string;
  lat?: number;
  lon?: number;
}

export interface OrderState {
  latestStageIndex: number;
  stages: OrderStage[];
  routes: OSRMRoute[];
  waypoints: OSRMWaypoint[];
  localizationsAutoComplete: Localization[];
}

const emptyStage: OrderStage = {
  inputValue: "",
};

const initialState: OrderState = {
  latestStageIndex: -1,
  stages: Array.from({ length: 2 }, () => emptyStage),
  routes: [],
  waypoints: [],
  localizationsAutoComplete: [],
};

export const orderSlice = createSlice({
  name: "order",
  initialState,
  reducers: {
    //stages
    addStage: state => {
      state.stages.push(emptyStage);
    },

    removeStage: (state, action: PayloadAction<number>) => {
      if (state.latestStageIndex === action.payload) {
        state.latestStageIndex = -1;
      }
      state.stages.splice(action.payload, 1);
    },

    updateStage: (
      state,
      action: { payload: { index: number; stage: OrderStage } },
    ) => {
      state.stages[action.payload.index] = action.payload.stage;
    },

    updateStageInputValue: (
      state,
      action: { payload: { index: number; inputValue: string } },
    ) => {
      const { index, inputValue } = action.payload;
      state.stages[index].inputValue = inputValue;
    },

    updateLatestStageIndex: (state, action: PayloadAction<number>) => {
      state.latestStageIndex = action.payload;
    },

    //localizations autocomplete
    updateLocalizationAutoComplete: (
      state,
      action: PayloadAction<Localization[]>,
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
} = orderSlice.actions;

export default orderSlice.reducer;
