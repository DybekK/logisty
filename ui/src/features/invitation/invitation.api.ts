import { useQuery } from "@tanstack/react-query"

import {
  GetInvitationResponse,
  CreateInvitationRequest,
} from "@/features/invitation/invitation.types"

import { authAxiosInstance, handleAxiosResponse } from "@/common"

const fetchInvitationKey = "fetchInvitation"

export const fetchInvitation = async (
  invitationId: string,
): Promise<GetInvitationResponse> =>
  authAxiosInstance
    .get(`/fleets/invitations/${invitationId}`)
    .then(handleAxiosResponse)

export const useFetchInvitation = (invitationId: string) =>
  useQuery({
    queryKey: [fetchInvitationKey, invitationId],
    queryFn: () => fetchInvitation(invitationId),
    retry: false,
  })

export const createInvitation = async (
  fleetId: string,
  request: CreateInvitationRequest,
): Promise<void> =>
  authAxiosInstance
    .post(`/fleets/${fleetId}/invite`, request)
    .then(handleAxiosResponse)

export const acceptInvitation = async (
  invitationId: string,
  password: string,
): Promise<void> =>
  authAxiosInstance
    .post(`/fleets/invitations/${invitationId}/accept`, { password })
    .then(handleAxiosResponse)
