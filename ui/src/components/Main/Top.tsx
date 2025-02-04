import React, { useState } from "react"
import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"

import {
  BellOutlined,
  LogoutOutlined,
  TranslationOutlined,
} from "@ant-design/icons"
import { Badge, Layout, List, Menu, MenuProps, Popover, Typography } from "antd"

import { useAuth } from "@/components"
import { Routes } from "@/router"

const { Header } = Layout

const menuStyle: React.CSSProperties = {
  background: "transparent",
  flex: 1,
  flexDirection: "row-reverse",
}

interface Notification {
  id: string
  title: string
  message: string
  timestamp: string
  read: boolean
}

const sampleNotifications: Notification[] = [
  {
    id: "1",
    title: "New Message",
    message: "You have received a new message from John Doe",
    timestamp: "2024-03-20T10:30:00",
    read: false,
  },
  {
    id: "2",
    title: "System Update",
    message: "System maintenance scheduled for tomorrow",
    timestamp: "2024-03-20T09:15:00",
    read: true,
  },
  {
    id: "3",
    title: "Task Completed",
    message: "Project X has been successfully completed",
    timestamp: "2024-03-19T16:45:00",
    read: true,
  },
]

export const Top: React.FC = () => {
  const { t, i18n } = useTranslation("layout")
  const { removeTokens } = useAuth()
  const navigate = useNavigate()
  const [notifications, setNotifications] =
    useState<Notification[]>(sampleNotifications)

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
    <List
      style={{ width: 300, maxHeight: 400, overflow: "auto" }}
      itemLayout="vertical"
      dataSource={notifications}
      renderItem={item => (
        <List.Item
          style={{
            backgroundColor: item.read
              ? "transparent"
              : "rgba(24, 144, 255, 0.05)",
            padding: "12px",
            cursor: "pointer",
          }}
          onClick={() => {
            setNotifications(
              notifications.map(n =>
                n.id === item.id ? { ...n, read: true } : n,
              ),
            )
          }}
        >
          <List.Item.Meta
            title={<Typography.Text strong>{item.title}</Typography.Text>}
            description={
              <div>
                <Typography.Text>{item.message}</Typography.Text>
                <Typography.Text
                  type="secondary"
                  style={{
                    display: "block",
                    fontSize: "12px",
                    marginTop: "4px",
                  }}
                >
                  {new Date(item.timestamp).toLocaleString()}
                </Typography.Text>
              </div>
            }
          />
        </List.Item>
      )}
    />
  )

  const unreadCount = notifications.filter(n => !n.read).length

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
