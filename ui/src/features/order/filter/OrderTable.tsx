import React from "react"
import { useTranslation } from "react-i18next"
import { MapProvider } from "react-map-gl"

import {
  CalendarOutlined,
  CarOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  InfoCircleOutlined,
  RightCircleOutlined,
  UserOutlined,
} from "@ant-design/icons"
import { Card, Collapse, Divider, Empty, Space, Table, Tag } from "antd"
import type { ColumnsType } from "antd/es/table"

import { useAppSelector } from "@/common"
import { Map3D } from "@/components"
import { useFetchOrders } from "@/features/order/order.api"
import { GetOrderResponse } from "@/features/order/order.types"

const cardBodyStyle: React.CSSProperties = {
  height: "100%",
  padding: 0,
  display: "flex",
  flexDirection: "row",
}

const cardStyle: React.CSSProperties = {
  height: "100%",
  width: "100%",
}

const listContainerStyle: React.CSSProperties = {
  flexDirection: "column",
  zIndex: 1,
  width: "40%",
  padding: 20,
  boxShadow: "8px 0px 15px -5px rgba(0, 0, 0, 0.025)",
  overflowY: "auto",
}

const mapContainerStyle: React.CSSProperties = {
  flex: 1,
  height: "100%",
  position: "relative",
}

const tableStyle: React.CSSProperties = {
  width: "100%",
}

const dividerStyle: React.CSSProperties = {
  height: "100%",
}

const estimatedTimesContentStyle: React.CSSProperties = {
  marginLeft: 24,
  marginTop: 0,
}

const timeValueStyle: React.CSSProperties = {
  fontWeight: 500,
}

const orderStepCardStyle: React.CSSProperties = {
  marginBottom: 12,
}

export const OrderTable: React.FC = () => {
  const { t } = useTranslation("order", { keyPrefix: "filter" })

  const { fleetId } = useAppSelector(state => state.auth.user!)

  const page = 1
  const pageSize = 10

  const { data, isLoading } = useFetchOrders({
    fleetId: fleetId,
    page: page - 1,
    limit: pageSize,
  })

  const routes = data?.orders.map(order => order.route.route) ?? []

  const columns: ColumnsType<GetOrderResponse> = [
    {
      title: "Status",
      key: "status",
      render: () => (
        <Tag icon={<ClockCircleOutlined />} color="processing">
          Active
        </Tag>
      ),
    },
    {
      title: "Order ID",
      dataIndex: "orderId",
      key: "orderId",
      render: orderId => (
        <Space>
          <InfoCircleOutlined />
          {orderId.slice(-4)}
        </Space>
      ),
    },
    {
      title: "Driver",
      key: "driver",
      render: (_, record) => (
        <Space>
          <UserOutlined />
          <span>{`${record.driverFirstName} ${record.driverLastName}`}</span>
        </Space>
      ),
    },
  ]

  const expandedRowRender = (record: GetOrderResponse) => (
    <Space direction="vertical" style={{ width: "100%", marginTop: 0 }}>
      <Collapse
        defaultActiveKey={["1", "2"]}
        style={{ width: "100%" }}
        bordered={false}
        items={[
          {
            key: "1",
            label: (
              <Space>
                <CalendarOutlined />
                {t("estimatedTimes", "Estimated Times")}
              </Space>
            ),
            children: (
              <Space direction="vertical" style={estimatedTimesContentStyle}>
                <Space>
                  <CarOutlined />
                  {`${t("estimatedStart", "Start")}:`}
                  <Tag color="blue" style={timeValueStyle}>
                    {new Date(record.estimatedStartedAt).toLocaleString()}
                  </Tag>
                </Space>
                <Space>
                  <EnvironmentOutlined />
                  {`${t("estimatedEnd", "End")}:`}
                  <Tag color="green" style={timeValueStyle}>
                    {new Date(record.estimatedEndedAt).toLocaleString()}
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
                {t("orderSteps", "Order Steps")}
              </Space>
            ),
            children: (
              <div style={estimatedTimesContentStyle}>
                {record.steps?.map((step, index) => (
                  <Card
                    key={index}
                    size="small"
                    style={orderStepCardStyle}
                    title={`${index + 1}. ${step.description}`}
                  >
                    <Space direction="vertical" style={{ width: "100%" }}>
                      <Space>
                        <ClockCircleOutlined />
                        {`${t(
                          step.estimatedArrivalAt
                            ? "expectedStartTime"
                            : "plannedStartTime",
                          step.estimatedArrivalAt
                            ? "Expected Arrival Time"
                            : "Planned Start Time",
                        )}:`}
                        <Tag color="purple" style={timeValueStyle}>
                          {new Date(
                            step.estimatedArrivalAt ||
                              record.estimatedStartedAt,
                          ).toLocaleString()}
                        </Tag>
                      </Space>
                    </Space>
                  </Card>
                ))}
              </div>
            ),
          },
        ]}
      />
    </Space>
  )

  return (
    <MapProvider>
      <Card bodyStyle={cardBodyStyle} style={cardStyle}>
        <div style={listContainerStyle}>
          <Table
            style={tableStyle}
            columns={columns}
            dataSource={data?.orders}
            loading={isLoading}
            locale={{
              emptyText: (
                <Empty
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                  description={t("empty")}
                />
              ),
            }}
            expandable={{
              expandedRowRender,
              expandRowByClick: true,
            }}
            pagination={{
              current: page,
              total: data?.total,
              pageSize: pageSize,
              showSizeChanger: false,
            }}
            rowKey="orderId"
          />
        </div>

        <Divider type="vertical" style={dividerStyle} />

        <div style={mapContainerStyle}>
          <Map3D id="filtrOrderMap" routes={routes} />
        </div>
      </Card>
    </MapProvider>
  )
}
