import { useCallback, useState } from "react"
import { useTranslation } from "react-i18next"

import { Button, Empty, Input, Modal, Select, Table } from "antd"

import debounce from "lodash/debounce"

import { parseToLocaleString, useAppSelector } from "@/common"
import { StatusTag } from "@/features/invitation"
import { CreateInvitation } from "@/features/invitation"
import { useFetchInvitations } from "@/features/invitation/invitation.api"
import {
  GetInvitationResponse,
  InvitationStatus,
} from "@/features/invitation/invitation.types"

const titleBlockStyle = {
  display: "flex",
  justifyContent: "space-between",
}

const getColumns = (t: (key: string) => string) => [
  {
    title: t("filterInvitations.status"),
    dataIndex: "status",
    key: "status",
    render: (status: InvitationStatus) => <StatusTag status={status} />,
  },
  {
    title: t("filterInvitations.firstName"),
    dataIndex: "firstName",
    key: "firstName",
  },
  {
    title: t("filterInvitations.lastName"),
    dataIndex: "lastName",
    key: "lastName",
  },
  {
    title: t("filterInvitations.email"),
    dataIndex: "email",
    key: "email",
  },
  {
    title: t("filterInvitations.createdAt"),
    dataIndex: "createdAt",
    key: "createdAt",
    render: parseToLocaleString,
  },
  {
    title: t("filterInvitations.expiresAt"),
    dataIndex: "expiresAt",
    key: "expiresAt",
    render: parseToLocaleString,
  },
  {
    title: t("filterInvitations.acceptedAt"),
    dataIndex: "acceptedAt",
    key: "acceptedAt",
    render: parseToLocaleString,
  },
]

const invitationData = (invitations?: GetInvitationResponse[]) =>
  invitations?.map(invitation => ({
    ...invitation,
    key: invitation.invitationId,
  }))

export const InvitationTable = () => {
  const { t } = useTranslation("invitation")

  const user = useAppSelector(state => state.auth.user)

  const [isModalOpen, setIsModalOpen] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)

  const [filters, setFilters] = useState({
    status: undefined as InvitationStatus | undefined,
    email: "",
  })

  const pageSize = 10
  const { data, isLoading, refetch } = useFetchInvitations({
    fleetId: user?.fleetId!,
    limit: pageSize,
    page: currentPage - 1,
    status: filters.status,
    email: filters.email || undefined,
  })

  const debouncedSetFilters = useCallback(
    debounce((value: string) => {
      setFilters(prev => ({ ...prev, email: value }))
    }, 500),
    [],
  )

  const handleOnSuccess = () => {
    setIsModalOpen(false)
    refetch()
  }

  return (
    <>
      <Table
        dataSource={invitationData(data?.invitations)}
        columns={getColumns(t)}
        locale={{
          emptyText: (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={t("filterInvitations.empty")}
            />
          ),
        }}
        loading={isLoading}
        pagination={{
          current: currentPage,
          pageSize: pageSize,
          onChange: page => setCurrentPage(page),
          total: data?.total,
        }}
        title={() => (
          <div style={{ ...titleBlockStyle, gap: "8px" }}>
            <div style={{ display: "flex", gap: "8px" }}>
              <Input.Search
                placeholder={t("filterInvitations.searchPlaceholder")}
                style={{ width: 300 }}
                defaultValue={filters.email}
                onChange={e => debouncedSetFilters(e.target.value)}
              />
              <Select
                placeholder={t("filterInvitations.statusPlaceholder")}
                style={{ width: 170 }}
                allowClear
                value={filters.status}
                onChange={value =>
                  setFilters(prev => ({ ...prev, status: value }))
                }
              >
                <Select.Option value={InvitationStatus.PENDING}>
                  {t("filterInvitations.statuses.PENDING")}
                </Select.Option>
                <Select.Option value={InvitationStatus.ACCEPTED}>
                  {t("filterInvitations.statuses.ACCEPTED")}
                </Select.Option>
                <Select.Option value={InvitationStatus.EXPIRED}>
                  {t("filterInvitations.statuses.EXPIRED")}
                </Select.Option>
              </Select>
            </div>
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
        <CreateInvitation onSuccess={handleOnSuccess} />
      </Modal>
    </>
  )
}
