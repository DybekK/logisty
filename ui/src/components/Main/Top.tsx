import React from "react"
import { useTranslation } from "react-i18next"

import { BellOutlined, TranslationOutlined } from "@ant-design/icons"
import { Layout, Menu, MenuProps } from "antd"

const { Header } = Layout

export const Top: React.FC = () => {
  const { t, i18n } = useTranslation("layout", { keyPrefix: "navbar" })

  const changeLanguage = async () => {
    const language = i18n.language
    const nextLanguage = language === "en" ? "pl" : "en"

    return i18n.changeLanguage(nextLanguage)
  }

  const items: MenuProps["items"] = [
    {
      label: t("notifications"),
      key: "notifications",
      icon: <BellOutlined />,
    },
    {
      key: "translation",
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
