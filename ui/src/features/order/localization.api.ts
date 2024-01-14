import axios from "axios";
import {
  Feature,
  Coordinates,
  PhotonResponse,
  NominatimResponse,
} from "common";
import { QueryClient } from "@tanstack/react-query";

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
      >(`https://nominatim.openstreetmap.org/search?format=json&q=${query}&limit=1}`)
      .then(({ data }) => extractCoordinates(data));

  return queryClient.fetchQuery({
    queryKey: [getLocationByQueryKey, query],
    queryFn,
  });
};
