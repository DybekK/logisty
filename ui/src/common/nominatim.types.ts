export type NominatimResponse = NominatimCoordinates[];

export interface NominatimCoordinates {
  lat: string;
  lon: string;
}

//TODO: separate API layer from domain layer
export interface Coordinates {
  lat: number;
  lon: number;
}
