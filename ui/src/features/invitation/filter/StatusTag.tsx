import { useTranslation } from "react-i18next"

import { Tag } from "antd"

import { match } from "ts-pattern"

import { InvitationStatus } from "@/features/invitation"

interface StatusTagProps {
  status: InvitationStatus
}

export const StatusTag: React.FC<StatusTagProps> = ({ status }) => {
  const { t } = useTranslation("invitation")

  const color = match(status)
    .with(InvitationStatus.PENDING, () => "geekblue")
    .with(InvitationStatus.ACCEPTED, () => "green")
    .with(InvitationStatus.EXPIRED, () => "gray")
    .exhaustive()

  return (
    <Tag color={color} key={status}>
      {t(`filterInvitations.statuses.${status}`)}
    </Tag>
  )
}
