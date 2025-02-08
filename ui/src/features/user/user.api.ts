import { useQuery } from "@tanstack/react-query"

import { authAxiosInstance, handleAxiosResponse } from "@/common"
import { GetUsersQuery, GetUsersResponse } from "@/features/user/user.types"

const fetchUserKey = "fetchUser"

export const fetchUsers = async (
  query: GetUsersQuery,
): Promise<GetUsersResponse> =>
  authAxiosInstance
    .get(`/fleets/${query.fleetId}/users`, {
      params: {
        limit: query.limit,
        page: query.page,
        role: query.role,
        email: query.email,
      },
    })
    .then(handleAxiosResponse)

export const useFetchUsers = (query: GetUsersQuery) =>
  useQuery({
    queryKey: [fetchUserKey, query],
    queryFn: () => fetchUsers(query),
  })
