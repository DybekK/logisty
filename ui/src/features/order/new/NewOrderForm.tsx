import { useMutation, useQueryClient } from "@tanstack/react-query"
import React, { useEffect } from "react"
import { useTranslation } from "react-i18next"
import { MapProvider } from "react-map-gl"

import { Card, Divider, Flex, message } from "antd"

import dayjs from "dayjs"

import { OSRMRoute, useAppDispatch, useAppSelector } from "@/common"
import { Map3D } from "@/components/Map3D/Map3D"
import { DriversPanel, RouteForm } from "@/features/order"
import {
  CreateNewOrderStep,
  createOrder,
  fetchGeneratedPathByCoordinates,
  reset,
  updateEstimatedTimes,
  updateRoutesAndWaypoints,
} from "@/features/order"

const cardBodyStyle: React.CSSProperties = {
  height: "100%",
  padding: 0,
  display: "flex",
  flexDirection: "row",
}

const cardStyle: React.CSSProperties = { height: "100%" }

const flexStyle: React.CSSProperties = {
  flexDirection: "column",
  zIndex: 1,
  width: 450,
  padding: 20,
  boxShadow: "8px 0px 15px -5px rgba(0, 0, 0, 0.025)",
}

const mapId = "orderMap"

interface StepTimes {
  estimatedArrivalAt: string[]
  estimatedEndedAt: string
}

const hasInvalidSteps = (steps: CreateNewOrderStep[]): boolean =>
  steps.some(
    ({ lat, lon, inputValue }) => !lat || !lon || inputValue.trim() === "",
  )

const isStartDateValid = (startDate?: string): boolean =>
  startDate ? dayjs(startDate).isAfter(dayjs()) : true

const hasValidInput = (
  steps: CreateNewOrderStep[],
  startDate?: string,
  selectedDriverId?: string,
): boolean =>
  !hasInvalidSteps(steps) && isStartDateValid(startDate) && !!selectedDriverId

const calculateStepTimes = (
  startDate: string,
  routes: OSRMRoute[],
): StepTimes => {
  const { times, currentDate } = routes
    .flatMap(route => route.legs)
    .reduce<{ currentDate: Date; times: Date[] }>(
      (acc, leg) => {
        const arrivalAt = new Date(
          acc.currentDate.getTime() + leg.duration * 1000,
        )

        return {
          currentDate: arrivalAt,
          times: [...acc.times, arrivalAt],
        }
      },
      { currentDate: new Date(startDate), times: [] },
    )

  return {
    estimatedArrivalAt: times.map(time => time.toISOString()),
    estimatedEndedAt: currentDate.toISOString(),
  }
}

export const NewOrderForm: React.FC = () => {
  const { t } = useTranslation("order", { keyPrefix: "new" })
  const queryClient = useQueryClient()
  const dispatch = useAppDispatch()

  const { fleetId, userId } = useAppSelector(state => state.auth.user!)
  const { steps, routes, startDate, estimatedEndedAt, selectedDriverId } =
    useAppSelector(state => state.createNewOrder)

  useEffect(() => {
    if (steps.filter(step => !!step.lat).length < 2) return

    fetchGeneratedPathByCoordinates(queryClient, steps).then(
      ({ routes, waypoints }) => {
        if (startDate) {
          const { estimatedArrivalAt, estimatedEndedAt } = calculateStepTimes(
            startDate!,
            routes,
          )

          dispatch(
            updateEstimatedTimes({ estimatedArrivalAt, estimatedEndedAt }),
          )
        }

        dispatch(updateRoutesAndWaypoints({ routes, waypoints }))
      },
    )
  }, [steps, startDate])

  const { mutateAsync: createOrderMutate } = useMutation({
    mutationFn: () => {
      return createOrder(fleetId, {
        driverId: selectedDriverId!,
        steps: steps.map(step => ({
          description: step.inputValue,
          lat: step.lat!,
          lon: step.lon!,
          estimatedArrivalAt: step.estimatedArrivalAt!,
        })),
        route: {
          distance: routes[0].distance,
          duration: routes[0].duration,
          route: routes[0].geometry,
        },
        createdBy: userId,
        estimatedStartedAt: startDate!,
        estimatedEndedAt: estimatedEndedAt!,
      })
    },
    onSuccess: () => {
      message.success(t("success"))
      dispatch(reset())
    },
  })

  return (
    <MapProvider>
      <Card bodyStyle={cardBodyStyle} style={cardStyle}>
        <Flex style={flexStyle}>
          <RouteForm
            onAcceptOrder={() => createOrderMutate()}
            hasValidInput={() =>
              hasValidInput(steps, startDate, selectedDriverId)
            }
          />
          <Divider />
          <DriversPanel />
        </Flex>
        <Map3D id={mapId} routes={routes.map(route => route.geometry)} />
      </Card>
    </MapProvider>
  )
}
