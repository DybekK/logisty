import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useTranslation } from "react-i18next"

import {
  CarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  RightCircleOutlined,
} from "@ant-design/icons"
import {
  Button,
  Card,
  Descriptions,
  Divider,
  Empty,
  Space,
  Spin,
  Tag,
  Timeline,
  Typography,
  message,
} from "antd"

import { Geolocation } from "@capacitor/geolocation"

import { createGoogleMapsLink, useAppSelector } from "@/common"
import { OrderStatus, StatusTag } from "@/features/order"

import { reportOrder, useFetchUpcomingOrder } from "../order.api"

const { Title, Text } = Typography

const spinnerContainerStyle: React.CSSProperties = {
  display: "flex",
  justifyContent: "center",
  padding: "24px",
}

const emptyContainerStyle: React.CSSProperties = {
  padding: "16px 0",
}

const verticalSpaceStyle: React.CSSProperties = {
  width: "100%",
}

const headerContainerStyle: React.CSSProperties = {
  width: "100%",
  display: "flex",
  flexDirection: "column",
  gap: "12px",
}

const statusContainer: React.CSSProperties = {
  display: "flex",
  width: "100%",
  gap: "8px",
  flexDirection: "column",
}

const spaceBetweenStyle: React.CSSProperties = {
  width: "100%",
  justifyContent: "space-between",
}

const statusRowStyle: React.CSSProperties = {
  display: "flex",
  gap: "8px",
  alignItems: "center",
  justifyContent: "space-between",
}

const titleStyle: React.CSSProperties = {
  margin: 0,
}

const dividerStyle: React.CSSProperties = {
  margin: "12px 0",
}

const successIconStyle: React.CSSProperties = {
  color: "#52c41a",
}

const arrivalButtonStyle: React.CSSProperties = {
  marginTop: "12px",
  width: "100%",
}

const stepsTitleStyle: React.CSSProperties = {
  marginBottom: "24px",
  marginTop: "0px",
}

