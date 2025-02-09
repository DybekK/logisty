export interface OSRMRouteResponse {
  code: string
  routes: OSRMRoute[]
  waypoints: OSRMWaypoint[]
}

export interface OSRMLegStep {
  distance: number
  duration: number
  summary: string
}

export interface OSRMLeg {
  steps: OSRMLegStep[]
  distance: number
  duration: number
}

export interface OSRMRoute {
  geometry: OSRMGeometry
  legs: OSRMLeg[]
  duration: number
  distance: number
}

export interface OSRMGeometry {
  coordinates: number[][]
  type: string
}

export interface OSRMWaypoint {
  hint: string
  distance: number
  name: string
  location: number[]
}
