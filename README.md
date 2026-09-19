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

Arquitectura en capas clásica:


- **`entity/`** — 11 entidades JPA que modelan el dominio completo
- **`repository/`** — interfaces Spring Data JPA con consultas derivadas
- **`service/`** — lógica de negocio (validaciones, reglas, orquestación)
- **`controller/`** — endpoints REST
- **`dto/`** — objetos de transferencia, para no exponer las entidades directamente
- **`security/`** — configuración de Spring Security, filtro JWT y detalles de usuario
- **`config/`** — configuración de Swagger/OpenAPI

## Modelo de datos

11 entidades: `Usuario`, `Direccion`, `Categoria` (con subcategorías autorreferenciadas), `Producto`, `ImagenProducto`, `Carrito`, `ItemCarrito`, `Orden`, `ItemOrden`, `Pago`, `Resena`.

**Decisiones de diseño relevantes:**
- `ItemOrden.precioUnitario` guarda el precio "congelado" al momento de la compra — nunca se recalcula desde `Producto.precio`, para que las órdenes pasadas reflejen el precio real que pagó el comprador.
- `Categoria` soporta jerarquía de subcategorías vía relación autorreferenciada (`categoriaPadre`).
- Roles simplificados a `USUARIO` (publica, compra, califica) y `ADMIN` (modera), sin separación rígida comprador/vendedor.

## Endpoints disponibles

### Auth (`/api/auth`) — públicos
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/registro` | Registra un nuevo usuario y devuelve JWT |
| POST | `/login` | Autentica y devuelve JWT |

### Categorías (`/api/categorias`)
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/` | No | Lista todas las categorías |
| GET | `/raiz` | No | Lista solo categorías principales (sin padre) |
| POST | `/` | Sí | Crea una categoría (o subcategoría) |

### Productos (`/api/productos`)
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/` | No | Lista el catálogo (productos disponibles) |
| GET | `/{id}` | No | Detalle de un producto |
| POST | `/` | Sí | Publica un producto nuevo |
| GET | `/mis-productos` | Sí | Productos publicados por el usuario autenticado |

## Estado del proyecto

✅ Modelo de datos completo · ✅ Autenticación JWT · ✅ CRUD Categorías · ✅ CRUD Productos · ✅ Swagger documentado

⏳ Pendiente: Carrito · Órdenes/Checkout · Pago simulado · Reseñas · Frontend Angular

## Cómo correrlo localmente

1. Clona el repo
2. Crea una base de datos PostgreSQL llamada `marketplace_db`
3. Configura `src/main/resources/application.properties`:
```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/marketplace_db
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_password
```
4. Corre con `mvn spring-boot:run`
5. Documentación interactiva: `http://localhost:8080/swagger-ui/index.html`
    - Autentícate primero con `/api/auth/login`, copia el token, y úsalo en el botón **Authorize** 🔒

## Autor

Orlando Díaz — [GitHub](https://github.com/Orlando-Diaz)