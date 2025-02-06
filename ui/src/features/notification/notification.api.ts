import { useQuery } from "@tanstack/react-query"

import { authAxiosInstance, handleAxiosResponse } from "@/common"
import { GetNotificationsResponse } from "@/features/notification"

export const fetchNotificationsKey = "fetchNotifications"
export const refetchNotificationsKey = "refetchNotifications"

export const fetchNotifications = async (
  fleetId: string,
  lastUpdatedAt: string,
  locale: string,
): Promise<GetNotificationsResponse> =>
  authAxiosInstance
    .get(`/fleets/${fleetId}/notifications/translated`, {
      params: {
        since: lastUpdatedAt,
        locale,
      },
    })
    .then(handleAxiosResponse)

export const useFetchNotifications = (
  fleetId: string,
  lastUpdatedAt: string,
  locale: string,
) =>
  useQuery({
    queryKey: [fetchNotificationsKey, fleetId],
    queryFn: () => fetchNotifications(fleetId, lastUpdatedAt, locale),
  })
