import React, { useEffect, useMemo } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { PhotonProperties, useAppDispatch, useAppSelector } from "common";
import {
  fetchFeaturesByQuery,
  fetchLocationByQuery,
  updateStage,
  updateStageInputValue,
} from "features/order";
import {
  clearLocalizationAutoComplete,
  updateLatestStageIndex,
  updateLocalizationAutoComplete,
} from "features/order";
import { AutoComplete, Input } from "antd";
import debounce from "lodash/debounce";
import { useMap } from "react-map-gl";

interface LocalizationAutoCompleteProps {
  index: number;
  placeholder: string;
}

const autoCompleteStyle: React.CSSProperties = { width: "85%" };

const createFullName = ({ name, city }: PhotonProperties): string => {
  if (name && city) {
    return `${name}, ${city}`;
  } else {
    return name || (city as string);
  }
};

export const LocalizationAutoComplete: React.FC<
  LocalizationAutoCompleteProps
> = ({ index, placeholder }) => {
  const queryClient = useQueryClient();
  const { orderMap } = useMap();
  const dispatch = useAppDispatch();

  const { latestStageIndex } = useAppSelector(state => state.createNewOrder);
  const stage = useAppSelector(state => state.createNewOrder.steps[index]);

  const fetchFeatures = async (value: string) => {
    const features = await fetchFeaturesByQuery(queryClient, value);

    const localizations = features
      .filter(({ properties }) => !!properties.name || !!properties.city)
      .map(({ properties, geometry }) => ({
        value: createFullName(properties),
        lat: geometry.coordinates[1],
        lon: geometry.coordinates[0],
      }));

    dispatch(updateLocalizationAutoComplete(localizations));
  };

  const fetchLocation = async (value: string) => {
    const [coordinates] = await fetchLocationByQuery(queryClient, value);

    if (!coordinates) {
      const emptyStage = { inputValue: value };
      return dispatch(updateStage({ index, stage: emptyStage }));
    }

    const nextStage = {
      inputValue: value,
      lat: parseFloat(coordinates.lat),
      lon: parseFloat(coordinates.lon),
    };

    orderMap?.flyTo({
      center: [nextStage.lon!, nextStage.lat!],
      zoom: 15,
    });
    dispatch(updateStage({ index, stage: nextStage }));
  };

  const onChange = (value: string) =>
    dispatch(updateStageInputValue({ index, inputValue: value }));

  const onSearch = useMemo(
    () =>
      debounce(async (value: string) => {
        await Promise.all([fetchFeatures(value), fetchLocation(value)]);
        dispatch(updateLatestStageIndex(index));
      }, 1000),
    [orderMap],
  );

  useEffect(() => {
    return () => {
      onSearch.cancel();
    };
  }, [onSearch]);

  const onClick = () => {
    if (latestStageIndex !== index) {
      dispatch(clearLocalizationAutoComplete());
    }
    dispatch(updateLatestStageIndex(index));
  };

  return (
    <AutoComplete
      style={autoCompleteStyle}
      value={stage.inputValue}
      onChange={onChange}
      onSearch={onSearch}
      onClick={onClick}
    >
      <Input size="large" placeholder={placeholder} />
    </AutoComplete>
  );
};
