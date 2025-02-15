import { useQuery } from "@tanstack/react-query"

import { authAxiosInstance, handleAxiosResponse } from "@/common"
import {
  CreateOrderRequest,
  GetOrdersQuery,
  GetOrdersResponse,
} from "@/features/order"

const fetchOrdersKey = "fetchOrders"
const fetchDriverOrdersKey = "fetchDriverOrders"

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

export const createOrder = async (fleetId: string, order: CreateOrderRequest) =>
  authAxiosInstance
    .post(`/fleets/${fleetId}/orders`, order)
    .then(handleAxiosResponse)
