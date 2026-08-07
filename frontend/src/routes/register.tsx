import { zodResolver } from '@hookform/resolvers/zod'
import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { UserPlus } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'

import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { userRoleSchema, type UserRole } from '@/dto/common/enums'
import {
  registerRequestSchema,
  type RegisterRequest,
} from '@/dto/auth/register'
import { useRegister } from '@/hooks/auth/use-register'

const RegisterPage = () => {
  const navigate = useNavigate()
  const register = useRegister()

  const form = useForm<RegisterRequest>({
    resolver: zodResolver(registerRequestSchema),
    defaultValues: {
      username: '',
      first_name: '',
      last_name: '',
      email: '',
      password: '',
      role: 'USER',
    },
  })

  const onSubmit = form.handleSubmit((values) => {
    register.mutate(values, {
      onSuccess: (message) => {
        toast.success(message)
        navigate({ to: '/login' })
      },
      onError: (error) => toast.error(error.message),
    })
  })

  return (
    <div className="flex min-h-svh items-center justify-center bg-background p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Tạo tài khoản</CardTitle>
          <CardDescription>
            Tài khoản được tạo trực tiếp trên Keycloak realm cấu hình ở backend.
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

              <div className="grid gap-5 sm:grid-cols-2">
                <Field data-invalid={Boolean(form.formState.errors.first_name)}>
                  <FieldLabel htmlFor="first_name">Họ</FieldLabel>
                  <Input
                    id="first_name"
                    autoComplete="given-name"
                    aria-invalid={Boolean(form.formState.errors.first_name)}
                    {...form.register('first_name')}
                  />
                  <FieldError errors={[form.formState.errors.first_name]} />
                </Field>
                <Field data-invalid={Boolean(form.formState.errors.last_name)}>
                  <FieldLabel htmlFor="last_name">Tên</FieldLabel>
                  <Input
                    id="last_name"
                    autoComplete="family-name"
                    aria-invalid={Boolean(form.formState.errors.last_name)}
                    {...form.register('last_name')}
                  />
                  <FieldError errors={[form.formState.errors.last_name]} />
                </Field>
              </div>

              <Field data-invalid={Boolean(form.formState.errors.email)}>
                <FieldLabel htmlFor="email">Email</FieldLabel>
                <Input
                  id="email"
                  type="email"
                  autoComplete="email"
                  aria-invalid={Boolean(form.formState.errors.email)}
                  {...form.register('email')}
                />
                <FieldError errors={[form.formState.errors.email]} />
              </Field>

              <Field data-invalid={Boolean(form.formState.errors.password)}>
                <FieldLabel htmlFor="password">Mật khẩu</FieldLabel>
                <Input
                  id="password"
                  type="password"
                  autoComplete="new-password"
                  aria-invalid={Boolean(form.formState.errors.password)}
                  {...form.register('password')}
                />
                <FieldError errors={[form.formState.errors.password]} />
              </Field>

              <Field data-invalid={Boolean(form.formState.errors.role)}>
                <FieldLabel htmlFor="role">Vai trò</FieldLabel>
                <Select
                  value={form.watch('role')}
                  onValueChange={(value) =>
                    form.setValue('role', value as UserRole, {
                      shouldValidate: true,
                    })
                  }
                >
                  <SelectTrigger id="role" className="w-full">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {userRoleSchema.options.map((role) => (
                      <SelectItem key={role} value={role}>
                        {role}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <FieldDescription>
                  Vai trò phải tồn tại sẵn trong realm Keycloak.
                </FieldDescription>
                <FieldError errors={[form.formState.errors.role]} />
              </Field>
            </FieldGroup>
          </CardContent>
          <CardFooter className="mt-6 flex-col items-stretch gap-3">
            <Button type="submit" disabled={register.isPending}>
              <UserPlus />
              {register.isPending ? 'Đang tạo tài khoản...' : 'Đăng ký'}
            </Button>
            <p className="text-center text-sm text-muted-foreground">
              Đã có tài khoản?{' '}
              <Link to="/login" className="underline underline-offset-4">
                Đăng nhập
              </Link>
            </p>
          </CardFooter>
        </form>
      </Card>
    </div>
  )
}

export const Route = createFileRoute('/register')({
  component: RegisterPage,
})
