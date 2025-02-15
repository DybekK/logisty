import React from "react"

import { Layout, theme } from "antd"

const { Content } = Layout

interface MainContentProps {
  isMobile?: boolean
  children: React.ReactNode
}

export const MainContent: React.FC<MainContentProps> = ({
  children,
  isMobile,
}) => {
  const {
    token: { borderRadiusLG },
  } = theme.useToken()

  return (
    <Content
      style={{
        padding: isMobile ? "5px" : "0 24px 24px",
        marginTop: isMobile ? 0 : "16px",
        minHeight: 280,
        borderRadius: borderRadiusLG,
      }}
    >
      {children}
    </Content>
  )
}
