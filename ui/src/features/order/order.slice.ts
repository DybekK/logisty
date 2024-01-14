import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Route } from "common";

export interface OrderStage {
  value: string;
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
  routes: Route[];
  localizationsAutoComplete: Localization[];
}

const emptyStage: OrderStage = {
  value: "",
};

const initialState: OrderState = {
  latestStageIndex: -1,
  stages: Array.from({ length: 2 }, () => emptyStage),
  routes: [],
  localizationsAutoComplete: [],
};

export const orderSlice = createSlice({
  name: "order",
  initialState,
  reducers: {
    addStage: (state) => {
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
      action: { payload: { index: number; localization: Localization } },
    ) => {
      state.stages[action.payload.index] = action.payload.localization;
    },
    updateLatestStageIndex: (state, action: PayloadAction<number>) => {
      state.latestStageIndex = action.payload;
    },
    updateLocalizationAutoComplete: (
      state,
      action: PayloadAction<Localization[]>,
    ) => {
      state.localizationsAutoComplete = action.payload;
    },
    clearLocalizationAutoComplete: (state) => {
      state.localizationsAutoComplete = [];
    },
    updateRoutes: (state, action: PayloadAction<Route[]>) => {
      state.routes = action.payload;
    },
  },
});

export const {
  addStage,
  removeStage,
  updateStage,
  updateLatestStageIndex,
  updateLocalizationAutoComplete,
  clearLocalizationAutoComplete,
  updateRoutes,
} = orderSlice.actions;

export default orderSlice.reducer;
