# Marketplace Backend

API REST de un marketplace de productos (tipo mini Mercado Libre), desarrollada con Spring Boot como parte de un proyecto full-stack (Spring Boot + Angular). Los usuarios pueden publicar productos, comprar, pagar (pago simulado), calificar sus compras y administrar sus publicaciones.

Frontend: [marketplace-frontend](https://github.com/Orlando-Diaz/marketplace-frontend)

## 🚀 Probar la app

**Demo en vivo:** https://marketplace-orlandodiaz.vercel.app
**Documentación de la API (Swagger):** https://marketplace-backend-27bf.onrender.com/swagger-ui/index.html

> ⏳ El backend está en un plan gratuito que se suspende tras 15 minutos sin uso. Si la app tarda en cargar, espera 1-2 minutos mientras el servidor despierta; después responde con normalidad.

Puedes explorar la app sin registrarte, usando las cuentas de demostración (también hay botones de acceso rápido en la pantalla de login):

| Rol | Correo | Contraseña | Qué puedes probar |
|-----|--------|------------|-------------------|
| Comprador / vendedor | `demo@marketplace.com` | `demo1234` | Carrito, checkout, historial de compras, reseñas, publicar, editar y pausar productos |
| Administrador | `admin@marketplace.com` | `admin1234` | Todo lo anterior + panel de administración de categorías |

> Las reglas de negocio del backend protegen la integridad de los datos: por ejemplo, no se puede eliminar una categoría que tenga productos o subcategorías.

## Stack técnico

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 4
- **Persistencia:** Spring Data JPA / Hibernate, JPA Specifications para búsquedas dinámicas
- **Base de datos:** PostgreSQL (H2 en memoria para tests)
- **Seguridad:** Spring Security + JWT, autorización por roles con `@PreAuthorize`
- **Validación:** Bean Validation (Jakarta Validation)
- **Correo:** Spring Mail con envío asíncrono (`@Async`) y tolerante a fallos
- **Testing:** JUnit 5, Mockito (tests unitarios), Spring Boot Test + H2 (tests de integración)
- **Documentación:** Springdoc OpenAPI (Swagger UI)
- **Contenedores:** Docker (build multi-stage), Docker Compose
- **Despliegue:** Render (backend en Docker), Neon (PostgreSQL), Vercel (frontend)
- **Build:** Maven

## Arquitectura

```
Controller → Service → Repository → Entity
```

- **`entity/`** — 11 entidades JPA que modelan el dominio completo
- **`repository/`** — interfaces Spring Data JPA con consultas derivadas y Specifications
- **`service/`** — lógica de negocio (validaciones, reglas, orquestación)
- **`controller/`** — endpoints REST
- **`dto/`** — objetos de transferencia con Bean Validation, para no exponer las entidades directamente
- **`security/`** — Spring Security, filtro JWT, CORS configurable, detalles de usuario y roles
- **`config/`** — configuración de Swagger/OpenAPI
- **`exception/`** — excepciones personalizadas y manejador global de errores

## Modelo de datos

11 entidades: `Usuario`, `Direccion`, `Categoria` (con subcategorías autorreferenciadas), `Producto`, `ImagenProducto`, `Carrito`, `ItemCarrito`, `Orden`, `ItemOrden`, `Pago`, `Resena`.

### Decisiones de diseño relevantes

- **Precio congelado:** `ItemOrden.precioUnitario` guarda el precio al momento de la compra; nunca se recalcula desde `Producto.precio`, así el historial no cambia si el vendedor modifica el precio.
- **Checkout transaccional:** `OrdenService.checkout` usa `@Transactional`: valida el stock de todos los items, congela precios, simula el pago, descuenta stock y vacía el carrito. Si algo falla, no se aplica nada.
- **Reseñas verificadas:** solo puede reseñar un producto quien lo haya comprado (validado contra `ItemOrden`), y una sola vez por producto.
- **Productos que no se borran, se pausan:** un producto vendido está referenciado por órdenes; eliminarlo rompería el historial de compras de otros usuarios. Pausarlo (`INACTIVO`) lo saca del catálogo sin perder datos.
- **Categorías en dos niveles:** categoría → subcategoría. Evita jerarquías profundas y ciclos; no se puede eliminar una categoría con productos o subcategorías.
- **Búsqueda con JPA Specifications:** cada filtro del catálogo (texto, categoría, rango de precio, stock) es una condición independiente y solo se combinan las que el usuario activa, en vez de escribir un método de repositorio por cada combinación.
- **Correos asíncronos y tolerantes a fallos:** el envío de correos corre en segundo plano y, si el servidor de correo falla, se registra en el log sin afectar el registro ni la compra.
- **Solo el dueño modifica:** editar, pausar un producto o tocar items del carrito y direcciones valida que el recurso pertenezca al usuario autenticado.

## Manejo de errores

Excepciones personalizadas mapeadas a códigos HTTP semánticos vía `GlobalExceptionHandler`:

| Excepción | Código HTTP | Caso de uso |
|-----------|-------------|-------------|
| `RecursoNoEncontradoException` | 404 | Usuario, producto, categoría, dirección u orden no encontrados |
| `AccesoNoAutorizadoException` | 403 | Modificar un producto, dirección o item que no te pertenece; reseñar sin haber comprado |
| `AccessDeniedException` | 403 | Acción reservada a ADMIN realizada por otro rol |
| `StockInsuficienteException` | 409 | Comprar o agregar al carrito más cantidad de la disponible |
| `RecursoDuplicadoException` | 409 | Email ya registrado, reseña duplicada |
| `OperacionNoPermitidaException` | 409 | Eliminar una categoría con productos o subcategorías, jerarquías inválidas |
| `MethodArgumentNotValidException` | 400 | Falla de validación en un DTO (`@Valid`), con detalle por campo |

## Testing

- **Unitarios** (`AuthServiceTest`, `OrdenServiceTest`) con JUnit 5 + Mockito: simulan todas las dependencias para probar la lógica de negocio de forma aislada (stock insuficiente, direcciones ajenas, carrito vacío, etc.).
- **Integración** (`OrdenServiceIntegrationTest`) con `@SpringBootTest` + H2 en memoria: prueba el flujo completo de checkout contra una base real, sin mocks de repositorios.

```bash
mvn test
```

## Endpoints

### Auth (`/api/auth`) — públicos

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/registro` | Registra un usuario, envía correo de bienvenida y devuelve JWT |
| POST | `/login` | Autentica y devuelve JWT |

### Categorías (`/api/categorias`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/` | No | Lista todas las categorías |
| GET | `/raiz` | No | Lista solo categorías principales |
| POST | `/` | ADMIN | Crea una categoría o subcategoría |
| PUT | `/{id}` | ADMIN | Edita nombre, descripción y categoría padre |
| DELETE | `/{id}` | ADMIN | Elimina una categoría sin productos ni subcategorías |

### Productos (`/api/productos`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/` | No | Catálogo paginado con filtros combinables y orden (ver abajo) |
| GET | `/{id}` | No | Detalle de un producto, con imágenes y calificación promedio |
| POST | `/` | Sí | Publica un producto (hasta 5 imágenes) |
| GET | `/mis-productos` | Sí | Productos del usuario autenticado |
| PUT | `/{id}` | Dueño | Edita un producto propio, incluidas sus imágenes |
| PATCH | `/{id}/estado?activo=` | Dueño | Pausa o reactiva un producto sin borrarlo |

Parámetros opcionales del catálogo: `q` (texto), `categoriaId` (incluye subcategorías), `precioMin`, `precioMax`, `soloDisponibles`, `orden` (`recientes`, `precio_asc`, `precio_desc`), `pagina`, `tamano`.

### Carrito (`/api/carrito`) — requieren autenticación

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/` | Carrito del usuario (se crea si no existe) |
| POST | `/items` | Agrega un producto (valida stock, suma cantidad si ya existe) |
| PUT | `/items/{itemId}` | Cambia la cantidad de un item (valida stock y dueño) |
| DELETE | `/items/{itemId}` | Quita un item del carrito |

### Direcciones (`/api/direcciones`) — requieren autenticación

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/` | Registra una dirección de envío |
| GET | `/` | Lista las direcciones del usuario |

### Órdenes (`/api/ordenes`) — requieren autenticación

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/checkout` | Convierte el carrito en orden: valida stock, simula el pago y envía correo de confirmación |
| GET | `/` | Historial de compras, con dirección de envío incluida |

### Reseñas (`/api/resenas`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/` | Sí | Reseña un producto comprado (1-5 estrellas, sin duplicados) |
| GET | `/producto/{productoId}` | No | Reseñas de un producto |

## Estado del proyecto

✅ Modelo de datos completo · ✅ Auth JWT + roles · ✅ CRUD completo · ✅ Búsqueda con filtros · ✅ Checkout transaccional · ✅ Reseñas verificadas · ✅ Panel de administración · ✅ Correos asíncronos · ✅ Validaciones · ✅ Excepciones personalizadas · ✅ Tests unitarios e integración · ✅ Dockerizado · ✅ Swagger · ✅ Desplegado en la nube

⏳ Mejoras futuras: migraciones con Flyway · CI/CD con GitHub Actions · correos en producción mediante una API HTTPS (el plan gratuito de Render bloquea SMTP)

## Cómo correrlo con Docker (recomendado)

1. Clona el repo.
2. Crea un archivo `.env` en la raíz (está en `.gitignore`, nunca se sube):

```env
DB_PASSWORD=una_password_para_desarrollo
JWT_SECRET=una_clave_larga_y_secreta_de_al_menos_32_caracteres
MAIL_USERNAME=tu_correo@gmail.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion_de_gmail
```

3. Levanta todo:

```bash
docker-compose up --build
```

4. Swagger: `http://localhost:8080/swagger-ui/index.html`

Esto levanta el backend y una base PostgreSQL en contenedores separados, conectados entre sí.

## Cómo correrlo localmente (sin Docker)

1. Clona el repo y crea una base de datos PostgreSQL.
2. Configura estas variables de entorno (en tu IDE o sistema):

```env
DB_PASSWORD=tu_password_de_postgres
JWT_SECRET=una_clave_larga_y_secreta_de_al_menos_32_caracteres
MAIL_USERNAME=tu_correo@gmail.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion_de_gmail
```

3. Ajusta `spring.datasource.url` en `application.properties` si tu Postgres no usa el puerto estándar.
4. Corre con `mvn spring-boot:run`.
5. Swagger: `http://localhost:8080/swagger-ui/index.html` — autentícate con `/api/auth/login`, copia el token y úsalo en **Authorize** 🔒.

## Despliegue (perfil `prod`)

En producción se activa el perfil `prod` (`SPRING_PROFILES_ACTIVE=prod`), que toma toda la configuración de variables de entorno:

| Variable | Descripción |
|----------|-------------|
| `DB_URL` | URL JDBC de PostgreSQL (con `sslmode=require`) |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales de la base |
| `JWT_SECRET` | Clave para firmar los tokens |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Cuenta de correo para notificaciones |
| `CORS_ALLOWED_ORIGINS` | Dominio(s) del frontend, separados por coma |
| `PORT` | Puerto asignado por la plataforma (por defecto 8080) |

## Autor

**Orlando Díaz** — Ingeniero de Sistemas y Computación · [GitHub](https://github.com/Orlando-Diaz)