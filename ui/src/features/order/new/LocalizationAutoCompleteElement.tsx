import { Button } from "antd";
import React from "react";
import {
  clearLocalizationAutoComplete,
  CreateNewOrderLocalization,
  updateStage,
} from "features/order";
import { useAppDispatch, useAppSelector } from "common";
import { useMap } from "react-map-gl";

interface LocalizationAutoCompleteElementProps {
  localization: CreateNewOrderLocalization;
}

const spanStyle: React.CSSProperties = {
  textAlign: "left",
  width: "250px",
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap",
};

export const LocalizationAutoCompleteElement: React.FC<
  LocalizationAutoCompleteElementProps
> = ({ localization }) => {
  const { orderMap } = useMap();
  const { latestStageIndex } = useAppSelector(state => state.createNewOrder);
  const dispatch = useAppDispatch();

  const updateLocalizationAutoComplete = (localization: CreateNewOrderLocalization) => {
    const stage = {
      inputValue: localization.value,
      lat: localization.lat,
      lon: localization.lon,
    };

    orderMap?.flyTo({
      center: [localization.lon!, localization.lat!],
      zoom: 15,
    });

    dispatch(updateStage({ index: latestStageIndex, stage }));
    dispatch(clearLocalizationAutoComplete());
  };

  return (
    <Button
      size="large"
      type="text"
      onClick={() => updateLocalizationAutoComplete(localization)}
    >
      <span style={spanStyle}>{localization.value}</span>
    </Button>
  );
};
