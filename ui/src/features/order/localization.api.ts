import { QueryClient } from "@tanstack/react-query";

import axios from "axios";

import {
  NominatimResponse,
  OSRMRouteResponse,
  PhotonFeature,
  PhotonResponse,
} from "@/common";

const { VITE_PHOTON_URL, VITE_NOMINATIM_URL, VITE_OSRM_URL } = import.meta.env;
const DEFAULT_TIMEOUT = 2000;

const getFeaturesByQueryKey = "getFeaturesByQuery";
export const fetchFeaturesByQuery = async (
  queryClient: QueryClient,
  query: string,
  limit: number = 10,
): Promise<PhotonFeature[]> => {
  const queryFn = () =>
    axios
      .get<PhotonResponse>(`${VITE_PHOTON_URL}/api`, {
        params: { q: query, limit: limit },
        timeout: DEFAULT_TIMEOUT,
      })
      .then(({ data }) => data.features);

  return queryClient.fetchQuery({
    queryKey: [getFeaturesByQueryKey, query, limit],
    queryFn,
  });
};

const getLocationByQueryKey = "getLocationByQuery";
export const fetchLocationByQuery = async (
  queryClient: QueryClient,
  query: string,
): Promise<NominatimResponse> => {
  const queryFn = () =>
    axios
      .get<NominatimResponse>(`${VITE_NOMINATIM_URL}/search`, {
        params: { format: "json", q: query, limit: 1 },
        timeout: DEFAULT_TIMEOUT,
      })
      .then(({ data }) => data);

  return queryClient.fetchQuery({
    queryKey: [getLocationByQueryKey, query],
    queryFn,
  });
};

const getGeneratedPathByCoordinatesKey = "getGeneratedPathByCoordinates";
export const fetchGeneratedPathByCoordinates = async (
  queryClient: QueryClient,
  steps: { lat?: number; lon?: number }[],
): Promise<OSRMRouteResponse> => {
  const formattedCoordinates = steps
    .filter(({ lat, lon }) => !!lat && !!lon)
    .map(({ lat, lon }) => `${lon},${lat}`)
    .join(";");

  const queryFn = () =>
    axios
      .get<OSRMRouteResponse>(
        `${VITE_OSRM_URL}/route/v1/driving/${formattedCoordinates}`,
        {
          params: {
            geometries: "geojson",
            alternatives: false,
            overview: "full",
            steps: true,
          },
          timeout: DEFAULT_TIMEOUT,
        },
      )
      .then(({ data }) => data);

  return queryClient.fetchQuery({
    queryKey: [getGeneratedPathByCoordinatesKey, formattedCoordinates],
    queryFn,
  });
};
