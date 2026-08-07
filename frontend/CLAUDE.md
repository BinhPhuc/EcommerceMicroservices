# EcommerceClient — Quy ước dự án

> File này là bộ nhớ dài hạn của dự án. Bất kỳ AI agent nào làm việc trên repo này **phải đọc hết file này trước khi viết dòng code đầu tiên**. Claude Code tự nạp file này mỗi phiên; với agent khác, bảo nó đọc `CLAUDE.md`.

---

## 1. Quy tắc bắt buộc (do chủ dự án đề ra)

### 1.1 Không viết comment trong code
Tuyệt đối **không có một dòng comment nào** trong code (`//`, `/* */`, JSDoc). Tên hàm, tên biến, tên file phải tự giải thích. Nếu thấy cần comment để giải thích, đó là dấu hiệu phải đặt lại tên hoặc tách hàm.

Ngoại lệ duy nhất: file do công cụ sinh ra (`src/routeTree.gen.ts`) và code vendored của shadcn trong `src/components/ui/` — không sửa comment sẵn có ở đó.

### 1.2 Không hardcode
Mọi giá trị **có thể thay đổi** phải nằm trong `.env` và đi qua `src/config/env.ts`.

- URL backend, timeout, key localStorage, cổng dev server, cấu hình query → `.env`.
- Đường dẫn endpoint nội bộ (`/api/v1/products/create`...) → hằng số trong `src/api/endpoints.ts`, vì chúng gần như không đổi.
- **Chỉ file `src/config/env.ts` được phép đọc `import.meta.env`.** Không file nào khác.
- Khi thêm biến môi trường mới: thêm vào `.env`, `.env.example`, và schema trong `env.ts`. Thiếu biến thì app **throw ngay lúc khởi động**, không im lặng chạy tiếp.

### 1.3 Luôn dùng component có sẵn của shadcn
Không bao giờ tự viết lại button, input, modal, dialog, dropdown, table, select, toast, badge, alert... Nếu shadcn có, dùng shadcn:

```bash
npx shadcn@latest add <tên-component>
```

Chỉ tự viết component khi shadcn **không** cung cấp. Component tự viết đặt ở `src/components/`, **không** đặt vào `src/components/ui/` (thư mục đó dành riêng cho shadcn).

Không sửa file trong `src/components/ui/` trừ khi bắt buộc để chạy được ngoài Next.js. Hiện có đúng một sửa đổi như vậy: `sonner.tsx` đổi `next-themes` → `@/hooks/use-theme`.

### 1.4 Type safe tuyệt đối
- Không `any`.
- Không dùng `as` để lách kiểu. Toàn dự án hiện có **đúng một** cast được phép, nằm trong `trustPayloadWithoutValidation` ở `src/api/request.ts`, phục vụ cờ tắt validate response.
- DTO là zod schema, type suy ra bằng `z.infer` / `z.output`. **zod là nguồn sự thật**, không viết interface tay rồi để lệch schema.

### 1.5 Mọi lời gọi API đi qua API Gateway
- **Không bao giờ gọi trực tiếp service bên dưới** (product-service:8080, order-service:8082, auth-service:8083). Chỉ gọi gateway.
- Chỉ tồn tại **một** axios instance công khai: `apiClient` trong `src/api/axios-client.ts`. Không tạo instance thứ hai, không dùng `fetch()` trực tiếp.
- Không viết lời gọi API trong component. Luồng bắt buộc: `component → hook (TanStack Query) → service → postJson() → apiClient`.

### 1.6 Stack cố định
| Hạng mục | Bắt buộc dùng |
|---|---|
| Router | **TanStack Router** (file-based). Không dùng react-router. |
| Data fetching | **TanStack Query**. Không tự viết useEffect + useState để fetch. |
| HTTP client | **axios** (qua `postJson`). |
| Validation | **zod** cho cả request và response. |
| Form | **react-hook-form** + `zodResolver`, dùng lại chính request schema của DTO. |
| Style | **Tailwind CSS v4**. Không viết file CSS riêng, không CSS-in-JS. |
| Icon | **lucide-react**. |

