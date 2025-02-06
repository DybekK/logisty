import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"

import {
  BellOutlined,
  CloseCircleOutlined,
  InfoCircleOutlined,
  LogoutOutlined,
  TranslationOutlined,
  WarningOutlined,
} from "@ant-design/icons"
import {
  Badge,
  Empty,
  Layout,
  List,
  Menu,
  MenuProps,
  Popover,
  Typography,
} from "antd"

import { useAppDispatch, useAppSelector } from "@/common/store"
import { useAuth } from "@/components"
import { NotificationType, markAsRead } from "@/features/notification"
import { Routes } from "@/router"

const { Header } = Layout

const menuStyle: React.CSSProperties = {
  background: "transparent",
  flex: 1,
  flexDirection: "row-reverse",
}

const getNotificationIcon = (type: NotificationType) => {
  switch (type) {
    case NotificationType.INFO:
      return <InfoCircleOutlined style={{ color: "#1890ff" }} />
    case NotificationType.WARNING:
      return <WarningOutlined style={{ color: "#faad14" }} />
    case NotificationType.ERROR:
      return <CloseCircleOutlined style={{ color: "#ff4d4f" }} />
  }
}

export const Top: React.FC = () => {
  const { t, i18n } = useTranslation("layout")
  const { removeTokens } = useAuth()
  const navigate = useNavigate()
  const dispatch = useAppDispatch()

  const { notifications, read } = useAppSelector(state => state.notification)

  const changeLanguage = async () => {
    const language = i18n.language
    const nextLanguage = language === "en" ? "pl" : "en"

    return i18n.changeLanguage(nextLanguage)
  }

  const signOut = () => {
    removeTokens()
    navigate(Routes.LOGIN)
  }

  const NotificationContent = () => (
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

  const unreadCount = notifications.filter(
    n => !read.includes(n.eventId),
  ).length

  const items: MenuProps["items"] = [
    {
      key: "logout",
      label: t("navbar.logout"),
      icon: <LogoutOutlined />,
      onClick: signOut,
    },
    {
      key: "notifications",
      icon: (
        <Popover
          content={<NotificationContent />}
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
      ),
    },
    {
      key: "translation",
      label: t("navbar.translation"),
      icon: <TranslationOutlined />,
      onClick: changeLanguage,
    },
  ]

  return (
    <Header style={{ display: "flex", background: "transparent" }}>
      <Menu
        style={menuStyle}
        selectable={false}
        mode="horizontal"
        items={items}
      />
    </Header>
  )
}
