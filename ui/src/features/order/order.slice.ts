import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface OrderStage {
  value: string;
  lat?: number;
  lon?: number;
}

export interface LocalizationAutoComplete {
  value: string;
  lat?: number;
  lon?: number;
}

export interface OrderState {
  latestStageIndex: number;
  stages: OrderStage[];
  localizationsAutoComplete: LocalizationAutoComplete[];
}

const emptyStage: OrderStage = {
  value: "",
};

const initialState: OrderState = {
  latestStageIndex: -1,
  stages: Array.from({ length: 2 }, () => emptyStage),
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
    updateLatestStage: (
      state,
      action: PayloadAction<LocalizationAutoComplete>,
    ) => {
      state.stages[state.latestStageIndex] = action.payload;
    },
    updateLatestStageIndex: (state, action: PayloadAction<number>) => {
      state.latestStageIndex = action.payload;
    },
    updateLocalizationAutoComplete: (
      state,
      action: PayloadAction<LocalizationAutoComplete[]>,
    ) => {
      state.localizationsAutoComplete = action.payload;
    },
    clearLocalizationAutoComplete: (state) => {
      state.localizationsAutoComplete = [];
    },
  },
});

export const {
  addStage,
  removeStage,
  updateLatestStage,
  updateLatestStageIndex,
  updateLocalizationAutoComplete,
  clearLocalizationAutoComplete,
} = orderSlice.actions;

export default orderSlice.reducer;