---

## 2. Kiến trúc thư mục

```
src/
├── config/env.ts            Đọc + validate biến môi trường (nơi DUY NHẤT chạm import.meta.env)
├── api/
│   ├── endpoints.ts         Hằng số đường dẫn API
│   ├── api-response.ts      Schema envelope ApiResponse / ErrorResponse
│   ├── api-error.ts         Class ApiError + chuẩn hoá mọi shape lỗi
│   ├── token-storage.ts     Nơi DUY NHẤT lưu/đọc token
│   ├── auth-events.ts       Sự kiện hết phiên
│   ├── axios-client.ts      Instance axios + interceptor + refresh single-flight
│   └── request.ts           postJson(): validate request, gọi API, validate response
├── dto/                     zod schema mirror DTO backend (nguồn sự thật của kiểu)
├── services/                Hàm gọi API thuần, không dính React
├── hooks/                   TanStack Query hooks + query-keys
├── features/auth/           auth-store (useSyncExternalStore) + useAuth
├── components/
│   ├── ui/                  shadcn sinh ra — KHÔNG tự viết vào đây
│   ├── layout/              layout tự viết
│   └── *.tsx                component tự viết
├── lib/                     tiện ích thuần (cn, jwt, browser-storage, query-client)
├── routes/                  TanStack Router file-based
└── styles/globals.css       Tailwind + token shadcn
```

### Quy ước đặt tên
- File: `kebab-case.ts` / `kebab-case.tsx`.
- Hook: `use-<tên>.ts`, export hàm `use<Tên>`.
- Service: `<domain>.service.ts`, export object `<domain>Service`.
- DTO: `<hành-động>.ts` trong `dto/<domain>/`, export `<x>RequestSchema`, `<x>ResponseSchema`, và type cùng tên PascalCase.
- Import nội bộ luôn dùng alias `@/`, không dùng `../..`.

---

## 3. Sự thật về backend (đã đọc source `../EcommerceMicroservices`, không phải suy đoán)

### 3.1 Chỉ có MỘT base URL: API Gateway `http://localhost:8085`

| Path | Service | Quyền yêu cầu |
|---|---|---|
| `/api/v1/auth/**` | auth-service | công khai (`permitAll`) |
| `/api/v1/products/**` | product-service | `ROLE_USER` hoặc `ROLE_ADMIN` |
| `/api/v1/categories/**` | product-service | chỉ cần đã đăng nhập |
| `/api/v1/orders/**` | order-service | `ROLE_USER` |

Gateway **không có filter StripPrefix** — path giữ nguyên khi forward.

### 3.2 Toàn bộ API — 6 endpoint, TẤT CẢ đều là POST

| Endpoint | Request | `data` trả về |
|---|---|---|
| `POST /api/v1/auth` | `{username, password}` | `{access_token, expires_in, refresh_expires_in, refresh_token}` |
| `POST /api/v1/auth/register` | `{username, first_name, last_name, email, password, role}` | `string` |
| `POST /api/v1/auth/refresh` | `{refresh_token}` | giống login — **CHƯA TỒN TẠI, xem mục 5** |
| `POST /api/v1/products/create` | `{name, price, stock, category_id}` | `{name}` |
| `POST /api/v1/products/get-by-ids` | `{product_ids: string[]}` | `Array<{id, name, price, stock, category_id, is_deleted}>` |
| `POST /api/v1/categories/create` | `{name, parent_id}` | `{name, parent_id}` |
| `POST /api/v1/orders/create` | `{customer_id, order_items:[{product_id, quantity}]}` | `{status, total_amount}` |

Enum duy nhất: `OrderStatus = PENDING | PREPARED | SHIPPED | DELIVERED | CANCELED` (chú ý `CANCELED` một chữ L).

### 3.3 Envelope

Thành công — `ApiResponse<T>`:
```json
{ "status_code": 200, "message": "OK", "data": {} }
```

