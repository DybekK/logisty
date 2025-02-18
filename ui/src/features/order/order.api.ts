import { useQuery } from "@tanstack/react-query"

import { authAxiosInstance, handleAxiosResponse } from "@/common"
import {
  CreateOrderRequest,
  GetOrdersQuery,
  GetOrdersResponse,
  GetUpcomingOrderQuery,
  GetUpcomingOrderResponse,
  ReportOrderRequest,
  TrackDriverLocationRequest,
} from "@/features/order"

const fetchOrdersKey = "fetchOrders"
const fetchDriverOrdersKey = "fetchDriverOrders"
const fetchUpcomingOrderKey = "fetchUpcomingOrder"

export const fetchUpcomingOrder = async (
  query: GetUpcomingOrderQuery,
): Promise<GetUpcomingOrderResponse> =>
  authAxiosInstance
    .get(`/fleets/${query.fleetId}/orders/drivers/${query.driverId}/upcoming`)
    .then(handleAxiosResponse)

export const fetchOrders = async (
  query: GetOrdersQuery,
): Promise<GetOrdersResponse> =>
  authAxiosInstance
    .get(`/fleets/${query.fleetId}/orders`, {
      params: {
        limit: query.limit,
        page: query.page,
      },
    })
    .then(handleAxiosResponse)

export const fetchDriverOrders = async (
  query: GetOrdersQuery,
): Promise<GetOrdersResponse> =>
  authAxiosInstance
    .get(`/fleets/${query.fleetId}/orders/driver/${query.driverId}`, {
      params: {
        limit: query.limit,
        page: query.page,
      },
    })
    .then(handleAxiosResponse)

export const createOrder = async (fleetId: string, order: CreateOrderRequest) =>
  authAxiosInstance
    .post(`/fleets/${fleetId}/orders`, order)
    .then(handleAxiosResponse)

export const reportOrder = async (
  fleetId: string,
  orderId: string,
  stepId: string,
  request: ReportOrderRequest,
) =>
  authAxiosInstance
    .post(`/fleets/${fleetId}/orders/${orderId}/report/${stepId}`, request)
    .then(handleAxiosResponse)

export const trackDriverLocation = async (
  fleetId: string,
  orderId: string,
  request: TrackDriverLocationRequest,
) =>
  authAxiosInstance
    .post(`fleets/${fleetId}/orders/${orderId}/track`, request)
    .then(handleAxiosResponse)

export const useFetchUpcomingOrder = (query: GetUpcomingOrderQuery) =>
  useQuery({
    retry: false,
    queryKey: [fetchUpcomingOrderKey, query],
    queryFn: () => fetchUpcomingOrder(query),
  })

export const useFetchOrders = (query: GetOrdersQuery) =>
  useQuery({
    queryKey: [fetchOrdersKey, query],
    queryFn: () => fetchOrders(query),
  })

export const useFetchDriverOrders = (query: GetOrdersQuery) =>
  useQuery({
    queryKey: [fetchDriverOrdersKey, query],
    queryFn: () => fetchDriverOrders(query),
  })
