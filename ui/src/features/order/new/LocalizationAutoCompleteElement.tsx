import { Button } from "antd";
import React from "react";
import {
  clearLocalizationAutoComplete,
  LocalizationAutoComplete,
  updateLatestStage,
} from "features/order";
import { useAppDispatch } from "common";

interface LocalizationAutoCompleteElementProps {
  index: number;
  localization: LocalizationAutoComplete;
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
  const dispatch = useAppDispatch();

  const updateLocalizationAutoComplete = (
    localization: LocalizationAutoComplete,
  ) => {
    dispatch(updateLatestStage(localization));
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
