export type BackendError = string

export interface BackendErrors<E extends BackendError> {
  errors: E[]
}

export type BackendResponse<T, E extends BackendError> = T | BackendErrors<E>
