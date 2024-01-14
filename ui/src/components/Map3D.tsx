import React, { useEffect, useState } from "react";
import Map from "react-map-gl/maplibre";
import { Source, Layer } from "react-map-gl";
import { FeatureCollection, LineString, Position } from "geojson";
import { Flex, Skeleton } from "antd";
import { Coordinates, Route } from "common";

const mapStyle = "http://localhost:8080/styles/basic-preview/style.json";

const flexStyle: React.CSSProperties = {
  height: "100%",
  width: "100%",
  justifyContent: "center",
  alignItems: "center",
};

interface Map3DProps {
  routes: Route[];
  focusOnCoordinates?: Coordinates;
}

const transformRoutesToGeoJSON = (
  routes: Route[],
): FeatureCollection<LineString> => {
  const features = routes.map((route) => {
    const coordinates: Position[] = route.legs.flatMap((leg) =>
      leg.steps.flatMap((step) =>
        step.intersections.map(
          (intersection) => intersection.location as Position,
        ),
      ),
    );

    return {
      type: "Feature" as const,
      properties: {},
      geometry: {
        type: "LineString" as const,
        coordinates,
      },
    };
  });

  return {
    type: "FeatureCollection",
    features,
  };
};

export const Map3D: React.FC<Map3DProps> = ({ routes, focusOnCoordinates }) => {
  const [coordinates, setCoordinates] = useState<Coordinates>({
    lat: 0,
    lon: 0,
  });
  const [loadingCoordinates, setLoadingCoordinates] = useState(true);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition((position) => {
      setCoordinates({
        lon: position.coords.longitude,
        lat: position.coords.latitude,
      });
      setLoadingCoordinates(false);
    });
  }, []);

  useEffect(() => {
    if (!focusOnCoordinates) return;
    setCoordinates(focusOnCoordinates);
  }, [focusOnCoordinates]);

  const lineData = transformRoutesToGeoJSON(routes);

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
        longitude: coordinates.lon,
        latitude: coordinates.lat,
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
          paint={{ "line-color": "blue", "line-width": 5 }}
        />
      </Source>
    </Map>
  );
};
