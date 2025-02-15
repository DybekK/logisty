import React from "react"
import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"

import {
  AppstoreAddOutlined,
  LaptopOutlined,
  Loading3QuartersOutlined,
  LogoutOutlined,
  TranslationOutlined,
  UserOutlined,
  UsergroupAddOutlined,
} from "@ant-design/icons"
import { Drawer, Layout, Menu, MenuProps, theme } from "antd"

import { UserRole, useAppDispatch, useAppSelector } from "@/common"
import { useAuth } from "@/components"
import { removeUser } from "@/features/auth"
import { clearNotifications } from "@/features/notification"
import { Routes } from "@/router"

const { Sider } = Layout

interface LeftSiderProps {
  isMobile?: boolean
  drawerVisible?: boolean
  onDrawerClose?: () => void
}

export const LeftSider: React.FC<LeftSiderProps> = ({
  isMobile,
  drawerVisible,
  onDrawerClose,
}) => {
  const {
    token: { colorBgLayout },
  } = theme.useToken()
  const { t, i18n } = useTranslation("layout")
  const navigate = useNavigate()
  const { removeTokens } = useAuth()
  const dispatch = useAppDispatch()
  const roles = useAppSelector(state => state.auth.user?.roles) ?? []

  const getRoleBasedMenuItems = (
    enabledRoles: UserRole[],
    items: MenuProps["items"],
  ) => {
    const isVisible = roles.some(role => enabledRoles.includes(role))

    return items?.filter(() => isVisible) ?? []
  }

  const handleNavigation = (route: string) => {
    navigate(route)
    onDrawerClose?.()
  }

  const changeLanguage = async () => {
    const language = i18n.language
    const nextLanguage = language === "en" ? "pl" : "en"
    await i18n.changeLanguage(nextLanguage)
    onDrawerClose?.()
  }

  const signOut = () => {
    removeTokens()
    dispatch(removeUser())
    dispatch(clearNotifications())
    navigate(Routes.LOGIN)
    onDrawerClose?.()
  }

  const mainMenuItems: MenuProps["items"] = [
    {
      label: t("leftbar.orders.title"),
      key: "orders",
      icon: <LaptopOutlined />,
      ...(isMobile && { type: "group" }),
      children: [
        ...getRoleBasedMenuItems(
          [UserRole.DISPATCHER],
          [
            {
              label: t("leftbar.orders.new"),
              key: "new",
              icon: <AppstoreAddOutlined />,
              onClick: () => handleNavigation(Routes.NEW_ORDER),
            },
          ],
        ),
        ...getRoleBasedMenuItems(
          [UserRole.DISPATCHER],
          [
            {
              label: t("leftbar.orders.pending"),
              key: "pending",
              icon: <Loading3QuartersOutlined />,
              onClick: () => handleNavigation(Routes.ORDERS),
            },
          ],
        ),
        ...getRoleBasedMenuItems(
          [UserRole.DRIVER],
          [
            {
              label: t("leftbar.orders.driver"),
              key: "driver",
              icon: <UserOutlined />,
              onClick: () => handleNavigation(Routes.DRIVER_ORDERS),
            },
          ],
        ),
      ],
    },
    ...getRoleBasedMenuItems(
      [UserRole.DISPATCHER],
      [
        {
          label: t("leftbar.fleet.title"),
          key: "fleet",
          icon: <UserOutlined />,
          ...(isMobile && { type: "group" }),
          children: [
            ...getRoleBasedMenuItems(
              [UserRole.DISPATCHER],
              [
                {
                  label: t("leftbar.fleet.invitations"),
                  key: "invitations",
                  icon: <UsergroupAddOutlined />,
                  onClick: () => handleNavigation(Routes.INVITATIONS),
                },
              ],
            ),
            ...getRoleBasedMenuItems(
              [UserRole.DISPATCHER],
              [
                {
                  label: t("leftbar.fleet.users"),
                  key: "users",
                  icon: <UserOutlined />,
                  onClick: () => handleNavigation(Routes.USERS),
                },
              ],
            ),
          ],
        },
      ],
    ),
  ]

  const mobileBottomItems: MenuProps["items"] = [
    {
      label: t("navbar.translation"),
      key: "translation",
      icon: <TranslationOutlined />,
      onClick: changeLanguage,
    },
    {
      label: t("navbar.logout"),
      key: "logout",
      icon: <LogoutOutlined />,
      onClick: signOut,
    },
  ]

  const items = isMobile ? mainMenuItems : [...mainMenuItems]

  const menuContent = isMobile ? (
    <div style={{ display: "flex", flexDirection: "column", height: "100%" }}>
      <Menu
        mode="inline"
        style={{
          flex: 1,
          border: "none",
        }}
        items={items}
        defaultOpenKeys={["orders", "fleet"]}
      />
      <Menu
        mode="inline"
        style={{
          border: "none",
        }}
        items={mobileBottomItems}
      />
    </div>
  ) : (
    <Menu
      mode="inline"
      style={{
        height: "100%",
        background: colorBgLayout,
      }}
      items={items}
      defaultOpenKeys={["sub1"]}
      defaultSelectedKeys={["1"]}
    />
  )

  if (isMobile) {
    return (
      <Drawer
        placement="left"
        onClose={onDrawerClose}
        open={drawerVisible}
      >
        {menuContent}
      </Drawer>
    )
  }

  return <Sider width={200}>{menuContent}</Sider>
}
