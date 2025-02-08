import { useTranslation } from "react-i18next"

import { Empty, List, Typography } from "antd"

import { useAppDispatch, useAppSelector } from "@/common/store"
import { getNotificationIcon, markAsRead } from "@/features/notification"

export const NotificationList: React.FC = () => {
  const { t } = useTranslation("layout")
  const dispatch = useAppDispatch()
  const { notifications, read } = useAppSelector(state => state.notification)

  const unreadCount = notifications.filter(
    n => !read.includes(n.eventId),
  ).length

  return (
    <div>
      {unreadCount > 0 && (
        <div style={{ padding: "0 12px 12px", textAlign: "left" }}>
          <Typography.Link
            onClick={() => {
              notifications.forEach(notification => {
                dispatch(markAsRead(notification.eventId))
              })
            }}
          >
            {t("navbar.markAllAsRead")}
          </Typography.Link>
        </div>
      )}
      <List
        style={{ width: 300, maxHeight: 400, overflow: "auto" }}
        itemLayout="vertical"
        dataSource={notifications}
        locale={{
          emptyText: (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={t("navbar.noNotifications")}
            />
          ),
        }}
        renderItem={item => (
          <List.Item
            style={{
              backgroundColor: read.includes(item.eventId)
                ? "transparent"
                : "rgba(24, 144, 255, 0.05)",
              padding: "12px",
              cursor: "pointer",
            }}
            onClick={() => {
              dispatch(markAsRead(item.eventId))
            }}
          >
            <List.Item.Meta
              avatar={getNotificationIcon(item.notificationType)}
              title={<Typography.Text strong>{item.title}</Typography.Text>}
              description={
                <div>
                  <Typography.Text>{item.message}</Typography.Text>
                  <Typography.Text
                    type="secondary"
                    style={{
                      display: "block",
                      fontSize: "12px",
                    }}
                  >
                    {new Date(item.appendedAt).toLocaleString()}
                  </Typography.Text>
                </div>
              }
            />
          </List.Item>
        )}
      />
    </div>
  )
}
