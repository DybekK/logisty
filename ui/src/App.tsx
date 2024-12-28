import React from "react";
import { Outlet } from "react-router-dom";

import { Layout, theme } from "antd";

import "maplibre-gl/dist/maplibre-gl.css";

import { LeftSider, MainContent, Top } from "@/components";

const App: React.FC = () => {
  const {
    token: { colorBgLayout },
  } = theme.useToken();

  return (
    <Layout style={{ background: colorBgLayout, height: "100vh" }}>
      <Top />
      <Layout>
        <LeftSider />
        <MainContent>
          <Outlet />
        </MainContent>
      </Layout>
    </Layout>
  );
};

export default App;
