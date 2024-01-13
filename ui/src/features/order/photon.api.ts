import axios from "axios";
import { Feature, PhotonResponse } from "common";
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
