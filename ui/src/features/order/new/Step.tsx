import React, { useState } from "react"
import { useTranslation } from "react-i18next"

import { CloseCircleOutlined } from "@ant-design/icons"
import { Form, Tooltip } from "antd"

import { useAppDispatch } from "@/common"
import { removeStep } from "@/features/order"
import { LocalizationAutoComplete } from "@/features/order/new"

const formItemStyle: React.CSSProperties = { marginBottom: 3 }
const divStyle: React.CSSProperties = { display: "flex", width: "100%" }
const iconStyle: React.CSSProperties = {
  fontSize: "20px",
  position: "relative",
  top: 3,
  left: 15,
}

interface StepProps {
  index: number
}

export const Step: React.FC<StepProps> = ({ index }) => {
  const dispatch = useAppDispatch()
  const { t } = useTranslation("order", { keyPrefix: "new" })
  const [isHovered, setIsHovered] = useState(false)

  const isFirstStep = index === 0
  const atLeastTwoSteps = index > 1

  const placeholder = isFirstStep ? t("startPoint") : t("destinationPoint")

  return (
    <Form.Item style={formItemStyle}>
      <div
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        style={divStyle}
      >
        <LocalizationAutoComplete index={index} placeholder={placeholder} />
        {isHovered && atLeastTwoSteps && (
          <Tooltip title={t("deleteStep")}>
            <CloseCircleOutlined
              style={iconStyle}
              onClick={() => dispatch(removeStep(index))}
            />
          </Tooltip>
        )}
      </div>
    </Form.Item>
  )
}
