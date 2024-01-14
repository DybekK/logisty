import React, { useEffect, useMemo } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { Properties, useAppDispatch, useAppSelector } from "common";
import {
  fetchFeaturesByQuery,
  fetchLocationByQuery,
  updateStage,
} from "features/order";
import {
  clearLocalizationAutoComplete,
  updateLatestStageIndex,
  updateLocalizationAutoComplete,
} from "features/order";
import { AutoComplete, Input } from "antd";
import debounce from "lodash/debounce";

interface LocalizationAutoCompleteProps {
  index: number;
  placeholder: string;
}

const autoCompleteStyle: React.CSSProperties = { width: "85%" };

const createFullName = ({ name, city }: Properties) =>
  `${city ? city + ", " : ""}${name ? name : ""}`;

export const LocalizationAutoComplete: React.FC<
  LocalizationAutoCompleteProps
> = ({ index, placeholder }) => {
  const queryClient = useQueryClient();
  const dispatch = useAppDispatch();

  const { latestStageIndex } = useAppSelector((state) => state.orders);
  const stage = useAppSelector((state) => state.orders.stages[index]);
  const [value, setValue] = React.useState<string>("");

  const onSearch = useMemo(
    () =>
      debounce(async (value: string) => {
        fetchFeaturesByQuery(queryClient, value).then((features) => {
          const localizations = features.map(({ properties, geometry }) => ({
            value: createFullName(properties),
            lat: geometry.coordinates[1],
            lon: geometry.coordinates[0],
          }));
          dispatch(updateLocalizationAutoComplete(localizations));
        });

        fetchLocationByQuery(queryClient, value).then(({ lat, lon }) => {
          const localization = { value, lat, lon };
          dispatch(updateStage({ index, localization }));
        });
        dispatch(updateLatestStageIndex(index));
      }, 1000),
    [],
  );

  useEffect(() => {
    setValue(stage.value);
  }, [stage]);

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
      value={value}
      onChange={setValue}
      onSearch={onSearch}
      onClick={onClick}
    >
      <Input size="large" placeholder={placeholder} />
    </AutoComplete>
  );
};
