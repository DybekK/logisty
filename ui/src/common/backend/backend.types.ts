import { AxiosError } from "axios"
import { P } from "ts-pattern"

export interface BackendErrors {
  errors: string[]
}

export type AxiosBackendError = AxiosError<BackendErrors>

export const patternErrors = <T extends string>(
  errorTypes: readonly [T, ...T[]],
) => P.shape({ errors: P.array(P.union(...errorTypes)) })
