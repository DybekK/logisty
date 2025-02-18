import React, { useEffect, useState } from "react"
import { MapRef, useMap } from "react-map-gl"
import Map from "react-map-gl/maplibre"

import { Flex, Skeleton } from "antd"

import { Feature, FeatureCollection, LineString } from "geojson"

import { SourceLayer } from "@/components"
import { StepMarker } from "@/components/Map3D/StepMarker"

const { VITE_MAP_GL_STYLE } = import.meta.env

const flexStyle: React.CSSProperties = {
  height: "100%",
  width: "100%",
  justifyContent: "center",
  alignItems: "center",
}

interface Coordinates {
  lat: number
  lon: number
}

interface Route {
  coordinates: number[][]
}

interface Point {
  type: string
  coordinates: number[]
}

interface Step {
  location: Point
}

interface Map3DProps {
  id: string
  plannedRoute?: Route
  driverRoute?: Route
  steps: Step[]
}

const transformRoutesToGeoJSON = (
  plannedRoute?: Route,
  driverRoute?: Route,
): FeatureCollection<LineString> => {
  const features: Array<Feature<LineString>> = []

  if (plannedRoute?.coordinates) {
    features.push({
      type: "Feature",
      properties: { routeType: "planned" },
      geometry: {
        type: "LineString",
        coordinates: plannedRoute.coordinates,
      },
    })
  }

  if (driverRoute?.coordinates) {
    features.push({
      type: "Feature",
      properties: { routeType: "driver" },
      geometry: {
        type: "LineString",
        coordinates: driverRoute.coordinates,
      },
    })
  }

  return {
    type: "FeatureCollection",
    features,
  }
}

const fitRoutesToBounds = (routes: Route[], mapInstance?: MapRef): void => {
  const allCoords = routes.flatMap(route => route.coordinates)
  if (allCoords.length > 0) {
    const lons = allCoords.map(coord => coord[0])
    const lats = allCoords.map(coord => coord[1])
    const west = Math.min(...lons)
    const east = Math.max(...lons)
    const south = Math.min(...lats)
    const north = Math.max(...lats)
    mapInstance?.fitBounds(
      [
        [west, south],
        [east, north],
      ],
      {
        padding: 20,
        duration: 1000,
      },
    )
  }
}

export const Map3D: React.FC<Map3DProps> = ({
  id,
  plannedRoute,
  driverRoute,
  steps,
}) => {
  const [coordinates, setCoordinates] = useState<Coordinates>({
    lat: 0,
    lon: 0,
  })
  const [loadingCoordinates, setLoadingCoordinates] = useState(true)
  const { features } = transformRoutesToGeoJSON(plannedRoute, driverRoute)
  const mapInstance = useMap()

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(position => {
      setCoordinates({
        lon: position.coords.longitude,
        lat: position.coords.latitude,
      })
      setLoadingCoordinates(false)
    })
  }, [])
  useEffect(() => {
    const routes = []
    if (plannedRoute) routes.push(plannedRoute)
    if (driverRoute) routes.push(driverRoute)
    fitRoutesToBounds(routes, mapInstance[id])
  }, [mapInstance[id], plannedRoute, driverRoute])

  if (loadingCoordinates) {
    return (
      <Flex style={flexStyle}>
        <Skeleton.Image active={true} />
      </Flex>
    )
  }

  return (
    <Map
      id={id}
      initialViewState={{
        longitude: coordinates.lon,
        latitude: coordinates.lat,
        zoom: 14,
      }}
      mapStyle={VITE_MAP_GL_STYLE}
    >
      {features.map((feature, index) => (
        <SourceLayer
          key={index}
          index={index}
          featuresLength={features.length}
          feature={feature}
        />
      ))}
      {steps.map((step, stepIndex) => (
        <StepMarker key={stepIndex} step={step} index={stepIndex} />
      ))}
    </Map>
  )
}
