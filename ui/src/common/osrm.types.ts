export interface OSRMRouteResponse {
  code: string;
  routes: OSRMRoute[];
  waypoints: OSRMWaypoint[];
}

export interface OSRMRoute {
  geometry: OSRMGeometry;
  duration: number;
  distance: number;
}

export interface OSRMGeometry {
  coordinates: number[][];
  type: string;
}

export interface OSRMWaypoint {
  hint: string;
  distance: number;
  name: string;
  location: number[];
}
