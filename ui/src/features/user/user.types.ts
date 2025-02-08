import { UserRole } from "@/common"

export interface GetUserResponse {
  userId: string
  firstName: string
  lastName: string
  email: string
  roles: UserRole[]
  createdAt: string
}

export interface GetUsersQuery {
  fleetId: string
  limit: number
  page: number
  role?: UserRole
  email?: string
}

export interface GetUsersResponse {
  users: GetUserResponse[]
  total: number
}
