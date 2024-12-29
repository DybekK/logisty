import React from "react"
import { useMap } from "react-map-gl"

import { Button } from "antd"

import { useAppDispatch, useAppSelector } from "@/common"
import {
  CreateNewOrderLocalization,
  clearLocalizationAutoComplete,
  updateStep,
} from "@/features/order"

interface LocalizationAutoCompleteElementProps {
  localization: CreateNewOrderLocalization
}

const spanStyle: React.CSSProperties = {
  textAlign: "left",
  width: "250px",
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap",
}

export const LocalizationAutoCompleteElement: React.FC<
  LocalizationAutoCompleteElementProps
> = ({ localization }) => {
  const { orderMap } = useMap()
  const { latestStepIndex } = useAppSelector(state => state.createNewOrder)
  const dispatch = useAppDispatch()

  const updateLocalizationAutoComplete = (
    localization: CreateNewOrderLocalization,
  ) => {
    const step = {
      inputValue: localization.value,
      lat: localization.lat,
      lon: localization.lon,
    }

    orderMap?.flyTo({
      center: [localization.lon!, localization.lat!],
      zoom: 15,
    })

    dispatch(updateStep({ index: latestStepIndex, step }))
    dispatch(clearLocalizationAutoComplete())
  }

  return (
    <Button
      size="large"
      type="text"
      onClick={() => updateLocalizationAutoComplete(localization)}
    >
      <span style={spanStyle}>{localization.value}</span>
    </Button>
  )
}
