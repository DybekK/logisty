import { useQuery } from "@tanstack/react-query"

import { authAxiosInstance, handleAxiosResponse } from "@/common"
import {
  CreateInvitationRequest,
  GetInvitationResponse,
  GetInvitationsQuery,
  GetInvitationsResponse,
} from "@/features/invitation/invitation.types"

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

export const fetchInvitations = async (
  query: GetInvitationsQuery,
): Promise<GetInvitationsResponse> =>
  authAxiosInstance
    .get(`/fleets/${query.fleetId}/invitations`, {
      params: {
        limit: query.limit,
        page: query.page,
        status: query.status,
        email: query.email,
      },
    })
    .then(handleAxiosResponse)

export const useFetchInvitations = (query: GetInvitationsQuery) =>
  useQuery({
    queryKey: [fetchInvitationKey, query],
    queryFn: () => fetchInvitations(query),
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
