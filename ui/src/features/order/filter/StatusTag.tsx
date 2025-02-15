import { useTranslation } from "react-i18next"
import { ClockCircleOutlined } from "@ant-design/icons"
import { Tag } from "antd"
import { match } from "ts-pattern"

import { OrderStatus } from "@/features/order"

interface StatusTagProps {
  status: OrderStatus
}

export const StatusTag: React.FC<StatusTagProps> = ({ status }) => {
  const { t } = useTranslation("order", { keyPrefix: "filter.statuses" })

  const color = match(status)
    .with(OrderStatus.ASSIGNED, () => "blue")
    .with(OrderStatus.PENDING, () => "processing")
    .with(OrderStatus.COMPLETED, () => "success")
    .with(OrderStatus.CANCELLED, () => "error")
    .exhaustive()

  return (
    <Tag icon={<ClockCircleOutlined />} color={color}>
      {t(`${status}`)}
    </Tag>
  )
}
