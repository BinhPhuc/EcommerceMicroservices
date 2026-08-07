import { zodResolver } from '@hookform/resolvers/zod'
import { createFileRoute } from '@tanstack/react-router'
import { PackagePlus, PackageSearch, ShoppingCart, Tags } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'

import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Field, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  createCategoryRequestSchema,
  type CreateCategoryRequest,
} from '@/dto/category/create-category'
import {
  createOrderItemRequestSchema,
  type CreateOrderItemRequest,
} from '@/dto/order/create-order'
import {
  createProductRequestSchema,
  type CreateProductRequest,
} from '@/dto/product/create-product'
import { useAuth } from '@/features/auth/use-auth'
import { useCreateCategory } from '@/hooks/category/use-create-category'
import { useCreateOrder } from '@/hooks/order/use-create-order'
import { useCreateProduct } from '@/hooks/product/use-create-product'
import { useProductsByIds } from '@/hooks/product/use-products-by-ids'

const splitIds = (value: string) =>
  value
    .split(',')
    .map((part) => part.trim())
    .filter(Boolean)

const CategoryCard = () => {
  const createCategory = useCreateCategory()
  const form = useForm<CreateCategoryRequest>({
    resolver: zodResolver(createCategoryRequestSchema),
    defaultValues: { name: '', parent_id: null },
  })

  const onSubmit = form.handleSubmit((values) => {
    createCategory.mutate(
      { ...values, parent_id: values.parent_id?.trim() || null },
      {
        onSuccess: (data) => toast.success(`Đã tạo danh mục ${data.name}`),
        onError: (error) => toast.error(error.message),
      },
    )
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Tags className="size-4" />
          Tạo danh mục
        </CardTitle>
        <CardDescription>POST /api/v1/categories/create</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit}>
          <FieldGroup>
            <Field data-invalid={Boolean(form.formState.errors.name)}>
              <FieldLabel htmlFor="category-name">Tên danh mục</FieldLabel>
              <Input id="category-name" {...form.register('name')} />
              <FieldError errors={[form.formState.errors.name]} />
            </Field>
            <Field>
              <FieldLabel htmlFor="category-parent">
                Danh mục cha (bỏ trống nếu là gốc)
              </FieldLabel>
              <Input
                id="category-parent"
                {...form.register('parent_id')}
                value={form.watch('parent_id') ?? ''}
              />
            </Field>
            <Button type="submit" disabled={createCategory.isPending}>
              Tạo danh mục
            </Button>
          </FieldGroup>
        </form>
      </CardContent>
    </Card>
  )
}

const ProductCard = () => {
  const createProduct = useCreateProduct()
  const form = useForm<CreateProductRequest>({
    resolver: zodResolver(createProductRequestSchema),
    defaultValues: { name: '', price: 0, stock: 0, category_id: '' },
  })

  const onSubmit = form.handleSubmit((values) => {
    createProduct.mutate(values, {
      onSuccess: (data) => toast.success(`Đã tạo sản phẩm ${data.name}`),
      onError: (error) => toast.error(error.message),
    })
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <PackagePlus className="size-4" />
          Tạo sản phẩm
        </CardTitle>
        <CardDescription>POST /api/v1/products/create</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit}>
          <FieldGroup>
            <Field data-invalid={Boolean(form.formState.errors.name)}>
              <FieldLabel htmlFor="product-name">Tên sản phẩm</FieldLabel>
              <Input id="product-name" {...form.register('name')} />
              <FieldError errors={[form.formState.errors.name]} />
            </Field>
            <div className="grid gap-5 sm:grid-cols-2">
              <Field data-invalid={Boolean(form.formState.errors.price)}>
                <FieldLabel htmlFor="product-price">Giá</FieldLabel>
                <Input
                  id="product-price"
                  type="number"
                  {...form.register('price', { valueAsNumber: true })}
                />
                <FieldError errors={[form.formState.errors.price]} />
              </Field>
              <Field data-invalid={Boolean(form.formState.errors.stock)}>
                <FieldLabel htmlFor="product-stock">Tồn kho</FieldLabel>
                <Input
                  id="product-stock"
                  type="number"
                  {...form.register('stock', { valueAsNumber: true })}
                />
                <FieldError errors={[form.formState.errors.stock]} />
              </Field>
            </div>
            <Field data-invalid={Boolean(form.formState.errors.category_id)}>
              <FieldLabel htmlFor="product-category">Mã danh mục</FieldLabel>
              <Input id="product-category" {...form.register('category_id')} />
              <FieldError errors={[form.formState.errors.category_id]} />
            </Field>
            <Button type="submit" disabled={createProduct.isPending}>
              Tạo sản phẩm
            </Button>
          </FieldGroup>
        </form>
      </CardContent>
    </Card>
  )
}

