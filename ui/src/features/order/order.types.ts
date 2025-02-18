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
  routePoints?: GeoLineString
  duration: number
  distance: number
}

export interface OrderStep {
  orderStepId: string
  description: string
  location: GeoPoint
  estimatedArrivalAt?: string
  actualArrivalAt?: string
}

export enum OrderStatus {
  ASSIGNED = "ASSIGNED",
  PENDING = "PENDING",
  COMPLETED = "COMPLETED",
  CANCELLED = "CANCELLED",
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

export interface ReportOrderRequest {
  arrivedAt: string
  lat: number
  lon: number
}

export interface GetOrderResponse {
  orderId: string
  fleetId: string
  driverId: string
  driverFirstName: string
  driverLastName: string
  status: OrderStatus
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
  driverId?: string
  limit: number
  page: number
}

export interface GetUpcomingOrderQuery {
  fleetId: string
  driverId: string
}

export interface GetUpcomingOrderResponse {
  orderId: string
  fleetId: string
  driverId: string
  status: OrderStatus
  steps: OrderStep[]
  route: OrderRoute
  createdBy: string
  createdAt: string
  estimatedStartedAt: string
  estimatedEndedAt: string
}

export interface TrackDriverLocationRequest {
  route: GeoLineString
}
