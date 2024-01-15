import React, { useEffect, useState } from "react";
import Map from "react-map-gl/maplibre";
import { Source, Layer, Marker } from "react-map-gl";
import { FeatureCollection, LineString } from "geojson";
import { Flex, Skeleton } from "antd";
import { Coordinates, Route, Waypoint } from "common";

const mapStyle = "http://localhost:8080/styles/basic-preview/style.json";

const flexStyle: React.CSSProperties = {
  height: "100%",
  width: "100%",
  justifyContent: "center",
  alignItems: "center",
};

interface Map3DProps {
  id: string;
  routes: Route[];
  waypoints: Waypoint[];
}

const transformRoutesToGeoJSON = (
  routes: Route[],
): FeatureCollection<LineString> => {
  const features = routes.map(route => {
    return {
      type: "Feature" as const,
      properties: {},
      geometry: route.geometry as LineString,
    };
  });

  return {
    type: "FeatureCollection",
    features,
  };
};

export const Map3D: React.FC<Map3DProps> = ({ id, routes, waypoints }) => {
  const [coordinates, setCoordinates] = useState<Coordinates>({
    lat: 0,
    lon: 0,
  });
  const [loadingCoordinates, setLoadingCoordinates] = useState(true);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(position => {
      setCoordinates({
        lon: position.coords.longitude,
        lat: position.coords.latitude,
      });
      setLoadingCoordinates(false);
    });
  }, []);

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
      id={id}
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
      {/*{waypoints.map((waypoint, index) => (*/}
      {/*  <Marker*/}
      {/*    key={index}*/}
      {/*    longitude={waypoint.location[0]}*/}
      {/*    latitude={waypoint.location[1]}*/}
      {/*  >*/}
      {/*    <svg*/}
      {/*      height="24"*/}
      {/*      version="1.1"*/}
      {/*      width="24"*/}
      {/*      xmlns="http://www.w3.org/2000/svg"*/}
      {/*    >*/}
      {/*      <g transform="translate(0 -1028.4)">*/}
      {/*        <path*/}
      {/*          d="m12.031 1030.4c-3.8657 0-6.9998 3.1-6.9998 7 0 1.3 0.4017 2.6 1.0938 3.7 0.0334 0.1 0.059 0.1 0.0938 0.2l4.3432 8c0.204 0.6 0.782 1.1 1.438 1.1s1.202-0.5 1.406-1.1l4.844-8.7c0.499-1 0.781-2.1 0.781-3.2 0-3.9-3.134-7-7-7zm-0.031 3.9c1.933 0 3.5 1.6 3.5 3.5 0 2-1.567 3.5-3.5 3.5s-3.5-1.5-3.5-3.5c0-1.9 1.567-3.5 3.5-3.5z"*/}
      {/*          fill="#c0392b"*/}
      {/*        />*/}
      {/*        <path*/}
      {/*          d="m12.031 1.0312c-3.8657 0-6.9998 3.134-6.9998 7 0 1.383 0.4017 2.6648 1.0938 3.7498 0.0334 0.053 0.059 0.105 0.0938 0.157l4.3432 8.062c0.204 0.586 0.782 1.031 1.438 1.031s1.202-0.445 1.406-1.031l4.844-8.75c0.499-0.963 0.781-2.06 0.781-3.2188 0-3.866-3.134-7-7-7zm-0.031 3.9688c1.933 0 3.5 1.567 3.5 3.5s-1.567 3.5-3.5 3.5-3.5-1.567-3.5-3.5 1.567-3.5 3.5-3.5z"*/}
      {/*          fill="#e74c3c"*/}
      {/*          transform="translate(0 1028.4)"*/}
      {/*        />*/}
      {/*      </g>*/}
      {/*    </svg>*/}
      {/*  </Marker>*/}
      {/*))}*/}
    </Map>
  );
};