Lỗi — `ErrorResponse`, **shape khác hẳn, KHÔNG bọc trong ApiResponse**:
```json
{ "status_code": 404, "error": "Not Found", "message": "...", "path": "...", "timestamp": "..." }
```

### 3.4 Cạm bẫy phải nhớ

1. **Login là `POST /api/v1/auth`** — không có `/login`, **không có dấu `/` cuối** (Spring 6 bỏ trailing-slash matching → `/api/v1/auth/` sẽ 404).
2. **Register trả HTTP 200 nhưng body ghi `status_code: 201`**. Không được suy ra thành công từ `status_code` trong body.
3. **Có 4 shape lỗi khác nhau**, `src/api/api-error.ts` xử lý cả 4 — đừng parse lỗi ở nơi khác:
   - `ErrorResponse` (từ `GlobalExceptionHandler`);
   - body mặc định của Spring cho lỗi `@Valid` (key là `status`, **không phải** `status_code` — backend thiếu handler cho `MethodArgumentNotValidException`);
   - RFC-7807 ProblemDetail;
   - **body rỗng** cho 401/403 từ gateway (gateway không có exception handler).
4. **CORS gateway khoá cứng origin `http://localhost:5173`**, chỉ cho phép header `Authorization` + `Content-Type`, `allowCredentials = false`.
   → Dev server **bắt buộc** chạy cổng 5173 (`vite.config.ts` đặt `strictPort: true` để fail to chứ không âm thầm nhảy 5174).
   → **Không được thêm bất kỳ header tuỳ ý nào** (`X-Request-Id`, `X-Correlation-Id`...) nếu không preflight sẽ fail.
   → Không dùng cookie httpOnly được, nên token lưu ở `localStorage`.
5. **Không có `@JsonInclude`** → field null trả về `null` tường minh, không bị bỏ đi.
6. **Casing trên wire là HỖN HỢP**, từng field được `@JsonProperty` thủ công. **Tuyệt đối không** áp phép chuyển snake_case ↔ camelCase toàn cục. DTO phía client giữ **nguyên xi tên field như trên wire** (`category_id`, `access_token`, `is_deleted`).
7. **Không có GET / list / phân trang / update / delete ở bất kỳ đâu.** `get-by-ids` là POST nhưng bản chất là đọc → dùng `useQuery`. Không viết type phân trang.
8. Tạo product/category/order **không trả về `id`**.
9. Giá và tồn kho là số nguyên (`int`), không phải `BigDecimal`, không có trường currency.
10. `POST /orders/create` trả `PENDING` ngay, Kafka saga mới đổi sang `PREPARED`, nhưng **không có endpoint đọc lại trạng thái đơn** → không poll được.

---

## 4. Cách hoạt động của tầng API

### Luồng một lời gọi
```
component → hook (TanStack Query) → service → postJson() → apiClient (axios)
```

`postJson()` (`src/api/request.ts`) làm 4 việc, theo đúng thứ tự:
1. Validate request bằng `requestSchema` → sai thì ném `ApiError` kind `request-schema`, **không gửi đi**.
2. Gọi `apiClient.post`.
3. Validate envelope `{status_code, message, data}`.
4. Validate `data` bằng `responseSchema`, trả về `data` đã typed.

Tắt bước 4 bằng `VITE_VALIDATE_API_RESPONSE=false` khi backend đổi schema mà frontend chưa kịp cập nhật.

### Interceptor
- **Request**: gắn `Authorization: Bearer <token>`, bỏ qua nếu config có `skipAuth: true` (login/register/refresh).
- **Response gặp 401**:
  - Nếu `VITE_AUTH_REFRESH_ENABLED=true` → gọi refresh theo cơ chế **single-flight** (chỉ một request refresh chạy, các request khác xếp hàng chờ rồi replay), thành công thì lưu token mới và retry request gốc một lần (cờ `retriedAfterRefresh` chống lặp vô hạn).
  - Thất bại, hoặc refresh bị tắt, hoặc request vốn `skipAuth` → xoá token, phát sự kiện hết phiên → `__root.tsx` điều hướng về `/login?redirect=...`.
