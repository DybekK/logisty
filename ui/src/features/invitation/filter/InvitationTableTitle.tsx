import { useTranslation } from "react-i18next"

import { Button, Input, Select } from "antd"

import { InvitationStatus } from "@/features/invitation/invitation.types"

const titleBlockStyle = {
  display: "flex",
  justifyContent: "space-between",
}

interface InvitationTableTitleProps {
  filters: {
    status: InvitationStatus | undefined
    email: string
  }
  debouncedSetFilters: (value: string) => void
  setFilters: React.Dispatch<
    React.SetStateAction<{
      status: InvitationStatus | undefined
      email: string
    }>
  >
  setIsModalOpen: React.Dispatch<React.SetStateAction<boolean>>
}

export const InvitationTableTitle: React.FC<InvitationTableTitleProps> = ({
  filters,
  debouncedSetFilters,
  setFilters,
  setIsModalOpen,
}) => {
  const { t } = useTranslation("invitation")
  return (
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
          onChange={value => setFilters(prev => ({ ...prev, status: value }))}
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
  )
}
