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
import { Card, Collapse, Empty, Space, Table, Tag } from "antd"
import type { ColumnsType } from "antd/es/table"

import { useAppSelector } from "@/common"
import { Map3D } from "@/components"
import { StatusTag } from "@/features/order"
import { useFetchOrders } from "@/features/order/order.api"
import { GetOrderResponse, OrderStatus } from "@/features/order/order.types"

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

const spaceBetweenStyle: React.CSSProperties = {
  width: "100%",
  justifyContent: "space-between",
}

const estimatedTimesContentStyle: React.CSSProperties = {
  marginLeft: 24,
  marginTop: 0,
}

const timeValueStyle: React.CSSProperties = {
  fontWeight: 500,
}

const stepItemStyle: React.CSSProperties = {
  padding: "8px 0",
  borderBottom: "1px solid #f0f0f0",
}

const stepDescriptionStyle: React.CSSProperties = {
  fontWeight: 500,
  marginBottom: 4,
}

export const OrderDispatcherTable: React.FC = () => {
  const { t } = useTranslation("order", { keyPrefix: "filter.dispatcher" })
  const { fleetId } = useAppSelector(state => state.auth.user!)
  const page = 1
  const pageSize = 10
  const { data, isLoading } = useFetchOrders({
    fleetId: fleetId,
    page: page - 1,
    limit: pageSize,
  })
  const [selectedRow, setSelectedRow] = React.useState<GetOrderResponse | null>(
    null,
  )
  const selectedRoute = selectedRow
    ? {
        coordinates: selectedRow.route.route.coordinates,
      }
    : undefined
  const selectedDriverRoute = selectedRow?.route.routePoints
    ? {
        coordinates: selectedRow.route.routePoints.coordinates,
      }
    : undefined

  const rowSelection = {
    type: "radio" as const,
    onChange: (
      _selectedRowKeys: React.Key[],
      selectedRows: GetOrderResponse[],
    ) => {
      setSelectedRow(selectedRows[0] || null)
    },
  }
  const columns: ColumnsType<GetOrderResponse> = [
    {
      title: t("status"),
      key: "status",
      dataIndex: "status",
      render: (status: OrderStatus) => <StatusTag status={status} />,
    },
    {
      title: t("orderId"),
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
      title: t("driver"),
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
                {t("estimatedTimes")}
              </Space>
            ),
            children: (
              <Space direction="vertical" style={estimatedTimesContentStyle}>
                <Space>
                  <CarOutlined />
                  {`${t("estimatedStart")}:`}
                  <Tag color="blue" style={timeValueStyle}>
                    {new Date(record.estimatedStartedAt).toLocaleString()}
                  </Tag>
                </Space>
                <Space>
                  <EnvironmentOutlined />
                  {`${t("estimatedEnd")}:`}
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
                {t("orderSteps")}
              </Space>
            ),
            children: (
              <div style={estimatedTimesContentStyle}>
                {record.steps?.map((step, index) => (
                  <div key={index} style={stepItemStyle}>
                    <div style={stepDescriptionStyle}>
                      {`${index + 1}. ${step.description}`}
                    </div>
                    <Space direction="vertical" style={{ width: "100%" }}>
                      <Space style={spaceBetweenStyle}>
                        <Space>
                          <ClockCircleOutlined />
                          {`${t("expectedStartTime")}:`}
                        </Space>
                        <Tag color="blue" style={timeValueStyle}>
                          {new Date(
                            step.estimatedArrivalAt ||
                              record.estimatedStartedAt,
                          ).toLocaleString()}
                        </Tag>
                      </Space>
                      {step.actualArrivalAt && (
                        <Space style={spaceBetweenStyle}>
                          <Space>
                            <ClockCircleOutlined />
                            {`${t("actualArrivalAt")}:`}
                          </Space>
                          <Tag color="success" style={timeValueStyle}>
                            {new Date(step.actualArrivalAt).toLocaleString()}
                          </Tag>
                        </Space>
                      )}
                    </Space>
                  </div>
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
            components={{
              body: {
                cell: (props: any) => (
                  <td {...props} style={{ borderBottom: "none" }} />
                ),
              },
            }}
            rowSelection={rowSelection}
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
        <div style={mapContainerStyle}>
          <Map3D
            id="filtrOrderMap"
            plannedRoute={selectedRoute}
            driverRoute={selectedDriverRoute}
            steps={selectedRow?.steps || []}
          />
        </div>
      </Card>
    </MapProvider>
  )
}
