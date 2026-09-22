# Marketplace Backend

Backend de un marketplace de productos (tipo mini Mercado Libre), desarrollado con Spring Boot como proyecto de práctica full-stack (Spring Boot + Angular).

## Stack técnico
- **Lenguaje:** Java 21
- **Framework:** Spring Boot 4
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de datos:** PostgreSQL (H2 en memoria para tests)
- **Seguridad:** Spring Security + JWT, autorización por roles
- **Validación:** Bean Validation (Jakarta Validation)
- **Correo:** Spring Mail (correos HTML de bienvenida y confirmación de compra)
- **Testing:** JUnit 5, Mockito (tests unitarios), Spring Boot Test + H2 (tests de integración)
- **Documentación:** Springdoc OpenAPI (Swagger UI)
- **Contenedores:** Docker, Docker Compose
- **Build:** Maven

## Arquitectura

Controller → Service → Repository → Entity


- **`entity/`** — 11 entidades JPA que modelan el dominio completo
- **`repository/`** — interfaces Spring Data JPA con consultas derivadas
- **`service/`** — lógica de negocio (validaciones, reglas, orquestación)
- **`controller/`** — endpoints REST
- **`dto/`** — objetos de transferencia con Bean Validation, para no exponer las entidades directamente
- **`security/`** — Spring Security, filtro JWT, detalles de usuario y roles
- **`config/`** — configuración de Swagger/OpenAPI
- **`exception/`** — excepciones personalizadas y manejador global de errores

## Modelo de datos

11 entidades: `Usuario`, `Direccion`, `Categoria` (con subcategorías autorreferenciadas), `Producto`, `ImagenProducto`, `Carrito`, `ItemCarrito`, `Orden`, `ItemOrden`, `Pago`, `Resena`.

**Decisiones de diseño relevantes:**
- `ItemOrden.precioUnitario` guarda el precio "congelado" al momento de la compra — nunca se recalcula desde `Producto.precio`.
- `Categoria` soporta jerarquía de subcategorías vía relación autorreferenciada.
- Roles: `USUARIO` (publica, compra, califica) y `ADMIN` (gestiona categorías), protegidos con `@PreAuthorize`.
- El checkout (`OrdenService.checkout`) es transaccional (`@Transactional`): valida stock, congela precios, simula el pago, descuenta stock y vacía el carrito — todo o nada.
- Solo puede reseñar un producto quien lo haya comprado (validado contra `ItemOrden`), y solo una vez por producto.

## Manejo de errores

Excepciones personalizadas mapeadas a códigos HTTP semánticos vía `GlobalExceptionHandler`:

| Excepción | Código HTTP | Caso de uso |
|-----------|-------------|-------------|
| `RecursoNoEncontradoException` | 404 | Usuario, producto, categoría, dirección, orden no encontrados |
| `AccesoNoAutorizadoException` | 403 | Usar dirección/item/reseña que no te pertenece |
| `StockInsuficienteException` | 409 | Comprar más cantidad de la disponible |
| `RecursoDuplicadoException` | 409 | Email ya registrado, reseña duplicada |
| `MethodArgumentNotValidException` | 400 | Falla de validación en un DTO (`@Valid`), con detalle por campo |

## Testing

- **Unitarios** (`AuthServiceTest`, `OrdenServiceTest`) con JUnit 5 + Mockito: simulan todas las dependencias para probar la lógica de negocio de forma aislada.
- **Integración** (`OrdenServiceIntegrationTest`) con `@SpringBootTest` + base de datos H2 en memoria: prueba el flujo completo de checkout contra una base real, sin mocks de repositorios.

Correr los tests:
```bash
mvn test
```

## Endpoints disponibles

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

### Productos (`/api/productos`)
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/?pagina=&tamano=` | No | Catálogo paginado, con promedio de calificación |
| GET | `/{id}` | No | Detalle de un producto |
| POST | `/` | Sí | Publica un producto |
| GET | `/mis-productos` | Sí | Productos del usuario autenticado |

### Carrito (`/api/carrito`) — todos requieren autenticación
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/` | Carrito del usuario (se crea si no existe) |
| POST | `/items` | Agrega producto (valida stock, suma cantidad si ya existe) |
| DELETE | `/items/{itemId}` | Quita un item del carrito |

### Direcciones (`/api/direcciones`) — todos requieren autenticación
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/` | Registra una dirección de envío |
| GET | `/` | Lista las direcciones del usuario |

### Órdenes (`/api/ordenes`) — todos requieren autenticación
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/checkout` | Convierte el carrito en orden: valida, cobra, envía correo de confirmación |
| GET | `/` | Historial de compras, con dirección de envío incluida |

### Reseñas (`/api/resenas`)
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/` | Sí | Reseña un producto comprado (1-5, sin duplicados) |
| GET | `/producto/{productoId}` | No | Reseñas de un producto |

## Estado del proyecto

✅ Modelo de datos completo · ✅ Auth JWT + roles · ✅ CRUD completo (Categorías, Productos, Carrito, Direcciones) · ✅ Checkout transaccional · ✅ Reseñas · ✅ Correos reales · ✅ Validaciones robustas · ✅ Excepciones personalizadas · ✅ Tests unitarios e integración · ✅ Dockerizado · ✅ Swagger documentado

⏳ Pendiente: Frontend Angular · Migraciones con Flyway · CI/CD · Despliegue

## Cómo correrlo con Docker (recomendado)

1. Clona el repo
2. Crea un archivo `.env` en la raíz con:

   DB_PASSWORD=una_password_para_desarrollo
   JWT_SECRET=una_clave_larga_y_secreta
   MAIL_USERNAME=tu_correo@gmail.com
   MAIL_PASSWORD=tu_contraseña_de_aplicacion_de_gmail

3. Corre:
```bash
   docker-compose up --build
```
4. Documentación interactiva: `http://localhost:8080/swagger-ui/index.html`

Esto levanta automáticamente el backend y una base PostgreSQL en contenedores separados, conectados entre sí.

## Cómo correrlo localmente (sin Docker)

1. Clona el repo
2. Crea una base de datos PostgreSQL
3. Configura las siguientes variables de entorno (en tu IDE o sistema):

   DB_PASSWORD=tu_password_de_postgres
   JWT_SECRET=una_clave_larga_y_secreta
   MAIL_USERNAME=tu_correo@gmail.com
   MAIL_PASSWORD=tu_contraseña_de_aplicacion_de_gmail

4. Ajusta `spring.datasource.url` en `application.properties` si tu puerto de Postgres no es el estándar
5. Corre con `mvn spring-boot:run`
6. Documentación interactiva: `http://localhost:8080/swagger-ui/index.html`
   - Autentícate con `/api/auth/login`, copia el token, y úsalo en **Authorize** 🔒

## Autor

Orlando Díaz — [GitHub](https://github.com/Orlando-Diaz)