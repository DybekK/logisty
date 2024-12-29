import { useQueryClient } from "@tanstack/react-query"
import React, { useEffect, useMemo } from "react"
import { useMap } from "react-map-gl"

import { AutoComplete, Input } from "antd"

import debounce from "lodash/debounce"

import { PhotonProperties, useAppDispatch, useAppSelector } from "@/common"
import {
  fetchFeaturesByQuery,
  fetchLocationByQuery,
  updateStep,
  updateStepInputValue,
} from "@/features/order"
import {
  clearLocalizationAutoComplete,
  updateLatestStepIndex,
  updateLocalizationAutoComplete,
} from "@/features/order"

interface LocalizationAutoCompleteProps {
  index: number
  placeholder: string
}

const autoCompleteStyle: React.CSSProperties = { width: "85%" }

const createFullName = ({ name, city }: PhotonProperties): string => {
  if (name && city) {
    return `${name}, ${city}`
  } else {
    return name || (city as string)
  }
}

export const LocalizationAutoComplete: React.FC<
  LocalizationAutoCompleteProps
> = ({ index, placeholder }) => {
  const queryClient = useQueryClient()
  const { orderMap } = useMap()
  const dispatch = useAppDispatch()

  const { latestStepIndex } = useAppSelector(state => state.createNewOrder)
  const step = useAppSelector(state => state.createNewOrder.steps[index])

  const fetchFeatures = async (value: string) => {
    const features = await fetchFeaturesByQuery(queryClient, value)

    const localizations = features
      .filter(({ properties }) => !!properties.name || !!properties.city)
      .map(({ properties, geometry }) => ({
        value: createFullName(properties),
        lat: geometry.coordinates[1],
        lon: geometry.coordinates[0],
      }))

    dispatch(updateLocalizationAutoComplete(localizations))
  }

  const fetchLocation = async (value: string) => {
    const [coordinates] = await fetchLocationByQuery(queryClient, value)

    if (!coordinates) {
      const emptyStep = { inputValue: value }
      return dispatch(updateStep({ index, step: emptyStep }))
    }

    const nextStep = {
      inputValue: value,
      lat: parseFloat(coordinates.lat),
      lon: parseFloat(coordinates.lon),
    }

    orderMap?.flyTo({
      center: [nextStep.lon!, nextStep.lat!],
      zoom: 15,
    })
    dispatch(updateStep({ index, step: nextStep }))
  }

  const onChange = (value: string) =>
    dispatch(updateStepInputValue({ index, inputValue: value }))

  const onSearch = useMemo(
    () =>
      debounce(async (value: string) => {
        await Promise.all([fetchFeatures(value), fetchLocation(value)])
        dispatch(updateLatestStepIndex(index))
      }, 1000),
    [orderMap],
  )

  useEffect(() => {
    return () => {
      onSearch.cancel()
    }
  }, [onSearch])

  const onClick = () => {
    if (latestStepIndex !== index) {
      dispatch(clearLocalizationAutoComplete())
    }
    dispatch(updateLatestStepIndex(index))
  }

  return (
    <AutoComplete
      style={autoCompleteStyle}
      value={step.inputValue}
      onChange={onChange}
      onSearch={onSearch}
      onClick={onClick}
    >
      <Input size="large" placeholder={placeholder} />
    </AutoComplete>
  )
}
