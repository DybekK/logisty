import React from "react";

import { Layout, theme } from "antd";

const { Content } = Layout;

interface MainContentProps {
  children: React.ReactNode;
}

export const MainContent: React.FC<MainContentProps> = ({ children }) => {
  const {
    token: { borderRadiusLG },
  } = theme.useToken();

  return (
    <Content
      style={{
        padding: "0 24px 24px",
        marginTop: "16px",
        minHeight: 280,
        borderRadius: borderRadiusLG,
      }}
    >
      {children}
    </Content>
  );
};
