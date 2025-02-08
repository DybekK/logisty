import { useTranslation } from "react-i18next"

import { BellOutlined } from "@ant-design/icons"
import { Badge } from "antd"
import { Typography } from "antd"
import { Popover } from "antd"

import { useAppSelector } from "@/common/store"
import { NotificationList } from "@/features/notification"

export const NotificationPopover = () => {
  const { t } = useTranslation("layout")

  const { notifications, read } = useAppSelector(state => state.notification)

  const unreadCount = notifications.filter(
    n => !read.includes(n.eventId),
  ).length
  return (
    <Popover
      content={<NotificationList />}
      title={
        <Typography.Title level={5} style={{ margin: 0 }}>
          {t("navbar.notifications")}
        </Typography.Title>
      }
      trigger="click"
      placement="bottomRight"
    >
      <Badge count={unreadCount} size="small">
        <BellOutlined style={{ fontSize: "16px" }} />
      </Badge>
    </Popover>
  )
}
