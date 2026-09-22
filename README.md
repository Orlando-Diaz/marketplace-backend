# Marketplace Backend

Backend de un marketplace de productos (tipo mini Mercado Libre), desarrollado con Spring Boot como proyecto de práctica full-stack (Spring Boot + Angular).

## Stack técnico
- **Lenguaje:** Java 21
- **Framework:** Spring Boot 4
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de datos:** PostgreSQL
- **Seguridad:** Spring Security + JWT (JJWT)
- **Documentación:** Springdoc OpenAPI (Swagger UI)
- **Build:** Maven

## Arquitectura


- **`entity/`** — 11 entidades JPA que modelan el dominio completo
- **`repository/`** — interfaces Spring Data JPA con consultas derivadas
- **`service/`** — lógica de negocio (validaciones, reglas, orquestación)
- **`controller/`** — endpoints REST
- **`dto/`** — objetos de transferencia con Bean Validation, para no exponer las entidades directamente
- **`security/`** — Spring Security, filtro JWT, detalles de usuario y roles
- **`config/`** — configuración de Swagger/OpenAPI
- **`exception/`** — manejador global de excepciones (errores de negocio y de validación)

## Modelo de datos

11 entidades: `Usuario`, `Direccion`, `Categoria` (con subcategorías autorreferenciadas), `Producto`, `ImagenProducto`, `Carrito`, `ItemCarrito`, `Orden`, `ItemOrden`, `Pago`, `Resena`.

**Decisiones de diseño relevantes:**
- `ItemOrden.precioUnitario` guarda el precio "congelado" al momento de la compra — nunca se recalcula desde `Producto.precio`.
- `Categoria` soporta jerarquía de subcategorías vía relación autorreferenciada.
- Roles: `USUARIO` (publica, compra, califica) y `ADMIN` (gestiona categorías), protegidos con `@PreAuthorize`.
- El checkout (`OrdenService.checkout`) es transaccional (`@Transactional`): valida stock, congela precios, simula el pago, descuenta stock y vacía el carrito — todo o nada.
- Solo puede reseñar un producto quien lo haya comprado (validado contra `ItemOrden`), y solo una vez por producto.

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

✅ Modelo de datos completo · ✅ Auth JWT + roles · ✅ CRUD completo (Categorías, Productos, Carrito, Direcciones) · ✅ Checkout transaccional · ✅ Reseñas · ✅ Correos reales · ✅ Validaciones robustas · ✅ Swagger documentado

⏳ Pendiente: Frontend Angular · Despliegue

## Cómo correrlo localmente

1. Clona el repo
2. Crea una base de datos PostgreSQL
3. Configura las siguientes variables de entorno (en tu IDE o `.env`):

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