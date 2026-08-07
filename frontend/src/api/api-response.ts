import { z } from 'zod'

export type ApiResponse<TData> = {
  status_code: number
  message: string
  data: TData
}

export const apiResponseSchema = <TData extends z.ZodType>(dataSchema: TData) =>
  z.object({
    status_code: z.number().int(),
    message: z.string(),
    data: dataSchema,
  })

export const apiEnvelopeSchema = z.object({
  status_code: z.number().int(),
  message: z.string(),
  data: z.unknown(),
})

export const errorResponseSchema = z.object({
  status_code: z.number().int(),
  error: z.string(),
  message: z.string(),
  path: z.string(),
  timestamp: z.union([z.string(), z.number()]),
})

export const springDefaultErrorSchema = z.object({
  status: z.number().int(),
  error: z.string().optional(),
  message: z.string().optional(),
  path: z.string().optional(),
  timestamp: z.union([z.string(), z.number()]).optional(),
})

export const problemDetailSchema = z.object({
  status: z.number().int(),
  title: z.string().optional(),
  detail: z.string().optional(),
  instance: z.string().optional(),
})

export type ErrorResponse = z.infer<typeof errorResponseSchema>
