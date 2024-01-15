import { Button } from "antd";
import React from "react";
import {
  clearLocalizationAutoComplete,
  Localization,
  updateStage,
} from "features/order";
import { useAppDispatch, useAppSelector } from "common";
import { useMap } from "react-map-gl";

interface LocalizationAutoCompleteElementProps {
  localization: Localization;
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
  const { latestStageIndex } = useAppSelector(state => state.orders);
  const dispatch = useAppDispatch();

  const updateLocalizationAutoComplete = (localization: Localization) => {
    orderMap?.flyTo({
      center: [localization.lon!, localization.lat!],
      zoom: 15,
    });

    dispatch(updateStage({ index: latestStageIndex, localization }));
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
