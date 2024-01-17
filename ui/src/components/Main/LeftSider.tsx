import React from "react";

import { Menu, Layout, theme, MenuProps } from "antd";
import {
  LaptopOutlined,
  AppstoreAddOutlined,
  CarryOutOutlined,
  Loading3QuartersOutlined,
} from "@ant-design/icons";
import { useTranslation } from "react-i18next";

const { Sider } = Layout;

export const LeftSider: React.FC = () => {
  const {
    token: { colorBgLayout },
  } = theme.useToken();
  const { t } = useTranslation("layout", { keyPrefix: "leftbar" });

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
        },
        {
          label: t("orders.pending"),
          key: "pending",
          icon: <Loading3QuartersOutlined />,
        },
        {
          label: t("orders.completed"),
          key: "completed",
          icon: <CarryOutOutlined />,
        },
      ],
    },
  ];

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
  );
};
