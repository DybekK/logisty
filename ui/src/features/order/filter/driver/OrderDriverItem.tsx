import React from "react"
import { useTranslation } from "react-i18next"

import {
  CalendarOutlined,
  CarOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  InfoCircleOutlined,
  RightCircleOutlined,
} from "@ant-design/icons"
import { Button, Card, Collapse, Space, Tag } from "antd"

import { GetOrderResponse } from "@/features/order/order.types"
import { StatusTag } from "@/features/order"

const cardStyle: React.CSSProperties = {
  width: "100%",
}

const estimatedTimesContentStyle: React.CSSProperties = {
  marginLeft: 24,
  marginTop: 0,
}

const timeValueStyle: React.CSSProperties = {
  fontWeight: 500,
}

const headerSpaceStyle: React.CSSProperties = {
  width: "100%",
  justifyContent: "space-between",
}

const stepItemStyle: React.CSSProperties = {
  padding: "8px 0",
  borderBottom: "1px solid #f0f0f0",
}

const stepDescriptionStyle: React.CSSProperties = {
  fontWeight: 500,
  marginBottom: 4,
}

interface OrderItemProps {
  order: GetOrderResponse
}

const createGoogleMapsLink = (steps: Array<[number, number]>): string => {
  if (steps.length < 2) return ""

  const locations = steps.map(([long, lat]) => `${lat},${long}`)

  if (/iPhone|iPad|iPod|Android/i.test(navigator.userAgent)) {
    return `comgooglemaps://?saddr=${locations[0]}&daddr=${locations[1]}&waypoints=${locations.slice(2).join("|")}&directionsmode=driving`
  }

  return `https://www.google.com/maps/dir/${locations.join("/")}`
}

export const OrderDriverItem: React.FC<OrderItemProps> = ({ order }) => {
  const { t } = useTranslation("order", { keyPrefix: "filter.driver" })

  const handleNavigationClick = () => {
    if (!order.steps?.length) return

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

  return (
    <Card style={cardStyle}>
      <Space direction="vertical" style={{ width: "100%" }}>
        <Space style={headerSpaceStyle}>
          <Space>
            <StatusTag status={order.status} />
            <Space>
              <InfoCircleOutlined />
              {order.orderId.slice(-4)}
            </Space>
          </Space>
          <Button
            type="link"
            onClick={handleNavigationClick}
            icon={<EnvironmentOutlined />}
          >
            {t("navigate")}
          </Button>
        </Space>

        <Collapse
          defaultActiveKey={[]}
          bordered={false}
          items={[
            {
              key: "1",
              label: (
                <Space>
                  <CalendarOutlined />
                  {t("estimatedTimes")}
                </Space>
              ),
              children: (
                <Space direction="vertical" style={estimatedTimesContentStyle}>
                  <Space>
                    <CarOutlined />
                    {`${t("estimatedStart")}:`}
                    <Tag color="blue" style={timeValueStyle}>
                      {new Date(order.estimatedStartedAt).toLocaleString()}
                    </Tag>
                  </Space>
                  <Space>
                    <EnvironmentOutlined />
                    {`${t("estimatedEnd")}:`}
                    <Tag color="green" style={timeValueStyle}>
                      {new Date(order.estimatedEndedAt).toLocaleString()}
                    </Tag>
                  </Space>
                </Space>
              ),
            },
            {
              key: "2",
              label: (
                <Space>
                  <RightCircleOutlined />
                  {t("orderSteps")}
                </Space>
              ),
              children: (
                <div style={estimatedTimesContentStyle}>
                  {order.steps?.map((step, index) => (
                    <div key={index} style={stepItemStyle}>
                      <div style={stepDescriptionStyle}>
                        {`${index + 1}. ${step.description}`}
                      </div>
                      <Space>
                        <ClockCircleOutlined />
                        {`${t(
                          step.estimatedArrivalAt
                            ? "expectedStartTime"
                            : "plannedStartTime"
                        )}:`}
                        <Tag color="purple" style={timeValueStyle}>
                          {new Date(
                            step.estimatedArrivalAt || order.estimatedStartedAt,
                          ).toLocaleString()}
                        </Tag>
                      </Space>
                    </div>
                  ))}
                </div>
              ),
            },
          ]}
        />
      </Space>
    </Card>
  )
}
