import { useCallback, useState } from "react"
import { useTranslation } from "react-i18next"

import { Empty, Modal, Table } from "antd"

import debounce from "lodash/debounce"

import { parseToLocaleString, useAppSelector } from "@/common"
import { InvitationTableTitle, StatusTag } from "@/features/invitation"
import { CreateInvitation } from "@/features/invitation"
import { useFetchInvitations } from "@/features/invitation/invitation.api"
import {
  GetInvitationResponse,
  InvitationStatus,
} from "@/features/invitation/invitation.types"

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

  const { fleetId } = useAppSelector(state => state.auth.user!)

  const [isModalOpen, setIsModalOpen] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)

  const [filters, setFilters] = useState({
    status: undefined as InvitationStatus | undefined,
    email: "",
  })

  const pageSize = 10
  const { data, isLoading, refetch } = useFetchInvitations({
    fleetId,
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
          <InvitationTableTitle
            filters={filters}
            debouncedSetFilters={debouncedSetFilters}
            setFilters={setFilters}
            setIsModalOpen={setIsModalOpen}
          />
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
