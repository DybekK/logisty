import React from "react";
import { useAppDispatch, useAppSelector } from "../../../common";
import { Button, Form } from "antd";
import { removeStage } from "../order.slice.ts";
import { CloseCircleOutlined } from "@ant-design/icons";
import { LocalizationAutoComplete } from "./LocalizationAutoComplete.tsx";

const formItemStyle: React.CSSProperties = { marginBottom: 3 };
const divStyle: React.CSSProperties = { display: "flex", width: "100%" };
const buttonStyle: React.CSSProperties = { margin: "3px 0 0 10px" };
const iconStyle: React.CSSProperties = { fontSize: "20px" };

interface StageStepProps {
  index: number;
}

export const StageStep: React.FC<StageStepProps> = ({ index }) => {
  const { stages } = useAppSelector((state) => state.orders);
  const dispatch = useAppDispatch();

  const showCloseButton = index !== 1 && index === stages.length - 1;

  return (
    <Form.Item style={formItemStyle}>
      <div style={divStyle}>
        <LocalizationAutoComplete
          index={index}
          placeholder="Wybierz miejsce początkowe zlecenia"
        />
        {showCloseButton && (
          <Button
            style={buttonStyle}
            shape="circle"
            type="text"
            onClick={() => dispatch(removeStage(index))}
            icon={<CloseCircleOutlined style={iconStyle} />}
          />
        )}
      </div>
    </Form.Item>
  );
};
