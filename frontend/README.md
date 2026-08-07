# EcommerceClient

Frontend React cho backend microservice tại `../EcommerceMicroservices`.

**Quy ước dự án, sự thật về API backend và các cạm bẫy nằm ở [`CLAUDE.md`](./CLAUDE.md) — đọc file đó trước khi viết code.**

## Stack

Vite + React 19 + TypeScript · TanStack Router (file-based) · TanStack Query · axios · zod · Tailwind CSS v4 · shadcn/ui · lucide-react

## Bắt đầu

```bash
cp .env.example .env
npm install
npm run dev
```

Dev server **bắt buộc chạy cổng 5173** vì CORS của API Gateway khoá cứng origin `http://localhost:5173`.

## Scripts

| Lệnh | Việc |
|---|---|
| `npm run dev` | Dev server (strictPort 5173) |
| `npm run build` | `tsc -b && vite build` |
| `npm run lint` | oxlint |
| `npm run preview` | Xem bản build |

## Cần backend chạy

Thứ tự: `docker compose -f ../EcommerceMicroservices/infrastructure/docker-compose.yml up -d` → eureka (8761) → auth-service (8083) → product-service (8080) → order-service (8082) → gateway (8085).

Realm `ecom-realm`, client `ecom`, role `USER`/`ADMIN` phải tạo tay trong Keycloak tại `http://localhost:8086`.
