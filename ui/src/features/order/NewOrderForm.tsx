import React, { useEffect } from "react";
import {
  Button,
  Card,
  Form,
  Input,
  Steps,
  AutoComplete,
  Divider,
  Flex,
} from "antd";
import { PlusCircleOutlined, CloseCircleOutlined } from "@ant-design/icons";
import { LeafletMap } from "components";
import debounce from "lodash/debounce";
import { useQueryClient } from "@tanstack/react-query";
import {
  addStage,
  fetchFeaturesByQuery,
  removeStage,
  updateLatestStage,
  updateLatestStageIndex,
  updateLocalizationAutoComplete,
} from "features/order";
import { Properties, useAppDispatch, useAppSelector } from "common";

interface AsyncAutoCompleteProps {
  index: number;
  placeholder: string;
}

const AsyncAutoComplete: React.FC<AsyncAutoCompleteProps> = ({
  index,
  placeholder,
}) => {
  const queryClient = useQueryClient();
  const dispatch = useAppDispatch();

  const stage = useAppSelector((state) => state.orders.stages[index]);
  const [value, setValue] = React.useState<string>("");

  useEffect(() => {
    setValue(stage.value);
  }, [stage]);

  const onSearch = async (value: string) => {
    const createFullName = ({ name, city }: Properties) =>
      `${city ? city + ", " : ""}${name ? name : ""}`;

    const features = await fetchFeaturesByQuery(queryClient, value);
    const localizations = features.map(({ properties, geometry }) => ({
      value: createFullName(properties),
      lat: geometry.coordinates[1],
      lon: geometry.coordinates[0],
    }));

    dispatch(updateLocalizationAutoComplete(localizations));
    dispatch(updateLatestStageIndex(index));
  };

  return (
    <AutoComplete
      style={{ width: "85%" }}
      value={value}
      onChange={setValue}
      onSearch={debounce(onSearch, 500)}
      onClick={() => dispatch(updateLatestStageIndex(index))}
    >
      <Input size="large" placeholder={placeholder} />
    </AutoComplete>
  );
};

const StageStep: React.FC<{
  index: number;
}> = ({ index }) => {
  const { stages } = useAppSelector((state) => state.orders);
  const dispatch = useAppDispatch();

  const showCloseButton = index !== 1 && index === stages.length - 1;

  return (
    <Form.Item style={{ marginBottom: 3 }}>
      <div style={{ display: "flex", width: "100%" }}>
        <AsyncAutoComplete
          index={index}
          placeholder="Wybierz miejsce początkowe zlecenia"
        />
        {showCloseButton && (
          <Button
            style={{ margin: "3px 0 0 10px" }}
            shape="circle"
            type="text"
            onClick={() => dispatch(removeStage(index))}
            icon={<CloseCircleOutlined style={{ fontSize: "20px" }} />}
          />
        )}
      </div>
    </Form.Item>
  );
};

export const NewOrderForm: React.FC = () => {
  const { stages, localizationsAutoComplete } = useAppSelector(
    (state) => state.orders,
  );

  const dispatch = useAppDispatch();

  const renderStages = () =>
    stages.map((_, index) => ({
      description: <StageStep index={index} />,
    }));

  return (
    <Card
      bodyStyle={{
        height: "100%",
        padding: 0,
        display: "flex",
        flexDirection: "row",
      }}
      style={{ height: "100%" }}
    >
      <Flex style={{ flexDirection: "column", width: 450, padding: 20 }}>
        <Form>
          <Steps
            progressDot
            direction="vertical"
            current={stages.length - 1}
            items={renderStages()}
          />
          <Button
            style={{ width: "100%", textAlign: "left" }}
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
            <Button
              onClick={() => dispatch(updateLatestStage(item))}
              key={index}
              size="large"
              type="text"
            >
              <span
                style={{
                  textAlign: "left",
                  width: "300px", // Set a specific width
                  overflow: "hidden", // Ensure the overflow is hidden
                  textOverflow: "ellipsis", // Use ellipsis for overflowed text
                  whiteSpace: "nowrap", // Prevent text from wrapping to next line
                }}
              >
                {item.value}
              </span>
            </Button>
          ))}
          <Divider />
        </Flex>
      </Flex>
      <LeafletMap />
    </Card>
  );
};
