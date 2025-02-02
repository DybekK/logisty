export enum InvitationStatus {
  PENDING = "PENDING",
  ACCEPTED = "ACCEPTED",
  REJECTED = "REJECTED",
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

export enum InvitationErrors {
  INVITATION_NOT_FOUND = "INVITATION_NOT_FOUND",
  INVITATION_ALREADY_EXISTS = "INVITATION_ALREADY_EXISTS",
  INVITATION_ALREADY_ACCEPTED = "INVITATION_ALREADY_ACCEPTED",
  INVITATION_EXPIRED = "INVITATION_EXPIRED",
}

export const GetInvitationErrorTypes = [
  InvitationErrors.INVITATION_NOT_FOUND,
] as const

export const AcceptInvitationErrorTypes = [
  InvitationErrors.INVITATION_NOT_FOUND,
  InvitationErrors.INVITATION_ALREADY_EXISTS,
  InvitationErrors.INVITATION_ALREADY_ACCEPTED,
  InvitationErrors.INVITATION_EXPIRED,
] as const
