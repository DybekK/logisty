export interface GetAvailableDriversQuery {
  fleetId: string
  startAt: string
  endAt: string
}

export interface GetAvailableDriverResponse {
  driverId: string
  firstName: string
  lastName: string
}

export interface GetAvailableDriversResponse {
  drivers: GetAvailableDriverResponse[]
}
