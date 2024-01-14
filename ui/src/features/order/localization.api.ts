import axios from "axios";
import {
  Route,
  Feature,
  Coordinates,
  PhotonResponse,
  NominatimResponse,
  OsrmRouteResponse,
} from "common";
import { QueryClient } from "@tanstack/react-query";
import { OrderStage } from "./order.slice.ts";

const getFeaturesByQueryKey = "getFeaturesByQuery";
export const fetchFeaturesByQuery = async (
  queryClient: QueryClient,
  query: string,
  limit: number = 5,
): Promise<Feature[]> => {
  const queryFn = () =>
    axios
      .get<PhotonResponse>(
        `https://photon.komoot.io/api/?q=${query}&limit=${limit}`,
        { timeout: 2000 },
      )
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
): Promise<Coordinates> => {
  const extractCoordinates = (data: NominatimResponse[]) => {
    const { lat, lon } = data[0];
    return { lat: parseFloat(lat), lon: parseFloat(lon) };
  };
  const queryFn = () =>
    axios
      .get<
        NominatimResponse[]
      >(`https://nominatim.openstreetmap.org/search?format=json&q=${query}&limit=1`)
      .then(({ data }) => extractCoordinates(data));

  return queryClient.fetchQuery({
    queryKey: [getLocationByQueryKey, query],
    queryFn,
  });
};

const getGeneratedPathByCoordinatesKey = "getGeneratedPathByCoordinates";
export const fetchGeneratedPathByCoordinates = async (
  queryClient: QueryClient,
  stages: OrderStage[],
): Promise<Route[]> => {
  const formattedCoordinates = stages
    .map(({ lat, lon }) => `${lon},${lat}`)
    .join(";");

  const queryFn = () =>
    axios
      .get<OsrmRouteResponse>(
        `https://routing.openstreetmap.de/routed-bike/route/v1/driving/${formattedCoordinates}?overview=full&alternatives=false&steps=true`,
      )
      .then(({ data }) => data.routes);

  return queryClient.fetchQuery({
    queryKey: [getGeneratedPathByCoordinatesKey, formattedCoordinates],
    queryFn,
  });
};
