import React from "react"
import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"

import {
  AppstoreAddOutlined,
  CarryOutOutlined,
  LaptopOutlined,
  Loading3QuartersOutlined,
  UserOutlined,
  UsergroupAddOutlined,
} from "@ant-design/icons"
import { Layout, Menu, MenuProps, theme } from "antd"

import { Routes } from "@/router"

const { Sider } = Layout

export const LeftSider: React.FC = () => {
  const {
    token: { colorBgLayout },
  } = theme.useToken()
  const { t } = useTranslation("layout", { keyPrefix: "leftbar" })
  const navigate = useNavigate()

  const items: MenuProps["items"] = [
    {
      label: t("orders.title"),
      key: "orders",
      icon: <LaptopOutlined />,
      children: [
        {
          label: t("orders.new"),
          key: "new",
          icon: <AppstoreAddOutlined />,
          onClick: () => navigate(Routes.NEW_ORDER),
        },
        {
          label: t("orders.pending"),
          key: "pending",
          icon: <Loading3QuartersOutlined />,
          onClick: () => navigate(Routes.ORDERS),
        },
      ],
    },
    {
      label: t("fleet.title"),
      key: "fleet",
      icon: <UserOutlined />,
      children: [
        {
          label: t("fleet.invitations"),
          key: "invitations",
          icon: <UsergroupAddOutlined />,
          onClick: () => navigate(Routes.INVITATIONS),
        },
        {
          label: t("fleet.users"),
          key: "users",
          icon: <UserOutlined />,
          onClick: () => navigate(Routes.USERS),
        },
      ],
    },
  ]

  return (
    <Sider width={200}>
      <Menu
        mode="inline"
        defaultSelectedKeys={["1"]}
        defaultOpenKeys={["sub1"]}
        style={{ height: "100%", background: colorBgLayout }}
        items={items}
      />
    </Sider>
  )
}
