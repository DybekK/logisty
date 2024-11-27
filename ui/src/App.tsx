import React from "react";

import { Layout, theme } from "antd";
import { LeftSider, MainContent, Top } from "components";

import "maplibre-gl/dist/maplibre-gl.css";
import { Outlet } from "react-router-dom";

const App: React.FC = () => {
  const {
    token: { colorBgLayout }
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
