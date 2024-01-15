import React, { useState } from "react";
import { useAppDispatch } from "../../../common";
import { Form, Tooltip } from "antd";
import { removeStage } from "../order.slice.ts";
import { CloseCircleOutlined } from "@ant-design/icons";
import { LocalizationAutoComplete } from "./LocalizationAutoComplete.tsx";

const formItemStyle: React.CSSProperties = { marginBottom: 3 };
const divStyle: React.CSSProperties = { display: "flex", width: "100%" };
const iconStyle: React.CSSProperties = {
  fontSize: "20px",
  position: "relative",
  top: 3,
  left: 15,
};

interface StageStepProps {
  index: number;
}

export const StageStep: React.FC<StageStepProps> = ({ index }) => {
  const dispatch = useAppDispatch();
  const [isHovered, setIsHovered] = useState(false);

  const isFirstStage = index === 0;
  const atLeastTwoStages = index > 1;

  const placeholder = isFirstStage
    ? "Wybierz punkt początkowy zlecenia"
    : "Wybierz punkt docelowy zlecenia";

  return (
    <Form.Item style={formItemStyle}>
      <div
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        style={divStyle}
      >
        <LocalizationAutoComplete index={index} placeholder={placeholder} />
        {isHovered && atLeastTwoStages && (
          <Tooltip title="Usuń ten etap">
            <CloseCircleOutlined
              style={iconStyle}
              onClick={() => dispatch(removeStage(index))}
            />
          </Tooltip>
        )}
      </div>
    </Form.Item>
  );
};
