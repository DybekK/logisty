import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"

import {
  LogoutOutlined,
  MenuOutlined,
  TranslationOutlined,
} from "@ant-design/icons"
import { Button, Layout, Menu, MenuProps } from "antd"

import { useAppDispatch } from "@/common"
import { useAuth } from "@/components"
import { removeUser } from "@/features/auth"
import {
  NotificationPopover,
  clearNotifications,
} from "@/features/notification"
import { Routes } from "@/router"

const { Header } = Layout

const menuStyle: React.CSSProperties = {
  background: "transparent",
  flex: 1,
  flexDirection: "row-reverse",
  border: "none",
}

const headerStyle: React.CSSProperties = {
  display: "flex",
  background: "transparent",
  alignItems: "center",
  borderBottom: "1px solid #f0f0f0",
}

const mobileHeaderStyle: React.CSSProperties = {
  ...headerStyle,
  padding: "0 16px",
}

const mobileMenuButtonStyle: React.CSSProperties = {
  marginRight: 16,
  padding: "4px 0",
}

interface TopProps {
  isMobile: boolean
  onMenuClick?: () => void
}

export const Top: React.FC<TopProps> = ({ isMobile, onMenuClick }) => {
  const { t, i18n } = useTranslation("layout")
  const { removeTokens } = useAuth()
  const navigate = useNavigate()
  const dispatch = useAppDispatch()

  const changeLanguage = async () => {
    const language = i18n.language
    const nextLanguage = language === "en" ? "pl" : "en"
    return i18n.changeLanguage(nextLanguage)
  }

  const signOut = () => {
    removeTokens()
    dispatch(removeUser())
    dispatch(clearNotifications())
    navigate(Routes.LOGIN)
  }

  const items: MenuProps["items"] = [
    !isMobile && {
      key: "logout",
      label: t("navbar.logout"),
      icon: <LogoutOutlined />,
      onClick: signOut,
    },
    {
      key: "notifications",
      icon: <NotificationPopover />,
    },
    !isMobile && {
      key: "translation",
      label: t("navbar.translation"),
      icon: <TranslationOutlined />,
      onClick: changeLanguage,
    },
  ].filter(Boolean) as MenuProps["items"]

  return (
    <Header style={isMobile ? mobileHeaderStyle : headerStyle}>
      {isMobile && (
        <Button
          type="text"
          icon={<MenuOutlined />}
          onClick={onMenuClick}
          style={mobileMenuButtonStyle}
        />
      )}
      <Menu
        style={menuStyle}
        selectable={false}
        mode="horizontal"
        items={items}
      />
    </Header>
  )
}
