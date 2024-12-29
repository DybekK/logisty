export interface PhotonResponse {
  type: string
  features: PhotonFeature[]
}

export interface PhotonFeature {
  type: string
  geometry: PhotonGeometry
  properties: PhotonProperties
}

export interface PhotonGeometry {
  coordinates: number[]
  type: string
}

export interface PhotonProperties {
  city?: string
  country?: string
  name?: string
  postcode?: string
}
