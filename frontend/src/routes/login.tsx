import { zodResolver } from '@hookform/resolvers/zod'
import {
  createFileRoute,
  Link,
  redirect,
  useNavigate,
  useRouter,
} from '@tanstack/react-router'
import { LogIn } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { z } from 'zod'

import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Field, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { env } from '@/config/env'
import { loginRequestSchema, type LoginRequest } from '@/dto/auth/login'
import { useLogin } from '@/hooks/auth/use-login'

const loginSearchSchema = z.object({
  redirect: z.string().optional(),
})

const LoginPage = () => {
  const { redirect: redirectHref } = Route.useSearch()
  const navigate = useNavigate()
  const router = useRouter()
  const login = useLogin()

  const form = useForm<LoginRequest>({
    resolver: zodResolver(loginRequestSchema),
    defaultValues: { username: '', password: '' },
  })

  const onSubmit = form.handleSubmit((values) => {
    login.mutate(values, {
      onSuccess: async () => {
        toast.success('Đăng nhập thành công')
        await router.invalidate()
        if (redirectHref) {
          router.history.replace(redirectHref)
          return
        }
        navigate({ to: '/', replace: true })
      },
      onError: (error) => toast.error(error.message),
    })
  })

  return (
    <div className="flex min-h-svh items-center justify-center bg-background p-4">
      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle>Đăng nhập {env.appName}</CardTitle>
          <CardDescription>
            Nhập tài khoản Keycloak để tiếp tục.
          </CardDescription>
        </CardHeader>
        <form onSubmit={onSubmit}>
          <CardContent>
            <FieldGroup>
              <Field data-invalid={Boolean(form.formState.errors.username)}>
                <FieldLabel htmlFor="username">Tên đăng nhập</FieldLabel>
                <Input
                  id="username"
                  autoComplete="username"
                  aria-invalid={Boolean(form.formState.errors.username)}
                  {...form.register('username')}
                />
                <FieldError errors={[form.formState.errors.username]} />
              </Field>
              <Field data-invalid={Boolean(form.formState.errors.password)}>
                <FieldLabel htmlFor="password">Mật khẩu</FieldLabel>
                <Input
                  id="password"
                  type="password"
                  autoComplete="current-password"
                  aria-invalid={Boolean(form.formState.errors.password)}
                  {...form.register('password')}
                />
                <FieldError errors={[form.formState.errors.password]} />
              </Field>
            </FieldGroup>
          </CardContent>
          <CardFooter className="mt-6 flex-col items-stretch gap-3">
            <Button type="submit" disabled={login.isPending}>
              <LogIn />
              {login.isPending ? 'Đang đăng nhập...' : 'Đăng nhập'}
            </Button>
            <p className="text-center text-sm text-muted-foreground">
              Chưa có tài khoản?{' '}
              <Link to="/register" className="underline underline-offset-4">
                Đăng ký
              </Link>
            </p>
          </CardFooter>
        </form>
      </Card>
    </div>
  )
}

export const Route = createFileRoute('/login')({
  validateSearch: loginSearchSchema,
  beforeLoad: ({ context }) => {
    if (context.auth.isAuthenticated) {
      throw redirect({ to: '/' })
    }
  },
  component: LoginPage,
})
