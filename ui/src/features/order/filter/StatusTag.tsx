import { Tag } from "antd"

import { match } from "ts-pattern"

import { OrderStatus } from "@/features/order"

interface StatusTagProps {
  status: OrderStatus
}

export const StatusTag: React.FC<StatusTagProps> = ({ status }) => {
  const color = match(status)
    .with(OrderStatus.COMPLETED, () => "green")
    .with(OrderStatus.PENDING, () => "geekblue")
    .exhaustive()

  return (
    <Tag color={color} key={status}>
      {status.toUpperCase()}
    </Tag>
  )
}
