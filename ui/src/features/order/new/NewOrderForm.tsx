import React, { useEffect } from "react";
import { Button, Card, Form, Steps, Divider, Flex } from "antd";
import { PlusCircleOutlined } from "@ant-design/icons";
import { Map3D } from "components";
import {
  addStage,
  fetchGeneratedPathByCoordinates,
  updateRoutesAndWaypoints,
} from "features/order";
import { useAppDispatch, useAppSelector } from "common";
import { StageStep } from "./StageStep.tsx";
import { LocalizationAutoCompleteElement } from "./LocalizationAutoCompleteElement.tsx";
import { useQueryClient } from "@tanstack/react-query";
import { MapProvider } from "react-map-gl";

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

const mapId = "orderMap";

export const NewOrderForm: React.FC = () => {
  const queryClient = useQueryClient();
  const dispatch = useAppDispatch();

  const { stages, routes, waypoints, localizationsAutoComplete } =
    useAppSelector(state => state.orders);

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
              style={buttonStyle}
              onClick={() => dispatch(addStage())}
              size="large"
              icon={<PlusCircleOutlined />}
            >
              Dodaj nowy etap
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
          </Flex>
        </Flex>
        <Map3D id={mapId} routes={routes} waypoints={waypoints} />
      </Card>
    </MapProvider>
  );
};