const ProductLookupCard = () => {
  const [rawIds, setRawIds] = useState('')
  const [productIds, setProductIds] = useState<string[]>([])
  const products = useProductsByIds(productIds)

  return (
    <Card className="lg:col-span-2">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <PackageSearch className="size-4" />
          Tra cứu sản phẩm theo mã
        </CardTitle>
        <CardDescription>POST /api/v1/products/get-by-ids</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <form
          className="flex flex-col gap-3 sm:flex-row"
          onSubmit={(event) => {
            event.preventDefault()
            setProductIds(splitIds(rawIds))
          }}
        >
          <Input
            placeholder="Nhập các mã sản phẩm, cách nhau bằng dấu phẩy"
            value={rawIds}
            onChange={(event) => setRawIds(event.target.value)}
          />
          <Button type="submit">Tra cứu</Button>
        </form>

        {products.isPending && productIds.length > 0 ? (
          <Skeleton className="h-24 w-full" />
        ) : null}

        {products.isError ? (
          <Alert variant="destructive">
            <AlertTitle>Không tra cứu được</AlertTitle>
            <AlertDescription>{products.error.message}</AlertDescription>
          </Alert>
        ) : null}

        {products.data && products.data.length > 0 ? (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Mã</TableHead>
                <TableHead>Tên</TableHead>
                <TableHead>Giá</TableHead>
                <TableHead>Tồn kho</TableHead>
                <TableHead>Danh mục</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {products.data.map((product) => (
                <TableRow key={product.id}>
                  <TableCell className="font-mono text-xs">
                    {product.id}
                  </TableCell>
                  <TableCell>{product.name}</TableCell>
                  <TableCell>{product.price}</TableCell>
                  <TableCell>{product.stock}</TableCell>
                  <TableCell className="font-mono text-xs">
                    {product.category_id}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : null}
      </CardContent>
    </Card>
  )
}

const OrderCard = () => {
  const createOrder = useCreateOrder()
  const form = useForm<CreateOrderItemRequest>({
    resolver: zodResolver(createOrderItemRequestSchema),
    defaultValues: { product_id: '', quantity: 1 },
  })

  const onSubmit = form.handleSubmit((values) => {
    createOrder.mutate([values], {
      onSuccess: (data) =>
        toast.success(
          `Đơn hàng ${data.status}, tổng tiền ${data.total_amount ?? 0}`,
        ),
      onError: (error) => toast.error(error.message),
    })
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <ShoppingCart className="size-4" />
          Tạo đơn hàng
        </CardTitle>
        <CardDescription>POST /api/v1/orders/create</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit}>
          <FieldGroup>
            <Field data-invalid={Boolean(form.formState.errors.product_id)}>
              <FieldLabel htmlFor="order-product">Mã sản phẩm</FieldLabel>
              <Input id="order-product" {...form.register('product_id')} />
              <FieldError errors={[form.formState.errors.product_id]} />
            </Field>
            <Field data-invalid={Boolean(form.formState.errors.quantity)}>
              <FieldLabel htmlFor="order-quantity">Số lượng</FieldLabel>
              <Input
                id="order-quantity"
                type="number"
                {...form.register('quantity', { valueAsNumber: true })}
              />
              <FieldError errors={[form.formState.errors.quantity]} />
            </Field>
            <Button type="submit" disabled={createOrder.isPending}>
              Đặt hàng
            </Button>
          </FieldGroup>
        </form>
        {createOrder.data ? (
          <Badge className="mt-4" variant="secondary">
            {createOrder.data.status}
          </Badge>
        ) : null}
      </CardContent>
    </Card>
  )
}

const DashboardPage = () => {
  const { user } = useAuth()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Bảng thử API</h1>
        <p className="text-sm text-muted-foreground">
          Đang đăng nhập với {user?.fullName} ({user?.id})
        </p>
      </div>

      {user && user.roles.length === 0 ? (
        <Alert variant="destructive">
          <AlertTitle>Token không chứa role nào</AlertTitle>
          <AlertDescription>
            Gateway sẽ trả 403 cho mọi endpoint sản phẩm và đơn hàng. Cần gán
            role cho client trong Keycloak.
          </AlertDescription>
        </Alert>
      ) : null}

      <div className="grid gap-6 lg:grid-cols-2">
        <CategoryCard />
        <ProductCard />
        <ProductLookupCard />
        <OrderCard />
      </div>
    </div>
  )
}

export const Route = createFileRoute('/_authenticated/')({
  component: DashboardPage,
})
