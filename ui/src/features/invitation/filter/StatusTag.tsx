import { Tag } from "antd"

import { match } from "ts-pattern"

import { InvitationStatus } from "@/features/invitation"

interface StatusTagProps {
  status: InvitationStatus
}

export const StatusTag: React.FC<StatusTagProps> = ({ status }) => {
  const color = match(status)
    .with(InvitationStatus.PENDING, () => "geekblue")
    .with(InvitationStatus.ACCEPTED, () => "green")
    .with(InvitationStatus.REJECTED, () => "red")
    .exhaustive()

  return (
    <Tag color={color} key={status}>
      {status.toUpperCase()}
    </Tag>
  )
}
