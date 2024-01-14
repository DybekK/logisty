import React, { useEffect, useState } from "react";
import Map from "react-map-gl/maplibre";
import { Source, Layer } from "react-map-gl";
import { Feature, Position } from "geojson";
import { Flex, Skeleton } from "antd";

const mapStyle = "http://localhost:8080/styles/basic-preview/style.json";

const flexStyle: React.CSSProperties = {
  height: "100%",
  width: "100%",
  justifyContent: "center",
  alignItems: "center",
};

export const Map3D: React.FC = () => {
  const [coordinates, setCoordinates] = useState<Position[]>([[0, 0]]);
  const [loadingCoordinates, setLoadingCoordinates] = useState(true);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition((position) => {
      setCoordinates([[position.coords.longitude, position.coords.latitude]]);
      setLoadingCoordinates(false);
    });
  }, []);

  const lineData: Feature = {
    type: "Feature",
    properties: {},
    geometry: {
      type: "LineString",
      coordinates: coordinates,
    },
  };

  if (loadingCoordinates) {
    return (
      <Flex style={flexStyle}>
        <Skeleton.Image active={true} />
      </Flex>
    );
  }
  return (
    <Map
      initialViewState={{
        longitude: coordinates[0][0],
        latitude: coordinates[0][1],
        zoom: 14,
      }}
      mapStyle={mapStyle}
    >
      <Source id="route" type="geojson" data={lineData}>
        <Layer
          id="route"
          type="line"
          source="route"
          layout={{ "line-join": "round", "line-cap": "round" }}
          paint={{ "line-color": "#888", "line-width": 8 }}
        />
      </Source>
    </Map>
  );
};
