import { useTranslation } from "react-i18next"

import { Input, Select } from "antd"

import { UserRole } from "@/common"

const titleBlockStyle = {
  display: "flex",
  justifyContent: "space-between",
}

interface UserTableTitleProps {
  filters: {
    role: UserRole | undefined
    email: string
  }
  debouncedSetFilters: (value: string) => void
  setFilters: React.Dispatch<
    React.SetStateAction<{
      role: UserRole | undefined
      email: string
    }>
  >
}

export const UserTableTitle: React.FC<UserTableTitleProps> = ({
  filters,
  debouncedSetFilters,
  setFilters,
}) => {
  const { t } = useTranslation("user")
  return (
    <div style={{ ...titleBlockStyle, gap: "8px" }}>
      <div style={{ display: "flex", gap: "8px" }}>
        <Input.Search
          placeholder={t("filterUsers.searchPlaceholder")}
          style={{ width: 300 }}
          defaultValue={filters.email}
          onChange={e => debouncedSetFilters(e.target.value)}
        />
        <Select
          placeholder={t("filterUsers.rolePlaceholder")}
          style={{ width: 170 }}
          allowClear
          value={filters.role}
          onChange={value => setFilters(prev => ({ ...prev, role: value }))}
        >
          <Select.Option value={UserRole.DISPATCHER}>
            {t("filterUsers.roles.DISPATCHER")}
          </Select.Option>
          <Select.Option value={UserRole.DRIVER}>
            {t("filterUsers.roles.DRIVER")}
          </Select.Option>
        </Select>
      </div>
    </div>
  )
}
