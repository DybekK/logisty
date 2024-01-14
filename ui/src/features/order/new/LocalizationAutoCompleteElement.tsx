import { Button } from "antd";
import React from "react";
import {
  clearLocalizationAutoComplete,
  Localization,
  updateStage,
} from "features/order";
import { useAppDispatch, useAppSelector } from "common";

interface LocalizationAutoCompleteElementProps {
  index: number;
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
> = ({ index, localization }) => {
  const { latestStageIndex } = useAppSelector((state) => state.orders);
  const dispatch = useAppDispatch();

  const updateLocalizationAutoComplete = (localization: Localization) => {
    dispatch(updateStage({ index: latestStageIndex, localization }));
    dispatch(clearLocalizationAutoComplete());
  };

  return (
    <Button
      size="large"
      type="text"
      onClick={() => updateLocalizationAutoComplete(localization)}
      key={index}
    >
      <span style={spanStyle}>{localization.value}</span>
    </Button>
  );
};
