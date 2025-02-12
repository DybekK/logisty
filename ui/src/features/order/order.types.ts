export interface GeoPoint {
  type: string
  coordinates: number[]
}

export interface GeoLineString {
  type: string
  coordinates: number[][]
}

export interface OrderRoute {
  route: GeoLineString
  duration: number
  distance: number
}

export interface OrderStep {
  description: string
  location: GeoPoint
  estimatedArrivalAt?: string
  actualArrivalAt?: string
}

export interface CreateOrderStep {
  description: string
  lat: number
  lon: number
  estimatedArrivalAt: string
}

export interface CreateOrderRoute {
  route: GeoLineString
  duration: number
  distance: number
}

export interface CreateOrderRequest {
  driverId: string
  steps: CreateOrderStep[]
  route: OrderRoute
  createdBy: string
  estimatedStartedAt: string
  estimatedEndedAt: string
}

export interface GetOrderResponse {
  orderId: string
  fleetId: string
  driverId: string
  driverFirstName: string
  driverLastName: string
  steps: OrderStep[]
  route: OrderRoute
  createdBy: string
  createdAt: string
  estimatedStartedAt: string
  estimatedEndedAt: string
}

export interface GetOrdersResponse {
  orders: GetOrderResponse[]
  total: number
}

export interface GetOrdersQuery {
  fleetId: string
  limit: number
  page: number
}
