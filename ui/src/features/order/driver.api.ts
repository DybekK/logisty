import { useQuery } from "@tanstack/react-query"

import { authAxiosInstance, handleAxiosResponse } from "@/common"
import {
  GetAvailableDriversQuery,
  GetAvailableDriversResponse,
} from "@/features/order"

const fetchAvailableDriversKey = "fetchAvailableDrivers"

export const fetchAvailableDrivers = async (
  query: GetAvailableDriversQuery,
): Promise<GetAvailableDriversResponse> =>
  authAxiosInstance
    .get(`/fleets/${query.fleetId}/drivers/available`, {
      params: {
        startAt: query.startAt,
        endAt: query.endAt,
      },
    })
    .then(handleAxiosResponse)

export const useFetchAvailableDrivers = (query: GetAvailableDriversQuery, enabled: boolean) =>
  useQuery({
    queryKey: [fetchAvailableDriversKey, query],
    queryFn: () => fetchAvailableDrivers(query),
    enabled,
  })
