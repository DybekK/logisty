import { useQueryClient } from "@tanstack/react-query"
import React, { useEffect } from "react"
import { useTranslation } from "react-i18next"
import { MapProvider } from "react-map-gl"

import { CheckOutlined, PlusCircleOutlined } from "@ant-design/icons"
import { Avatar, Button, Card, Divider, Flex, Form, Steps } from "antd"

import { useAppDispatch, useAppSelector } from "@/common"
import { Map3D } from "@/components"
import {
  CreateNewOrderStep,
  addStep,
  fetchGeneratedPathByCoordinates,
  updateRoutesAndWaypoints,
} from "@/features/order"
import { LocalizationAutoCompleteElement, Step } from "@/features/order/new"

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

const buttonStyle: React.CSSProperties = {
  width: "100%",
  textAlign: "left",
  justifyContent: "flex-start",
  padding: "4px 11px",
}

const buttonTitleStyle: React.CSSProperties = {
  textAlign: "left",
  width: "250px",
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap",
}

const addStepButtonStyle: React.CSSProperties = { ...buttonStyle }
const acceptOrderButtonStyle: React.CSSProperties = {
  ...buttonStyle,
  marginTop: 10,
}

const driverAvatarStyle: React.CSSProperties = {
  backgroundColor: "#f56a00",
  marginRight: 8,
}

const driverStatusStyle: React.CSSProperties = {
  width: 8,
  height: 8,
  borderRadius: "50%",
  backgroundColor: "#52c41a", // Green for available
  marginLeft: "auto",
  marginRight: 8,
}

const mapId = "orderMap"

interface Driver {
  id: string
  name: string
}

const drivers: Driver[] = [
  {
    id: "1",
    name: "Jan Kowalski",
  },
  {
    id: "2",
    name: "Adam Nowak",
  },
  {
    id: "3",
    name: "Janusz Tracz",
  },
]

const isStepsValid = (steps: CreateNewOrderStep[]): boolean =>
  steps.some(
    ({ lat, lon, inputValue }) => !lat || !lon || inputValue.trim() === "",
  )

export const NewOrderForm: React.FC = () => {
  const queryClient = useQueryClient()
  const dispatch = useAppDispatch()

  const { t } = useTranslation("order", { keyPrefix: "new" })
  const { steps, routes, waypoints, localizationsAutoComplete } =
    useAppSelector(state => state.createNewOrder)

  useEffect(() => {
    if (steps.filter(step => !!step.lat).length < 2) return

    fetchGeneratedPathByCoordinates(queryClient, steps).then(
      ({ routes, waypoints }) =>
        dispatch(updateRoutesAndWaypoints({ routes, waypoints })),
    )
  }, [steps])

  return (
    <MapProvider>
      <Card bodyStyle={cardBodyStyle} style={cardStyle}>
        <Flex style={flexStyle}>
          <Form>
            <Steps progressDot direction="vertical" current={steps.length - 1}>
              {steps.map((_, index) => (
                <Steps.Step key={index} description={<Step index={index} />} />
              ))}
            </Steps>
            <Button
              style={addStepButtonStyle}
              onClick={() => dispatch(addStep())}
              size="large"
              icon={<PlusCircleOutlined />}
            >
              {t("addStep")}
            </Button>
            <Button
              style={acceptOrderButtonStyle}
              size="large"
              type="text"
              icon={<CheckOutlined />}
              disabled={isStepsValid(steps)}
            >
              {t("acceptOrder")}
            </Button>
          </Form>
          <Divider />
          <Flex style={{ flexDirection: "column" }}>
            {localizationsAutoComplete.map((item, index) => (
              <LocalizationAutoCompleteElement
                key={index}
                localization={item}
              />
            ))}
            {localizationsAutoComplete.length > 0 && <Divider />}
            {drivers.map(driver => (
              <Button
                key={driver.id}
                style={buttonStyle}
                size="large"
                type="text"
              >
                <Avatar style={driverAvatarStyle} size="small">
                  {driver.name.charAt(0)}
                </Avatar>
                <span style={buttonTitleStyle}>{driver.name}</span>
                <span style={driverStatusStyle} title="Available" />
              </Button>
            ))}
          </Flex>
        </Flex>
        <Map3D id={mapId} routes={routes} waypoints={waypoints} />
      </Card>
    </MapProvider>
  )
}
