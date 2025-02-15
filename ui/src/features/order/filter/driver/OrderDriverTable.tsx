import React from "react"
import { useTranslation } from "react-i18next"

import { Button, Empty, List } from "antd"

import { useAppSelector } from "@/common"
import { OrderDriverItem } from "@/features/order/filter"
import { useFetchOrders } from "@/features/order/order.api"

const listContainerStyle: React.CSSProperties = {
  width: "100%",
  overflowY: "auto",
  height: "calc(100vh - 64px)",
}

export const OrderDriverTable: React.FC = () => {
  const { t } = useTranslation("order", { keyPrefix: "filter" })
  const { fleetId } = useAppSelector(state => state.auth.user!)

  const page = 1
  const pageSize = 10

  const { data, isLoading } = useFetchOrders({
    fleetId: fleetId,
    page: page - 1,
    limit: pageSize,
  })

  return (
    <div style={listContainerStyle}>
      <List
        dataSource={data?.orders}
        loading={isLoading}
        renderItem={order => (
          <List.Item key={order.orderId}>
            <OrderDriverItem order={order} />
          </List.Item>
        )}
        locale={{
          emptyText: (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={t("empty")}
            />
          ),
        }}
        loadMore={
          data?.total && data.total > pageSize ? (
            <div style={{ textAlign: "center", marginTop: 12 }}>
              <Button>Load More</Button>
            </div>
          ) : null
        }
      />
    </div>
  )
}