- Refresh dùng một axios instance **riêng, không interceptor** để không đệ quy.

### Xử lý lỗi trong UI
Mọi lỗi tới component đều là `ApiError`. Dùng `error.message` để hiển thị, `error.status` / `error.kind` để phân nhánh. Không tự đọc `error.response.data` ở component.

---

## 5. Việc backend còn thiếu / cần sửa

### 5.1 Endpoint refresh CHƯA TỒN TẠI (bắt buộc thêm)
Frontend đã viết sẵn và **bật mặc định**. Backend cần implement đúng hợp đồng:
```
POST /api/v1/auth/refresh
Body:     { "refresh_token": "<token>" }
Response: ApiResponse<LoginResponse>
          { "status_code": 200, "message": "OK",
            "data": { "access_token", "expires_in", "refresh_expires_in", "refresh_token" } }
```
Nằm trong `permitAll()` của `/api/v1/auth/**` (đã sẵn). **Trước khi có endpoint này, đặt `VITE_AUTH_REFRESH_ENABLED=false`** thì gặp 401 sẽ logout thẳng thay vì thử refresh.

Không được cho frontend gọi thẳng Keycloak để refresh: client `ecom` là confidential, làm vậy phải nhúng client secret vào trình duyệt.

### 5.2 Lỗi role nhiều khả năng chặn toàn bộ luồng
`GrantedAuthoritiesExtractor` của gateway đọc role từ claim `resource_access.ecom.roles` (**client role**), nhưng `UserServiceImpl.createUser` gán **realm-level role** → nằm ở `realm_access.roles`.
→ User đăng ký qua `/register` sẽ nhận **403** ở `/products` và `/orders` dù token hợp lệ.
→ Cách sửa: map role vào client `ecom` trong Keycloak, **hoặc** sửa extractor đọc `realm_access.roles`.
→ Trang demo có sẵn cảnh báo khi token không chứa role nào.

### 5.3 Khác
- `clientSecret` Keycloak đang commit plaintext trong `auth-service/src/main/resources/application.yaml` → cần xoay vòng và đưa ra biến môi trường.
- Không có realm export: realm `ecom-realm`, client `ecom`, role `USER`/`ADMIN` phải tạo tay ở `http://localhost:8086` (admin / admin_password).
- Chưa có endpoint logout, đọc đơn hàng, danh sách sản phẩm, danh sách danh mục.

---

## 6. Lệnh thường dùng

```bash
npm run dev        # dev server, BẮT BUỘC cổng 5173 (strictPort)
npm run build      # tsc -b && vite build
npm run lint       # oxlint
npx tsc -b --noEmit
npx shadcn@latest add <component>
```

### Kiểm tra trước khi coi là xong
```bash
npx tsc -b --noEmit                      # không lỗi
npm run build                            # build sạch
npm run lint                             # không warning
grep -rn "localhost\|http://" src --include=*.ts --include=*.tsx | grep -v routeTree.gen | grep -v components/ui/   # phải rỗng
grep -rn "^\s*//\|^\s*/\*" src --include=*.ts --include=*.tsx | grep -v routeTree.gen | grep -v components/ui/      # phải rỗng
grep -rn "import.meta.env" src           # chỉ được ra src/config/env.ts
```

### Thứ tự khởi động backend
`docker compose -f ../EcommerceMicroservices/infrastructure/docker-compose.yml up -d` → eureka (8761) → auth-service (8083) → product-service (8080) → order-service (8082) → gateway (8085).

---

## 7. Thói quen làm việc mà chủ dự án mong đợi

- Đọc code backend để lấy sự thật, **không suy đoán** shape API.
- Nói rõ khi phát hiện vấn đề ở backend thay vì âm thầm workaround.
- Ưu tiên giải pháp ít phụ thuộc: đã có `useSyncExternalStore` thì không cài thêm zustand; đã có TanStack Query thì không tự viết cache.
- Khi phải chọn giữa hai hướng có hệ quả khác nhau rõ rệt → hỏi trước, không tự quyết.
