import { useNavigate } from '@tanstack/react-router'
import { LogOut } from 'lucide-react'

import { ModeToggle } from '@/components/mode-toggle'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { env } from '@/config/env'
import { useAuth } from '@/features/auth/use-auth'
import { useLogout } from '@/hooks/auth/use-logout'

const initialsOf = (value: string) =>
  value.slice(0, 2).toUpperCase() || '??'

export const AppShell = ({ children }: { children: React.ReactNode }) => {
  const { user } = useAuth()
  const logout = useLogout()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate({ to: '/login', replace: true })
  }

  return (
    <div className="flex min-h-svh flex-col bg-background">
      <header className="sticky top-0 z-10 border-b bg-background/95 backdrop-blur">
        <div className="mx-auto flex h-14 w-full max-w-6xl items-center gap-3 px-4">
          <span className="font-semibold">{env.appName}</span>
          <Separator orientation="vertical" className="h-6" />
          {user ? (
            <div className="flex items-center gap-2">
              <Avatar className="size-7">
                <AvatarFallback>{initialsOf(user.username)}</AvatarFallback>
              </Avatar>
              <span className="text-sm text-muted-foreground">
                {user.username}
              </span>
              {user.roles.map((role) => (
                <Badge key={role} variant="secondary">
                  {role}
                </Badge>
              ))}
            </div>
          ) : null}
          <div className="ml-auto flex items-center gap-2">
            <ModeToggle />
            <Button variant="outline" size="sm" onClick={handleLogout}>
              <LogOut />
              Đăng xuất
            </Button>
          </div>
        </div>
      </header>
      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-6">
        {children}
      </main>
    </div>
  )
}
