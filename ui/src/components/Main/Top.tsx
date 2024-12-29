import React from "react"
import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"

import {
  BellOutlined,
  LogoutOutlined,
  TranslationOutlined,
} from "@ant-design/icons"
import { Layout, Menu, MenuProps } from "antd"

import { useAuth } from "@/components"
import { Routes } from "@/router"

const { Header } = Layout

export const Top: React.FC = () => {
  const { t, i18n } = useTranslation("layout")
  const { removeTokens } = useAuth()
  const navigate = useNavigate()

  const changeLanguage = async () => {
    const language = i18n.language
    const nextLanguage = language === "en" ? "pl" : "en"

    return i18n.changeLanguage(nextLanguage)
  }

  const signOut = () => {
    removeTokens()
    navigate(Routes.LOGIN)
  }

  const items: MenuProps["items"] = [
    {
      key: "logout",
      label: t("navbar.logout"),
      icon: <LogoutOutlined />,
      onClick: signOut,
    },
    {
      key: "notifications",
      label: t("navbar.notifications"),
      icon: <BellOutlined />,
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
        style={{
          background: "transparent",
          flex: 1,
          flexDirection: "row-reverse",
        }}
        mode="horizontal"
        items={items}
      />
    </Header>
  )
}
