import { UserRole } from "@/common"

export enum InvitationStatus {
  PENDING = "PENDING",
  ACCEPTED = "ACCEPTED",
  EXPIRED = "EXPIRED",
}

export interface CreateInvitationRequest {
  email: string
  roles: UserRole[]
  firstName: string
  lastName: string
  phoneNumber: string
  street: string
  streetNumber: string
  apartmentNumber?: string
  city: string
  stateProvince: string
  postalCode: string
}

export interface GetInvitationResponse {
  invitationId: string
  fleetId: string
  fleetName: string
  firstName: string
  lastName: string
  email: string
  status: InvitationStatus
  createdAt: string
  expiresAt: string
  acceptedAt: string | null
}

export interface GetInvitationsQuery {
  fleetId: string
  limit: number
  page: number
  status?: InvitationStatus
  email?: string
}

export interface GetInvitationsResponse {
  invitations: GetInvitationResponse[]
  total: number
}

export enum InvitationErrors {
  INVITATION_NOT_FOUND = "INVITATION_NOT_FOUND",
  INVITATION_ALREADY_ACCEPTED = "INVITATION_ALREADY_ACCEPTED",
  INVITATION_EXPIRED = "INVITATION_EXPIRED",
  USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS",
  INVITATION_ALREADY_EXISTS = "INVITATION_ALREADY_EXISTS",
}

export const GetInvitationErrorTypes = [
  InvitationErrors.INVITATION_NOT_FOUND,
] as const

export const CreateInvitationErrorTypes = [
  InvitationErrors.USER_ALREADY_EXISTS,
  InvitationErrors.INVITATION_ALREADY_EXISTS,
] as const

export const AcceptInvitationErrorTypes = [
  InvitationErrors.INVITATION_NOT_FOUND,
  InvitationErrors.INVITATION_ALREADY_EXISTS,
  InvitationErrors.INVITATION_ALREADY_ACCEPTED,
  InvitationErrors.INVITATION_EXPIRED,
] as const
