import { useCallback, useState } from "react"
import { useTranslation } from "react-i18next"

import { Empty, Table, Tag } from "antd"

import debounce from "lodash/debounce"

import { UserRole, parseToLocaleString, useAppSelector } from "@/common"
import { UserTableTitle } from "@/features/user/filter"
import { useFetchUsers } from "@/features/user/user.api"
import { GetUserResponse } from "@/features/user/user.types"

const getColumns = (t: (key: string) => string) => [
  {
    title: t("filterUsers.role"),
    dataIndex: "roles",
    key: "roles",
    render: (roles: UserRole[]) => (
      <>
        {roles.map(role => (
          <Tag key={role}>{t(`filterUsers.roles.${role}`)}</Tag>
        ))}
      </>
    ),
  },
  {
    title: t("filterUsers.firstName"),
    dataIndex: "firstName",
    key: "firstName",
  },
  {
    title: t("filterUsers.lastName"),
    dataIndex: "lastName",
    key: "lastName",
  },
  {
    title: t("filterUsers.email"),
    dataIndex: "email",
    key: "email",
  },
  {
    title: t("filterUsers.createdAt"),
    dataIndex: "createdAt",
    key: "createdAt",
    render: parseToLocaleString,
  },
]

const userData = (users?: GetUserResponse[]) =>
  users?.map(user => ({
    ...user,
    key: user.userId,
  }))

export const UserTable = () => {
  const { t } = useTranslation("user")

  const { fleetId } = useAppSelector(state => state.auth.user!)
  const [currentPage, setCurrentPage] = useState(1)

  const [filters, setFilters] = useState({
    role: undefined as UserRole | undefined,
    email: "",
  })

  const pageSize = 10
  const { data, isLoading } = useFetchUsers({
    fleetId,
    limit: pageSize,
    page: currentPage - 1,
    role: filters.role,
    email: filters.email || undefined,
  })

  const debouncedSetFilters = useCallback(
    debounce((value: string) => {
      setFilters(prev => ({ ...prev, email: value }))
    }, 500),
    [],
  )

  return (
    <>
      <Table
        dataSource={userData(data?.users)}
        columns={getColumns(t)}
        locale={{
          emptyText: (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={t("filterUsers.empty")}
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
          <UserTableTitle
            filters={filters}
            debouncedSetFilters={debouncedSetFilters}
            setFilters={setFilters}
          />
        )}
      />
    </>
  )
}
