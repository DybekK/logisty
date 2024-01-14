import React from "react";

import { Layout, theme } from "antd";
import { LeftSider, MainContent, Top } from "components";
import { NewOrderForm } from "features/order";

import "maplibre-gl/dist/maplibre-gl.css";

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
          <NewOrderForm />
        </MainContent>
      </Layout>
    </Layout>
  );
};

export default App;
