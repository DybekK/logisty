import React from "react"
import { Marker } from "react-map-gl"

interface Point {
  type: string
  coordinates: number[]
}

interface Step {
  location: Point
}

interface StepMarkerProps {
  step: Step
  index: number
}

export const StepMarker: React.FC<StepMarkerProps> = ({ step, index }) => {
  return (
    <Marker
      longitude={step.location.coordinates[0]}
      latitude={step.location.coordinates[1]}
      anchor="bottom"
    >
      <div
        style={{
          background: "#0f53ff",
          color: "white",
          width: "24px",
          height: "24px",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          borderRadius: "50%",
          cursor: "pointer",
          fontSize: "14px",
          fontWeight: "600",
          boxShadow: "0 2px 4px rgba(0,0,0,0.2)",
          border: "2px solid white",
          transition: "transform 0.2s ease, box-shadow 0.2s ease",
        }}
      >
        {index + 1}
      </div>
    </Marker>
  )
}
