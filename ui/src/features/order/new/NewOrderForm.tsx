import React, { useEffect } from "react";
import { Button, Card, Form, Steps, Divider, Flex } from "antd";
import { PlusCircleOutlined } from "@ant-design/icons";
import { Map3D } from "components";
import {
  addStage,
  fetchGeneratedPathByCoordinates,
  updateRoutes,
} from "features/order";
import { Coordinates, useAppDispatch, useAppSelector } from "common";
import { StageStep } from "./StageStep.tsx";
import { LocalizationAutoCompleteElement } from "./LocalizationAutoCompleteElement.tsx";
import { useQueryClient } from "@tanstack/react-query";

const cardBodyStyle: React.CSSProperties = {
  height: "100%",
  padding: 0,
  display: "flex",
  flexDirection: "row",
};

const cardStyle: React.CSSProperties = { height: "100%" };

const flexStyle: React.CSSProperties = {
  flexDirection: "column",
  width: 450,
  padding: 20,
  boxShadow: "8px 0px 15px -5px rgba(0, 0, 0, 0.10)",
};

const buttonStyle: React.CSSProperties = { width: "100%", textAlign: "left" };

export const NewOrderForm: React.FC = () => {
  const queryClient = useQueryClient();

  const { stages, routes, localizationsAutoComplete, latestStageIndex } =
    useAppSelector((state) => state.orders);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (stages.filter((stage) => !!stage.lat).length < 2) return;

    fetchGeneratedPathByCoordinates(queryClient, stages).then((routes) =>
      dispatch(updateRoutes(routes)),
    );
  }, [stages]);

  const renderStages = () =>
    stages.map((_, index) => ({
      description: <StageStep index={index} />,
    }));

  const extractCoordinates = (): Coordinates => ({
    lat: stages[latestStageIndex]?.lat || 0,
    lon: stages[latestStageIndex]?.lon || 0,
  });

  return (
    <Card bodyStyle={cardBodyStyle} style={cardStyle}>
      <Flex style={flexStyle}>
        <Form>
          <Steps
            progressDot
            direction="vertical"
            current={stages.length - 1}
            items={renderStages()}
          />
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
              index={index}
              localization={item}
            />
          ))}
          {localizationsAutoComplete.length > 0 && <Divider />}
        </Flex>
      </Flex>
      <Map3D focusOnCoordinates={extractCoordinates()} routes={routes} />
    </Card>
  );
};
