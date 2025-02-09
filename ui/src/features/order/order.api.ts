import { useQuery } from "@tanstack/react-query"

import { authAxiosInstance, handleAxiosResponse } from "@/common"
import { GetOrdersQuery, GetOrdersResponse, CreateOrderRequest } from "@/features/order"

const fetchOrdersKey = "fetchOrders"

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

export const useFetchOrders = (query: GetOrdersQuery) =>
  useQuery({
    queryKey: [fetchOrdersKey, query],
    queryFn: () => fetchOrders(query),
  })

export const createOrder = async (fleetId: string, order: CreateOrderRequest) =>
  authAxiosInstance.post(`/fleets/${fleetId}/orders`, order)
    .then(handleAxiosResponse)
