import React, { useEffect } from "react";
import { Button, Card, Form, Steps, Divider, Flex } from "antd";
import { PlusCircleOutlined, CheckOutlined } from "@ant-design/icons";
import { Map3D } from "components";
import {
  addStage,
  fetchGeneratedPathByCoordinates,
  CreateNewOrderStep,
  updateRoutesAndWaypoints,
} from "features/order";
import { useAppDispatch, useAppSelector } from "common";
import { StageStep } from "./StageStep.tsx";
import { LocalizationAutoCompleteElement } from "./LocalizationAutoCompleteElement.tsx";
import { useQueryClient } from "@tanstack/react-query";
import { MapProvider } from "react-map-gl";
import { useTranslation } from "react-i18next";

const cardBodyStyle: React.CSSProperties = {
  height: "100%",
  padding: 0,
  display: "flex",
  flexDirection: "row",
};

const cardStyle: React.CSSProperties = { height: "100%" };

const flexStyle: React.CSSProperties = {
  flexDirection: "column",
  zIndex: 1,
  width: 450,
  padding: 20,
  boxShadow: "8px 0px 15px -5px rgba(0, 0, 0, 0.025)",
};

const buttonStyle: React.CSSProperties = { width: "100%", textAlign: "left" };
const addStageButtonStyle: React.CSSProperties = { ...buttonStyle };
const acceptOrderButtonStyle: React.CSSProperties = {
  ...buttonStyle,
  marginTop: 10,
};

const mapId = "orderMap";

interface Driver {
  id: string;
  name: string;
}

const drivers: Driver[] = [
  {
    id: "1",
    name: "Jan Kowalski",
  },
  {
    id: "2",
    name: "Adam Nowak",
  },
  {
    id: "3",
    name: "Janusz Tracz",
  },
];

const isStagesValid = (stages: CreateNewOrderStep[]): boolean =>
  stages.some(
    ({ lat, lon, inputValue }) => !lat || !lon || inputValue.trim() === "",
  );

export const NewOrderForm: React.FC = () => {
  const queryClient = useQueryClient();
  const dispatch = useAppDispatch();

  const { t } = useTranslation("order", { keyPrefix: "new" });
  const { stages, routes, waypoints, localizationsAutoComplete } =
    useAppSelector(state => state.createNewOrder);

  useEffect(() => {
    if (stages.filter(stage => !!stage.lat).length < 2) return;

    fetchGeneratedPathByCoordinates(queryClient, stages).then(
      ({ routes, waypoints }) =>
        dispatch(updateRoutesAndWaypoints({ routes, waypoints })),
    );
  }, [stages]);

  return (
    <MapProvider>
      <Card bodyStyle={cardBodyStyle} style={cardStyle}>
        <Flex style={flexStyle}>
          <Form>
            <Steps progressDot direction="vertical" current={stages.length - 1}>
              {stages.map((_, index) => (
                <Steps.Step
                  key={index}
                  description={<StageStep index={index} />}
                />
              ))}
            </Steps>
            <Button
              style={addStageButtonStyle}
              onClick={() => dispatch(addStage())}
              size="large"
              icon={<PlusCircleOutlined />}
            >
              {t("addStage")}
            </Button>
            <Button
              style={acceptOrderButtonStyle}
              size="large"
              type="text"
              icon={<CheckOutlined />}
              disabled={isStagesValid(stages)}
            >
              {t("acceptOrder")}
            </Button>
          </Form>
          <Divider />
          <Flex style={{ flexDirection: "column" }}>
            {localizationsAutoComplete.map((item, index) => (
              <LocalizationAutoCompleteElement
                key={index}
                localization={item}
              />
            ))}
            {localizationsAutoComplete.length > 0 && <Divider />}
            {drivers.map(driver => (
              <Button
                key={driver.id}
                style={buttonStyle}
                size="large"
                type="text"
              >
                {driver.name}
              </Button>
            ))}
          </Flex>
        </Flex>
        <Map3D id={mapId} routes={routes} waypoints={waypoints} />
      </Card>
    </MapProvider>
  );
};
