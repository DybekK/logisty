import React, { useState } from "react"

import { Button, Table, Modal } from "antd"

import { StatusTag } from "@/features/invitation"
import { InvitationStatus } from "@/features/invitation/invitation.types"
import { useTranslation } from "react-i18next"
import { CreateInvitation } from "@/features/invitation"

const titleBlockStyle = {
  display: "flex",
  justifyContent: "flex-end",
}

const dataSource = [
  {
    key: "1",
    firstName: "John",
    lastName: "Doe",
    email: "john@example.com",
    status: InvitationStatus.PENDING,
    createdAt: "2023-01-01",
    expiresAt: "2023-02-01",
    acceptedAt: null,
  },
]

const columns = [
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    render: (status: InvitationStatus) => <StatusTag status={status} />,
  },
  {
    title: "First Name",
    dataIndex: "firstName",
    key: "firstName",
  },
  {
    title: "Last Name",
    dataIndex: "lastName",
    key: "lastName",
  },
  {
    title: "Email",
    dataIndex: "email",
    key: "email",
  },
  {
    title: "Created At",
    dataIndex: "createdAt",
    key: "createdAt",
  },
  {
    title: "Expires At",
    dataIndex: "expiresAt",
    key: "expiresAt",
  },
  {
    title: "Accepted At",
    dataIndex: "acceptedAt",
    key: "acceptedAt",
  },
]

interface InvitationTableProps {
  invitationStatus: InvitationStatus
}

export const InvitationTable: React.FC<InvitationTableProps> = () => {
  const { t } = useTranslation("invitation")
  const [isModalOpen, setIsModalOpen] = useState(false)

  return (
    <>
      <Table
        dataSource={dataSource}
        columns={columns}
        title={() => (
          <div style={titleBlockStyle}>
            <Button type="primary" onClick={() => setIsModalOpen(true)}>
              {t("filterInvitations.createInvitation")}
            </Button>
          </div>
        )}
      />
      <Modal
        title={t("filterInvitations.createInvitation")}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={null}
        width={1000}
      >
        <CreateInvitation />
      </Modal>
    </>
  )
}