export const UpcomingOrderForm = () => {
  const { t } = useTranslation("order")
  const { fleetId, userId: driverId } = useAppSelector(
    state => state.auth.user!,
  )
  const queryClient = useQueryClient()

  const {
    data: order,
    isLoading,
    refetch,
  } = useFetchUpcomingOrder({
    fleetId,
    driverId,
  })

  const { mutateAsync: reportArrival, isPending: reportArrivalPending } =
    useMutation({
      mutationFn: ({
        stepId,
        lat,
        lon,
      }: {
        stepId: string
        lat: number
        lon: number
      }) =>
        reportOrder(fleetId, order!.orderId, stepId, {
          arrivedAt: new Date().toISOString(),
          lat,
          lon,
        }),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["fetchUpcomingOrder"] })
      },
    })

  const handleNavigationClick = () => {
    if (!order?.steps?.length) return

    const coordinates = order.steps
      .filter(
        step => step.location.coordinates[0] && step.location.coordinates[1],
      )
      .map(
        step =>
          [step.location.coordinates[0], step.location.coordinates[1]] as [
            number,
            number,
          ],
      )

    if (coordinates.length >= 2) {
      const mapsLink = createGoogleMapsLink(coordinates)
      window.open(mapsLink, "_blank")
    }
  }

  const handleReportArrival = async (stepId: string) => {
    try {
      const coordinates = await Geolocation.getCurrentPosition({
        enableHighAccuracy: true,
      })

      const { latitude: lat, longitude: lon } = coordinates.coords
      await reportArrival({ stepId, lat, lon })
      await refetch()

      message.success(t("upcoming.reportArrivalSuccess"))
    } catch (error) {
      message.error(t("upcoming.error.reportArrival"))
    }
  }

  if (isLoading) {
    return (
      <Card>
        <div style={spinnerContainerStyle}>
          <Spin size="large" />
        </div>
      </Card>
    )
  }

  if (!order) {
    return (
      <Card>
        <div style={emptyContainerStyle}>
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={t("upcoming.empty")}
          ></Empty>
        </div>
      </Card>
    )
  }

  const getCurrentStepId = () => {
    if (!order?.steps) return null
    const currentStep = order.steps.find(step => !step.actualArrivalAt)
    return currentStep?.orderStepId
  }

  return (
    <Card bodyStyle={{ padding: "16px" }}>
      <Space direction="vertical" size="middle" style={verticalSpaceStyle}>
        <div style={headerContainerStyle}>
          <Space direction="vertical" size={0}>
            <Title level={4} style={titleStyle}>
              {t("upcoming.title")}
            </Title>
            <Text type="secondary">ID: {order.orderId}</Text>
          </Space>
          <div style={statusContainer}>
            <div style={statusRowStyle}>
              <StatusTag status={order.status} />
              <Button
                type="link"
                onClick={handleNavigationClick}
                icon={<EnvironmentOutlined />}
              >
                {t("upcoming.navigate")}
              </Button>
            </div>
            {order.status === OrderStatus.ASSIGNED && (
              <Button
                type="primary"
                icon={<CarOutlined />}
                onClick={() => handleReportArrival(order.steps[0].orderStepId)}
                loading={reportArrivalPending}
                block
              >
                {t("upcoming.startOrder")}
              </Button>
            )}
          </div>
        </div>

        <Divider style={dividerStyle} />

        <Descriptions column={1}>
          <Descriptions.Item label={t("filter.driver.estimatedStart")}>
            <Tag color="blue" icon={<ClockCircleOutlined />}>
              {new Date(order.estimatedStartedAt).toLocaleString()}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label={t("filter.driver.estimatedEnd")}>
            <Tag color="green" icon={<ClockCircleOutlined />}>
              {new Date(order.estimatedEndedAt).toLocaleString()}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label={t("upcoming.totalDistance")}>
            <Tag>{(order.route.distance / 1000).toFixed(1)} km</Tag>
          </Descriptions.Item>
          <Descriptions.Item label={t("upcoming.estimatedDuration")}>
            <Tag>{Math.round(order.route.duration / 60)} min</Tag>
          </Descriptions.Item>
        </Descriptions>

        <Divider style={dividerStyle} />

        <div>
          <Title style={stepsTitleStyle} level={5}>
            <Space>
              <RightCircleOutlined />
              {t("upcoming.deliverySteps")}
            </Space>
          </Title>
          <Timeline
            items={order.steps.map((step, index) => {
              const isCurrentStep = step.orderStepId === getCurrentStepId()
              return {
                dot: step.actualArrivalAt ? (
                  <CheckCircleOutlined style={successIconStyle} />
                ) : (
                  <EnvironmentOutlined />
                ),
                color: step.actualArrivalAt ? "green" : "blue",
                children: (
                  <Space direction="vertical">
                    <Text strong>{`${index + 1}. ${step.description}`}</Text>
                    <Space style={spaceBetweenStyle}>
                      {`${t("upcoming.expectedStartTime")}:`}
                      <Tag color="blue">
                        {new Date(
                          step.estimatedArrivalAt || order.estimatedStartedAt,
                        ).toLocaleString()}
                      </Tag>
                    </Space>
                    {step.actualArrivalAt ? (
                      <Space style={spaceBetweenStyle}>
                        {`${t("upcoming.actualArrivalAt")}:`}
                        <Tag color="success">
                          {new Date(step.actualArrivalAt).toLocaleString()}
                        </Tag>
                      </Space>
                    ) : (
                      isCurrentStep &&
                      order.status !== OrderStatus.ASSIGNED && (
                        <Button
                          style={arrivalButtonStyle}
                          type="primary"
                          onClick={() => handleReportArrival(step.orderStepId)}
                          loading={reportArrivalPending}
                        >
                          {t("upcoming.reportArrival")}
                        </Button>
                      )
                    )}
                  </Space>
                ),
              }
            })}
          />
        </div>
      </Space>
    </Card>
  )
}
