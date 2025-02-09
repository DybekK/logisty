import { useMutation, useQueryClient } from "@tanstack/react-query"
import React, { useEffect } from "react"
import { useTranslation } from "react-i18next"
import { MapProvider } from "react-map-gl"
import dayjs from 'dayjs'

import { CheckOutlined, PlusCircleOutlined } from "@ant-design/icons"
import { Avatar, Button, Card, DatePicker, Divider, Flex, Form, message, Steps } from "antd"

import { OSRMRoute, useAppDispatch, useAppSelector } from "@/common"
import { Map3D } from "@/components"
import {
  CreateNewOrderStep,
  addStep,
  createOrder,
  fetchGeneratedPathByCoordinates,
  reset,
  setStartDate,
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

const datePickerContainerStyle: React.CSSProperties = { 
  marginBottom: 11, 
  marginLeft: 24 
}

const datePickerStyle: React.CSSProperties = { 
  height: 40, 
  width: "85%" 
}

const flexColumnStyle: React.CSSProperties = { 
  flexDirection: "column" 
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

const hasInvalidSteps = (steps: CreateNewOrderStep[]): boolean =>
  steps.some(
    ({ lat, lon, inputValue }) => !lat || !lon || inputValue.trim() === "",
  )

const isStartDateValid = (startDate: string): boolean =>
  dayjs(startDate).isAfter(dayjs())

const hasValidInput = (startDate: string, steps: CreateNewOrderStep[]): boolean =>
  !hasInvalidSteps(steps) && isStartDateValid(startDate)

const calculateStepTimes = (startDate: string, routes: OSRMRoute[]): { 
  stepTimes: { start: Date, end: Date }[], 
  courseEnd: Date 
} => {
  if (!routes || routes.length === 0) {
    const defaultTime = new Date(startDate)
    return {
      stepTimes: [{ start: defaultTime, end: defaultTime }],
      courseEnd: defaultTime
    }
  }

  let currentTime = new Date(startDate)
  const stepTimes: { start: Date, end: Date }[] = []
  
  // Add first step at the starting location
  stepTimes.push({
    start: currentTime,
    end: currentTime
  })
  
  routes.forEach((route) => {
    if (!route) return
    
    // Calculate total duration for the entire route
    const routeDuration = route.duration || 
      route.legs.reduce((total, leg) => total + (leg.duration || 0), 0)
    
    const stepStart = new Date(currentTime)
    const stepEnd = new Date(currentTime.getTime() + routeDuration * 1000)
    
    stepTimes.push({ start: stepStart, end: stepEnd })
    currentTime = stepEnd
  })
  
  return {
    stepTimes,
    courseEnd: currentTime
  }
}

export const NewOrderForm: React.FC = () => {
  const { t } = useTranslation("order", { keyPrefix: "new" })
  const queryClient = useQueryClient()
  const dispatch = useAppDispatch()

  const { fleetId, userId } = useAppSelector(state => state.auth.user!)
  const { steps, routes, localizationsAutoComplete, startDate } =
    useAppSelector(state => state.createNewOrder)

  useEffect(() => {
    if (steps.filter(step => !!step.lat).length < 2) return

    fetchGeneratedPathByCoordinates(queryClient, steps).then(
      ({ routes, waypoints }) =>
        dispatch(updateRoutesAndWaypoints({ routes, waypoints })),
    )
  }, [steps])

  const { mutateAsync: createOrderMutate } = useMutation({
    mutationFn: () => {
      const { stepTimes, courseEnd } = calculateStepTimes(startDate!, routes)

      console.log(stepTimes, courseEnd)
      
      return createOrder(fleetId, {
        driverId: "d40e3a18-b560-4d69-a469-a8a50685c850",
        steps: steps.map((step, index) => ({
          description: step.inputValue,
          lat: step.lat ?? 0,
          lon: step.lon ?? 0,
          estimatedStartedAt: stepTimes[index].start.toISOString(),
          estimatedEndedAt: stepTimes[index].end.toISOString(),
        })),
        route: {
          distance: routes[0].distance,
          duration: routes[0].duration,
          route: routes[0].geometry
        },
        createdBy: userId,
        estimatedStartedAt: startDate!,
        estimatedEndedAt: courseEnd.toISOString(),
      })
    },
    onSuccess: () => {
      message.success(t("success"))
      dispatch(reset())
    },
  })

  const handleStartDateChange = (date: dayjs.Dayjs | null) =>
    dispatch(setStartDate(date ? date.toISOString() : ''))

  const handleAcceptOrder = () =>
    createOrderMutate()

  return (
    <MapProvider>
      <Card bodyStyle={cardBodyStyle} style={cardStyle}>
        <Flex style={flexStyle}>
          <Form>
            <Form.Item style={datePickerContainerStyle}>
              <DatePicker
                showTime
                style={datePickerStyle}
                placeholder={t("startDate")}
                size="middle"
                value={startDate ? dayjs(startDate) : null}
                onChange={handleStartDateChange}
              />
            </Form.Item>
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
              onClick={handleAcceptOrder}
              style={acceptOrderButtonStyle}
              size="large"
              ghost
              type="primary"
              icon={<CheckOutlined />}
              disabled={!hasValidInput(startDate!, steps)}
            >
              {t("acceptOrder")}
            </Button>
          </Form>
          <Divider />
          <Flex style={flexColumnStyle}>
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
        <Map3D id={mapId} routes={routes.map(route => route.geometry)} />
      </Card>
    </MapProvider>
  )
}
