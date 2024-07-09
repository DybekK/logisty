import React from "react";
import { Table } from "antd";
import { OrderStatus } from "../slice/orders.slice.ts"
import { StatusTag } from "./StatusTag.tsx";

const dataSource = [
  {
    key: "1",
    status: "pending",
    orderStart: "123 Main Street",
    orderEnd: "456 Elm Street",
    driver: "John Doe",
    createdBy: "Admin",
    createdAt: "2022-01-01",
    dueAt: "2022-01-02"
  },
  {
    key: "2",
    status: "completed",
    orderStart: "789 Oak Street",
    orderEnd: "321 Pine Street",
    driver: "Jane Doe",
    createdBy: "User",
    createdAt: "2022-01-03",
    dueAt: "2022-01-04"
  }
];

const columns = [
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    render: (status: OrderStatus) => <StatusTag status={status} />
  },
  {
    title: "Order Start",
    dataIndex: "orderStart",
    key: "orderStart"
  },
  {
    title: "Order End",
    dataIndex: "orderEnd",
    key: "orderEnd"
  },
  {
    title: "Driver",
    dataIndex: "driver",
    key: "driver"
  },
  {
    title: "Created By",
    dataIndex: "createdBy",
    key: "createdBy"
  },
  {
    title: "Created At",
    dataIndex: "createdAt",
    key: "createdAt"
  },
  {
    title: "Due At",
    dataIndex: "dueAt",
    key: "dueAt"
  }
];

interface OrderTableProps {
  orderStatus: OrderStatus; //TODO: move to domain level
}

export const OrderTable: React.FC<OrderTableProps> = ({orderStatus}) => {
  return <Table dataSource={dataSource} columns={columns} />;
};