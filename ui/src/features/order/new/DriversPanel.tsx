import React, { useCallback, useEffect } from "react"
import { useTranslation } from "react-i18next"

import { Avatar, Button, Divider, Empty, Flex, Input } from "antd"

import { debounce } from "lodash"

import { useAppDispatch, useAppSelector } from "@/common"
import {
  selectDriver,
  unselectDriver,
  updateSearchByEmail,
  useFetchAvailableDrivers,
} from "@/features/order"
import { LocalizationAutoCompleteElement } from "@/features/order/new"

const buttonStyle: React.CSSProperties = {
  width: "100%",
  textAlign: "left",
  justifyContent: "flex-start",
  padding: "4px 11px",
}

const buttonTitleStyle: React.CSSProperties = {
  textAlign: "left",
  width: "250px",
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap",
}

const driverAvatarStyle: React.CSSProperties = {
  backgroundColor: "#f56a00",
  marginRight: 8,
}

const driverStatusStyle: React.CSSProperties = {
  width: 8,
  height: 8,
  borderRadius: "50%",
  backgroundColor: "#52c41a",
  marginLeft: "auto",
  marginRight: 8,
}

const searchInputStyle: React.CSSProperties = {
  marginBottom: 16,
}

const flexColumnStyle: React.CSSProperties = {
  flexDirection: "column",
}

const SearchByEmailInput = () => {
  const { t } = useTranslation("order", { keyPrefix: "new" })

  const dispatch = useAppDispatch()
  const { searchByEmail } = useAppSelector(state => state.createNewOrder)
  const debouncedSearchByEmail = useCallback(
    debounce((value: string) => {
      dispatch(updateSearchByEmail(value))
    }, 500),
    [],
  )

  return (
    <Input.Search
      placeholder={t("searchByEmail")}
      onChange={e => debouncedSearchByEmail(e.target.value)}
      defaultValue={searchByEmail}
      style={searchInputStyle}
    />
  )
}

export const DriversPanel = () => {
  const { t } = useTranslation("order", { keyPrefix: "new" })

  const dispatch = useAppDispatch()
  const {
    localizationsAutoComplete,
    startDate,
    estimatedEndedAt,
    searchByEmail,
    selectedDriverId,
  } = useAppSelector(state => state.createNewOrder)

  const { fleetId } = useAppSelector(state => state.auth.user!)

  const { data: availableDrivers } = useFetchAvailableDrivers(
    {
      fleetId,
      startAt: startDate!,
      endAt: estimatedEndedAt!,
      email: searchByEmail,
    },
    !!startDate && !!estimatedEndedAt,
  )

  useEffect(() => {
    if (availableDrivers?.drivers.length === 0) {
      dispatch(unselectDriver())
    }
  }, [availableDrivers, selectedDriverId, dispatch])

  const isDriverSelected = (driverId: string) => selectedDriverId === driverId

  const handleSelectDriver = (driverId: string) =>
    dispatch(selectDriver(driverId))

  if (!startDate && !searchByEmail) {
    return (
      <Flex style={flexColumnStyle}>
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={t("startDateNotProvided")}
        />
      </Flex>
    )
  }

  if (startDate && !searchByEmail && !availableDrivers?.drivers.length) {
    return (
      <Flex style={flexColumnStyle}>
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={t("noDrivers")}
        />
      </Flex>
    )
  }

  if (startDate && searchByEmail && !availableDrivers?.drivers.length) {
    return (
      <>
        <SearchByEmailInput />
        <Flex style={flexColumnStyle}>
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={t("noDrivers")}
          />
        </Flex>
      </>
    )
  }

  return (
    <Flex style={flexColumnStyle}>
      {localizationsAutoComplete.map((item, index) => (
        <LocalizationAutoCompleteElement key={index} localization={item} />
      ))}
      {localizationsAutoComplete.length > 0 && <Divider />}
      <SearchByEmailInput />
      {availableDrivers?.drivers.map(driver => (
        <Button
          key={driver.driverId}
          style={buttonStyle}
          size="large"
          type={isDriverSelected(driver.driverId) ? "link" : "text"}
          onClick={() => handleSelectDriver(driver.driverId)}
        >
          <Avatar style={driverAvatarStyle} size="small">
            {driver.firstName.charAt(0)}
          </Avatar>
          <span style={buttonTitleStyle}>
            {`${driver.firstName} ${driver.lastName}`}
          </span>
          <span style={driverStatusStyle} title="Available" />
        </Button>
      ))}
    </Flex>
  )
}
