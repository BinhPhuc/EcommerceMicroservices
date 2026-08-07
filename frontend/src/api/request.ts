import type { z } from 'zod'

import {
  requestSchemaError,
  responseSchemaError,
  toApiError,
} from '@/api/api-error'
import { apiEnvelopeSchema } from '@/api/api-response'
import { apiClient } from '@/api/axios-client'
import { env } from '@/config/env'

const trustPayloadWithoutValidation = <TExpected>(payload: unknown): TExpected =>
  payload as TExpected

type PostJsonOptions<
  TRequestSchema extends z.ZodType,
  TResponseSchema extends z.ZodType,
> = {
  url: string
  body: z.input<TRequestSchema>
  requestSchema: TRequestSchema
  responseSchema: TResponseSchema
  skipAuth?: boolean
  signal?: AbortSignal
}

export const postJson = async <
  TRequestSchema extends z.ZodType,
  TResponseSchema extends z.ZodType,
>({
  url,
  body,
  requestSchema,
  responseSchema,
  skipAuth,
  signal,
}: PostJsonOptions<TRequestSchema, TResponseSchema>): Promise<
  z.output<TResponseSchema>
> => {
  const validatedBody = requestSchema.safeParse(body)

  if (!validatedBody.success) {
    throw requestSchemaError(url, validatedBody.error)
  }

  try {
    const response = await apiClient.post(url, validatedBody.data, {
      skipAuth,
      signal,
    })

    const envelope = apiEnvelopeSchema.safeParse(response.data)

    if (!envelope.success) {
      throw responseSchemaError(url, envelope.error)
    }

    if (!env.validateApiResponse) {
      return trustPayloadWithoutValidation<z.output<TResponseSchema>>(
        envelope.data.data,
      )
    }

    const parsedData = responseSchema.safeParse(envelope.data.data)

    if (!parsedData.success) {
      throw responseSchemaError(url, parsedData.error)
    }

    return parsedData.data
  } catch (error) {
    throw toApiError(error)
  }
}
