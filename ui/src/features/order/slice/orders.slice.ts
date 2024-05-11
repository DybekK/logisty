import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export enum OrderStatus {
  PENDING = "pending",
  COMPLETED = "completed"
}

interface Location {
  street: string;
  city: string;
  postCode: string;
}

interface OrderStep {
  location: Location;
  coordinates: [number, number];
}

interface Order {
  id: string;
  status: OrderStatus;
  steps: OrderStep[];
  createdAt: string;
}

interface OrderState {
  orders: Order[];
}

const initialState: OrderState = {
  orders: [],
};

export const ordersSlice = createSlice({
  name: "order",
  initialState,
  reducers: {
    setOrders: (state, { payload }: PayloadAction<Order[]>) => {
      state.orders = payload;
    },
  },
});

export const { setOrders } = ordersSlice.actions;

export default ordersSlice.reducer;