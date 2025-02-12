import React from "react"
import { useTranslation } from "react-i18next"

import { CheckOutlined, PlusCircleOutlined } from "@ant-design/icons"
import { Button, DatePicker, Form, Steps } from "antd"

import dayjs from "dayjs"

import { useAppDispatch, useAppSelector } from "@/common"
import { addStep, setStartDate } from "@/features/order"
import { Step } from "@/features/order/new"

const datePickerContainerStyle: React.CSSProperties = {
  marginBottom: 11,
  marginLeft: 24,
}

const datePickerStyle: React.CSSProperties = {
  height: 40,
  width: "85%",
}

const buttonStyle: React.CSSProperties = {
  width: "100%",
  textAlign: "left",
  justifyContent: "flex-start",
  padding: "4px 11px",
}

const addStepButtonStyle: React.CSSProperties = { ...buttonStyle }
const acceptOrderButtonStyle: React.CSSProperties = {
  ...buttonStyle,
  marginTop: 10,
}

interface RouteFormProps {
  onAcceptOrder: () => void
  hasValidInput: () => boolean
}

export const RouteForm: React.FC<RouteFormProps> = ({
  onAcceptOrder,
  hasValidInput,
}) => {
  const { t } = useTranslation("order", { keyPrefix: "new" })
  const dispatch = useAppDispatch()
  const { steps, startDate } = useAppSelector(state => state.createNewOrder)

  const handleStartDateChange = (date: dayjs.Dayjs | null) =>
    dispatch(setStartDate(date ? date.toISOString() : ""))

  return (
    <Form>
      <Form.Item style={datePickerContainerStyle}>
        <DatePicker
          showTime
          style={datePickerStyle}
          placeholder={t("startDate")}
          size="middle"
          value={startDate ? dayjs(startDate) : null}
          onChange={handleStartDateChange}
        />
      </Form.Item>
      <Steps progressDot direction="vertical" current={steps.length - 1}>
        {steps.map((_, index) => (
          <Steps.Step key={index} description={<Step index={index} />} />
        ))}
      </Steps>
      <Button
        style={addStepButtonStyle}
        onClick={() => dispatch(addStep())}
        size="large"
        icon={<PlusCircleOutlined />}
      >
        {t("addStep")}
      </Button>
      <Button
        onClick={onAcceptOrder}
        style={acceptOrderButtonStyle}
        size="large"
        ghost
        type="primary"
        icon={<CheckOutlined />}
        disabled={!hasValidInput()}
      >
        {t("acceptOrder")}
      </Button>
    </Form>
  )
}
