import { MapContainer, TileLayer } from "react-leaflet";
import React from "react";

export const LeafletMap: React.FC = () => {
  return (
    <MapContainer
      center={[51.505, -0.09]}
      zoom={10}
      style={{ height: "100%", width: "100%" }}
    >
      <TileLayer url="https://tiles.stadiamaps.com/tiles/outdoors/{z}/{x}/{y}{r}.png" />
    </MapContainer>
  );
};
