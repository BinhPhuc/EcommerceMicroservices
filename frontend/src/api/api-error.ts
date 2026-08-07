import axios from 'axios'
import { z } from 'zod'

import {
  errorResponseSchema,
  problemDetailSchema,
  springDefaultErrorSchema,
} from '@/api/api-response'

export type ApiErrorKind =
  | 'network'
  | 'timeout'
  | 'canceled'
  | 'http'
  | 'request-schema'
  | 'response-schema'
  | 'unknown'

const FALLBACK_MESSAGE_BY_STATUS: Record<number, string> = {
  400: 'Yêu cầu không hợp lệ.',
  401: 'Phiên đăng nhập đã hết hạn hoặc chưa đăng nhập.',
  403: 'Tài khoản không có quyền thực hiện thao tác này.',
  404: 'Không tìm thấy dữ liệu.',
  409: 'Dữ liệu đã tồn tại.',
  500: 'Máy chủ gặp sự cố.',
  502: 'Dịch vụ phía sau không phản hồi.',
  503: 'Dịch vụ tạm thời không khả dụng.',
  504: 'Máy chủ phản hồi quá chậm.',
}

const GENERIC_MESSAGE = 'Đã xảy ra lỗi không xác định.'
const NETWORK_MESSAGE = 'Không kết nối được tới máy chủ.'
const TIMEOUT_MESSAGE = 'Máy chủ phản hồi quá chậm.'
const CANCELED_MESSAGE = 'Yêu cầu đã bị huỷ.'

type ApiErrorOptions = {
  kind: ApiErrorKind
  message: string
  status: number
  statusCode?: number
  path?: string
  timestamp?: string | number
  fieldErrors?: Record<string, string[]>
  cause?: unknown
}

export class ApiError extends Error {
  readonly kind: ApiErrorKind
  readonly status: number
  readonly statusCode?: number
  readonly path?: string
  readonly timestamp?: string | number
  readonly fieldErrors?: Record<string, string[]>

  constructor(options: ApiErrorOptions) {
    super(options.message, { cause: options.cause })
    this.name = 'ApiError'
    this.kind = options.kind
    this.status = options.status
    this.statusCode = options.statusCode
    this.path = options.path
    this.timestamp = options.timestamp
    this.fieldErrors = options.fieldErrors
  }

  get isUnauthorized() {
    return this.status === 401
  }

  get isForbidden() {
    return this.status === 403
  }

  get isClientError() {
    return this.status >= 400 && this.status < 500
  }
}

export const isApiError = (error: unknown): error is ApiError =>
  error instanceof ApiError

const fallbackMessageFor = (status: number) =>
  FALLBACK_MESSAGE_BY_STATUS[status] ?? GENERIC_MESSAGE

const fromResponseBody = (status: number, body: unknown): ApiError => {
  const errorResponse = errorResponseSchema.safeParse(body)
  if (errorResponse.success) {
    return new ApiError({
      kind: 'http',
      status,
      statusCode: errorResponse.data.status_code,
      message: errorResponse.data.message || fallbackMessageFor(status),
      path: errorResponse.data.path,
      timestamp: errorResponse.data.timestamp,
    })
  }

  const springDefault = springDefaultErrorSchema.safeParse(body)
  if (springDefault.success) {
    return new ApiError({
      kind: 'http',
      status,
      statusCode: springDefault.data.status,
      message: springDefault.data.message || fallbackMessageFor(status),
      path: springDefault.data.path,
      timestamp: springDefault.data.timestamp,
    })
  }

  const problemDetail = problemDetailSchema.safeParse(body)
  if (problemDetail.success) {
    return new ApiError({
      kind: 'http',
      status,
      statusCode: problemDetail.data.status,
      message:
        problemDetail.data.detail ||
        problemDetail.data.title ||
        fallbackMessageFor(status),
      path: problemDetail.data.instance,
    })
  }

  if (typeof body === 'string' && body.trim().length > 0) {
    return new ApiError({ kind: 'http', status, message: body })
  }

  return new ApiError({ kind: 'http', status, message: fallbackMessageFor(status) })
}

export const requestSchemaError = (path: string, error: z.ZodError): ApiError =>
  new ApiError({
    kind: 'request-schema',
    status: 0,
    message: `Dữ liệu gửi lên ${path} không hợp lệ.`,
    fieldErrors: z.flattenError(error).fieldErrors as Record<string, string[]>,
    cause: error,
  })

export const responseSchemaError = (
  path: string,
  error: z.ZodError,
): ApiError =>
  new ApiError({
    kind: 'response-schema',
    status: 0,
    message: `Dữ liệu trả về từ ${path} không đúng định dạng mong đợi.`,
    fieldErrors: z.flattenError(error).fieldErrors as Record<string, string[]>,
    cause: error,
  })

export const toApiError = (error: unknown): ApiError => {
  if (isApiError(error)) {
    return error
  }

  if (axios.isCancel(error)) {
    return new ApiError({
      kind: 'canceled',
      status: 0,
      message: CANCELED_MESSAGE,
      cause: error,
    })
  }

  if (axios.isAxiosError(error)) {
    if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
      return new ApiError({
        kind: 'timeout',
        status: 0,
        message: TIMEOUT_MESSAGE,
        cause: error,
      })
    }

    if (!error.response) {
      return new ApiError({
        kind: 'network',
        status: 0,
        message: NETWORK_MESSAGE,
        cause: error,
      })
    }

    const apiError = fromResponseBody(error.response.status, error.response.data)
    return new ApiError({
      kind: apiError.kind,
      status: apiError.status,
      statusCode: apiError.statusCode,
      message: apiError.message,
      path: apiError.path ?? error.config?.url,
      timestamp: apiError.timestamp,
      cause: error,
    })
  }

  if (error instanceof Error) {
    return new ApiError({
      kind: 'unknown',
      status: 0,
      message: error.message || GENERIC_MESSAGE,
      cause: error,
    })
  }

  return new ApiError({ kind: 'unknown', status: 0, message: GENERIC_MESSAGE })
}
