import { useMutation } from "@tanstack/react-query"
import { useTranslation } from "react-i18next"

import { message } from "antd"

import { BackgroundGeolocationPlugin } from "@capacitor-community/background-geolocation"
import { registerPlugin } from "@capacitor/core"
import { LocalNotifications } from "@capacitor/local-notifications"

import { useAppDispatch, useAppSelector } from "@/common/store"
import { addCoordinate, clearTrack } from "@/features/order"
import { trackDriverLocation } from "@/features/order"

const BackgroundGeolocation = registerPlugin<BackgroundGeolocationPlugin>(
  "BackgroundGeolocation",
)

interface UseBackgroundGeolocationOptions {
  backgroundMessage?: string
  backgroundTitle?: string
  distanceFilter?: number
  stale?: boolean
  fleetId?: string
  orderId?: string
}

export const useBackgroundGeolocation = (
  options: UseBackgroundGeolocationOptions = {},
) => {
  const { t } = useTranslation("order")
  const dispatch = useAppDispatch()
  const { route } = useAppSelector(state => state.tracking)

  let watcherId: string | undefined
  let trackingInterval: number | undefined

  const { mutateAsync: trackLocation } = useMutation({
    mutationFn: async () => {
      if (!options.fleetId || !options.orderId) {
        return Promise.reject("Missing fleetId or orderId")
      }

      return trackDriverLocation(options.fleetId, options.orderId, { route })
    },
    onSuccess: () => {
      dispatch(clearTrack())
    },
  })

  const startTracking = () => {
    if (trackingInterval) return

    trackingInterval = setInterval(async () => {
      trackLocation()
    }, 10 * 1000)
  }

  const stopTracking = () => {
    clearInterval(trackingInterval)
  }

  const startWatching = async () => {
    if (watcherId) {
      return
    }

    try {
      LocalNotifications.requestPermissions()
      watcherId = await BackgroundGeolocation.addWatcher(
        {
          backgroundMessage:
            options.backgroundMessage || "Location tracking is active",
          backgroundTitle: options.backgroundTitle || "Using your location",
          requestPermissions: true,
          stale: options.stale || false,
          distanceFilter: options.distanceFilter || 0,
        },
        (location, error) => {
          if (error) {
            message.error(t("tracking.error"))
          }

          if (location) {
            dispatch(addCoordinate([location.longitude, location.latitude]))
          }

          startTracking()
        },
      )
    } catch (e) {
      console.error(e)
    }
  }

  const stopWatching = async () => {
    if (watcherId) {
      await BackgroundGeolocation.removeWatcher({ id: watcherId })
      watcherId = undefined
      stopTracking()
    }
  }

  return {
    startWatching,
    stopWatching,
  }
}
