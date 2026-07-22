# Prácticas Spring Boot - fundamentos01

Proyecto de la materia Programación y Plataformas Web (UPS).

## Datos del proyecto

- Group: `ec.edu.ups.icc`
- Artifact: `fundamentos01`
- Java 25
- Gradle (Groovy)
- Spring Boot 4.1.0
- Dependencias:
  - Spring Web
  - Spring Boot DevTools

## Ejecutar el proyecto

```bash
./gradlew bootRun
```

La aplicación inicia en el puerto **8080**.

En `application.yml` se configuró la ruta base `/api`.

---

# Práctica 01 - Configuración

## Objetivo

Configurar el proyecto y comprobar que spring funciona correctamente.

### Endpoint

```text
GET http://localhost:8080/api/api/status
```

Respuesta:

```json
{
  "service": "Spring Boot API",
  "status": "running",
  "timestamp": "2026-06-19T02:43:22.606358Z"
}
```

> La ruta queda como `/api/api/status` porque se usa `context-path: /api` y el controller también tiene `/api/status`.

---

# Práctica 02 - Estructura del proyecto

## Objetivo

Organizar el proyecto por paquetes y crear el módulo **students**.

### Endpoints

```text
GET /api/students
GET /api/students/count
```

Respuesta:

```json
[
  {
    "id": 2,
    "name": "Juan",
    "age": "30"
  },
  {
    "id": 1,
    "name": "Diego",
    "age": "10"
  }
]
```

---

# Práctica 03 - API REST

## Objetivo

Crear un CRUD para **users** y **products** usando dto, model y mapper. En esta práctica los datos todavía se guardan en memoria.

## Endpoints

### Users

| Método | Ruta |
|---------|------|
| GET | `/api/users` |
| GET | `/api/users/{id}` |
| POST | `/api/users` |
| PUT | `/api/users/{id}` |
| PATCH | `/api/users/{id}` |
| DELETE | `/api/users/{id}` |

Para **products** son los mismos endpoints cambiando `/users` por `/products`.

## Crear usuario

```http
POST /api/users
```

```json
{
  "name": "Ana",
  "email": "ana@ups.edu.ec",
  "password": "1234"
}
```

Respuesta:

```json
{
  "id": 1,
  "name": "Ana",
  "email": "ana@ups.edu.ec"
}
```

Si el usuario no existe:

```json
{
  "message": "Usuario no encontrado con id 99"
}
```

Ejemplo de producto:

```json
{
  "id": 1,
  "name": "Teclado",
  "price": 25.5,
  "stock": 10
}
```

## Estructura

```text
users/
├── controllers/UserController.java
├── dtos/
├── models/UserModel.java
└── mappers/UserMapper.java
```

`products` tiene la misma estructura.

---

# Práctica 04 - Controladores y servicios

## Objetivo

Separar la lógica del controller y moverla a la capa de servicios.

Ahora el controller solo recibe la petición y llama al servicio.

## Ejemplo

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAll();
    }
}
```

## Nueva estructura

```text
users/
├── controllers/
├── services/
│   ├── UserService.java
│   └── UserServiceImpl.java
├── dtos/
├── models/
└── mappers/
```

`products` utiliza la misma estructura.

Los endpoints siguen siendo los mismos de la práctica anterior.

# Práctica 05 - Persistencia con PostgreSQL y JPA

## Objetivo

Guardar la información en **postgresql** usando **spring data jpa**.

## Configuración

- Se creó un contenedor de docker con PostgreSQL.
- Se configuró la conexión en `application.yml`.
- Se agregaron las dependencias de JPA y PostgreSQL.

## Archivos nuevos

```text
core/
└── entities/BaseEntity.java

users/
├── entities/UserEntity.java
└── repositories/UserRepository.java
```

El `UserMapper` también convierte entre **Model** y **Entity**.

## Cambios en el servicio

Ahora el servicio usa `UserRepository` en lugar de una lista en memoria.

```java
private final UserRepository userRepository;

public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

Los métodos utilizan `save()`, `findById()` y `findAll()`.

El borrado es lógico usando el campo `deleted`.

## Verificación

```bash
docker exec -it postgres-dev psql -U ups -d devdb -c "SELECT * FROM users;"
```

---

# Práctica 06 - Validación de DTOs

## Objetivo

Validar los datos antes de guardarlos.

## Dependencia

Se agregó:

```text
spring-boot-starter-validation
```

## Validaciones

Ejemplo en los dto:

```java
@NotBlank(message = "El nombre es obligatorio")
@Size(min = 3, max = 150)
private String name;

@NotBlank(message = "El email es obligatorio")
@Email(message = "Debe ingresar un email válido")
private String email;
```

Para productos también se agregaron validaciones como:

```java
@Min(0)
private Double price;

@Min(0)
private Integer stock;
```

## Activar la validación

```java
@PostMapping
public UserResponseDto create(@Valid @RequestBody CreateUserDto dto) {
    return userService.create(dto);
}
```

## Validaciones en el servicio

También se valida que el correo no exista.

```java
if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
    throw new ConflictException("Email already registered");
}
```

En products también se valida que no exista otro producto con el mismo nombre.

---

# Práctica 07 - Manejo global de errores

## Objetivo

Responder errores con un formato único.

## Estructura

```text
core/exceptions/
├── base/ApplicationException.java
├── domain/
│   ├── NotFoundException.java
│   ├── ConflictException.java
│   └── BadRequestException.java
├── response/ErrorResponse.java
└── handler/GlobalExceptionHandler.java
```

## Handler

```java
@ExceptionHandler(ApplicationException.class)
public ResponseEntity<ErrorResponse> handleApplicationException(
        ApplicationException ex,
        HttpServletRequest request) {

    return ResponseEntity.status(ex.getStatus())
            .body(new ErrorResponse(
                    ex.getStatus(),
                    ex.getMessage(),
                    request.getRequestURI()));
}
```

También se agregó manejo para:

- `MethodArgumentNotValidException`
- `Exception`

## Cambios

En los servicios se reemplazó:

```java
throw new IllegalStateException(...)
```

por

```java
.orElseThrow(() -> new NotFoundException("User not found"));
```

Los registros con `deleted = true` ya no se muestran.

## Pruebas

```bash
# Error de validación
curl -X POST localhost:8080/api/products \
-H "Content-Type: application/json" \
-d '{"name":"","price":-5,"stock":-1}'
```

```bash
# Producto repetido
curl -X POST localhost:8080/api/products \
-H "Content-Type: application/json" \
-d '{"name":"laptop","price":800,"stock":5}'
```

```bash
# Producto no encontrado
curl localhost:8080/api/products/999
```

---

# Práctica 08 - Relaciones entre entidades

## Objetivo

Relacionar **products**, **users** y **categories**.

## Nuevo módulo

```text
categories/
├── controllers/
├── dtos/
├── entities/
├── models/
├── mappers/
├── repositories/
└── services/
```

### Endpoints

```text
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

## Relaciones

```java
@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private UserEntity owner;

@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
private CategoryEntity category;
```

## DTO

Ahora el producto recibe los ids de usuario y categoría.

```text
userId
categoryId
```

La respuesta devuelve la información relacionada.

```json
{
  "id": 9,
  "name": "Laptop Gaming 08",
  "price": 1200,
  "stock": 10,
  "owner": {
    "id": 7,
    "name": "Juan Perez"
  },
  "category": {
    "id": 1,
    "name": "Electronicos"
  }
}
```

## Validación

Antes de guardar un producto se verifica que el usuario y la categoría existan.

```java
UserEntity owner = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new NotFoundException("User not found"));

CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new NotFoundException("Category not found"));
```

## Consultas

Se agregaron estos métodos al repositorio.

```java
List<ProductEntity> findByOwner_IdAndDeletedFalse(Long ownerId);

List<ProductEntity> findByCategory_IdAndDeletedFalse(Long categoryId);
```

También se agregaron dos endpoints.

```text
GET /api/products/user/{userId}

GET /api/products/category/{categoryId}
```

## Verificación

```bash
docker exec -it postgres-dev psql -U ups -d devdb -c "\d products"
```

```sql
SELECT p.id,
       p.name,
       p.user_id,
       u.name,
       p.category_id,
       c.name
FROM products p
INNER JOIN users u ON p.user_id = u.id
INNER JOIN categories c ON p.category_id = c.id;
```
# Práctica 09 - Filtros con query params

## Objetivo

Agregar filtros para consultar los productos de un usuario.

## Endpoint

```text
GET /api/users/1/products
GET /api/users/1/products?name=laptop
GET /api/users/1/products?minPrice=400&maxPrice=700
```

Los filtros son opcionales.

## Controller

```java
@GetMapping("/{id}/products")
public List<ProductResponseDto> findProductsByUser(
        @PathVariable Long id,
        @Valid @ModelAttribute ProductFilterDto filters
) {
    return userService.findProductsByUser(id, filters);
}
```

## Consulta

```java
@Query("""
SELECT p FROM ProductEntity p
WHERE p.deleted = false
AND p.owner.id = :userId
AND (COALESCE(:name,'') = '' OR LOWER(p.name)
LIKE LOWER(CONCAT('%',COALESCE(:name,''),'%')))
AND (:minPrice IS NULL OR p.price >= :minPrice)
AND (:maxPrice IS NULL OR p.price <= :maxPrice)
""")
```

---

# Práctica 10 - Paginación

## Objetivo

Agregar paginación usando **Page** y **Slice**.

## Endpoints

```text
GET /api/products/page
GET /api/products/slice
```

Parámetros disponibles:

| Parámetro | Valor por defecto |
|-----------|-------------------|
| page | 0 |
| size | 10 |
| sortBy | id |
| direction | asc |

Ejemplos:

```text
GET /api/products/page?page=0&size=5&sortBy=price&direction=desc

GET /api/products/slice?page=0&size=5&sortBy=createdAt&direction=desc
```

## DTO

Se creó:

```text
core/dtos/PaginationDto.java
```

## Repository

```java
@Query(
value = "SELECT p FROM ProductEntity p WHERE p.deleted = false",
countQuery = "SELECT COUNT(p) FROM ProductEntity p WHERE p.deleted = false"
)
Page<ProductEntity> findActivePage(Pageable pageable);

@Query("SELECT p FROM ProductEntity p WHERE p.deleted = false")
Slice<ProductEntity> findActiveSlice(Pageable pageable);
```

## Datos de prueba

```bash
docker exec -i postgres-dev psql -U ups -d devdb < seed_data.sql
```

## Resultado con Page

```json
{
  "number": 0,
  "size": 3,
  "totalElements": 20300,
  "totalPages": 6767,
  "first": true,
  "last": false
}
```

![Respuesta con Page](assets/10-page.png)

## Resultado con Slice

```json
{
  "number": 0,
  "size": 3,
  "first": true,
  "last": false
}
```

![Respuesta con Slice](assets/10-slice.png)

## Error de validación

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada inválidos",
  "details": {
    "page": "La página debe ser mayor o igual a 0",
    "size": "El tamaño debe ser mayor o igual a 1"
  }
}
```

![Error de paginación](assets/10-page-invalid.png)

## Categorías paginadas

Se agregaron estos endpoints.

```text
GET /api/categories/{id}/products

GET /api/categories/{id}/products/page

GET /api/categories/{id}/products/slice
```

Ejemplo:

```text
GET /api/categories/1/products/page?name=seed&page=0&size=5&sortBy=price&direction=desc
```

![Categoría paginada](assets/10-categoria-page.png)

---

# Práctica 11 - Autenticación con JWT

## Objetivo

Agregar autenticación usando JWT.

## Endpoints

```text
POST /api/auth/register

POST /api/auth/login
```

Después del login se debe enviar el token.

```text
Authorization: Bearer <token>
```

## Archivos

- JwtUtil
- JwtAuthenticationFilter
- JwtAuthenticationEntryPoint
- AuthService

## Registro

```http
POST /api/auth/register
```

```json
{
  "name": "Ana Torres",
  "email": "ana@example.com",
  "password": "Secret123"
}
```

![Registro](assets/11-register.png)

## Login

![Login](assets/11-login.png)

## Sin token

```json
{
  "status": 401,
  "message": "Token de autenticación inválido o no proporcionado"
}
```

![401](assets/11-sin-token.png)

## Con token

![200](assets/11-con-token.png)

---

# Práctica 12 - Roles y @PreAuthorize

## Objetivo

Restringir algunos endpoints según el rol del usuario.

## Ejemplo

```java
@GetMapping
@PreAuthorize("hasRole('ADMIN')")
public List<ProductResponseDto> findAll() {
    return productService.findAll();
}
```

También se aplicó en `UserController`.

## Manejo del 403

Se agregaron estos handlers.

```java
@ExceptionHandler(AuthorizationDeniedException.class)

@ExceptionHandler(AccessDeniedException.class)
```

## Asignar rol ADMIN

```sql
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@example.com'
AND r.name = 'ROLE_ADMIN';
```

Después es necesario volver a iniciar sesión para generar un nuevo token.

## Usuario sin permisos

```json
{
  "status": 403,
  "message": "No tienes permisos para acceder a este recurso"
}
```

![403](assets/12-forbidden-user.png)

## Usuario ADMIN

![200](assets/12-ok-admin.png)

---

# Práctica 13 - Ownership y validación de propiedad

## Objetivo

Que un usuario solo pueda editar o eliminar sus propios productos. Un ADMIN puede modificar cualquiera.

## CreateProductDto

Ya no recibe `userId`. El owner sale del token, no del body.

## ProductService

Los métodos que modifican datos ahora reciben al usuario autenticado.

```java
ProductResponseDto create(CreateProductDto dto, UserDetailsImpl currentUser);
ProductResponseDto update(Long id, UpdateProductDto dto, UserDetailsImpl currentUser);
void delete(Long id, UserDetailsImpl currentUser);
```

## validateOwnership

```java
private void validateOwnership(ProductEntity product, UserDetailsImpl currentUser) {
    if (hasRole(currentUser, "ROLE_ADMIN")) {
        return;
    }

    if (!product.getOwner().getId().equals(currentUser.getId())) {
        throw new AccessDeniedException("No puedes modificar productos ajenos");
    }
}
```

Se llama antes de `update`, `partialUpdate` y `delete` en `ProductServiceImpl`.

## GlobalExceptionHandler

El handler de `AccessDeniedException` ahora usa el mensaje de la excepción en vez de uno fijo.

```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ErrorResponse> handleAccessDeniedException(
        AccessDeniedException ex,
        HttpServletRequest request) {

    String message = ex.getMessage() != null ? ex.getMessage() : "Acceso denegado";

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(HttpStatus.FORBIDDEN, message, request.getRequestURI()));
}
```

## Slice solo del dueño

`GET /products/slice` sigue abierto a cualquier usuario logueado, pero ahora filtra por owner en el repositorio, no en Java: así la base de datos devuelve únicamente las filas de ese usuario en vez de traer todos los productos a memoria.

```java
@Query("""
        SELECT p
        FROM ProductEntity p
        WHERE p.deleted = false
          AND p.owner.id = :userId
        """)
Slice<ProductEntity> findActiveSliceByOwnerId(@Param("userId") Long userId, Pageable pageable);
```

## Crear producto sin userId

```http
POST /api/products
```

```json
{
  "name": "Laptop",
  "price": 900,
  "stock": 10,
  "categoryIds": [1]
}
```

El `owner` de la respuesta corresponde al usuario del token, no a nada enviado en el body.

![Crear producto](assets/13-create-sin-userid.png)

## Editar producto propio

![200](assets/13-update-propio.png)

## Editar producto ajeno

```json
{
  "status": 403,
  "message": "No puedes modificar productos ajenos"
}
```

![403](assets/13-update-ajeno.png)

## Eliminar producto ajeno

![403](assets/13-delete-ajeno.png)


## Slice solo del dueño

![Slice filtrado](assets/13-slice-propio.png)

---

# Práctica 14 - Despliegue en producción

## Objetivo

Preparar el proyecto para correr en un ambiente real: profiles separados por ambiente, Actuator para monitoreo, y una imagen Docker pensada para producción (no solo para desarrollo).

## Profiles

```
src/main/resources/
├── application.yml        ← Base (común a dev y prod)
├── application-dev.yml    ← Desarrollo
└── application-prod.yml   ← Producción
```

`application-prod.yml` no tiene ningún valor hardcodeado, todo sale de variables de entorno:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate

jwt:
  secret: ${JWT_SECRET}
```

`ddl-auto: validate` en vez de `update`: en producción Hibernate solo verifica que las tablas coincidan con las entidades, nunca las modifica solo.

Por defecto corre con `dev` (definido en `application.yml`). Para activar otro:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## Spring Boot Actuator

```
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## Proteger Actuator

```java
.requestMatchers("/actuator/health").permitAll()
.requestMatchers("/actuator/**").hasRole("ADMIN")
```

Durante las pruebas se encontró un problema: cuando `hasRole` bloqueaba el acceso, Spring hacía una redirección interna hacia `/error` para generar la respuesta.

El problema era que el filtro JWT no guardaba el contexto de autenticación en un `SecurityContextRepository`, por lo que al entrar a `/error` se perdía la sesión y se devolvía un error **401** en lugar de **403**.

La solución fue permitir el acceso público a `/error`, ya que la validación de seguridad ya se había realizado previamente. De esta forma, la respuesta puede generarse correctamente sin volver a perder la autenticación.

```java
.requestMatchers("/error").permitAll()
```

## Dockerfile de producción

Cambios sobre el Dockerfile de la práctica de Docker:

```dockerfile
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
```

- Usuario no-root: si alguien ejecuta código dentro del contenedor, no corre como root.
- `HEALTHCHECK`: es lo que hace que `docker ps` muestre `(healthy)` en vez de solo `Up`.

## docker-compose con profile prod

```yaml
environment:
  SPRING_PROFILES_ACTIVE: prod
  DATABASE_URL: jdbc:postgresql://db:5432/devdb
  DB_USERNAME: ups
  DB_PASSWORD: ups123
  JWT_SECRET: ...
```

## Capturas

```bash
docker compose up -d --build
docker ps
```

![Contenedor healthy](assets/14-docker-ps-healthy.png)

Health check público, sin token:

```bash
curl http://localhost:8080/api/actuator/health
```

![Health](assets/14-actuator-health.png)

Metrics con un usuario normal (no ADMIN):

```json
{
  "status": 403,
  "error": "Forbidden",
  "path": "/api/actuator/metrics"
}
```

![403 metrics](assets/14-actuator-metrics-forbidden.png)

Metrics con ADMIN:

![200 metrics](assets/14-actuator-metrics-admin.png)

## Explicación 1: ¿Qué es Tomcat embebido?

Tomcat es el servidor encargado de recibir las solicitudes HTTP y comunicarlas con Spring. Que sea **embebido** significa que viene incluido dentro de la aplicación, por lo que no es necesario instalar ni configurar un servidor externo.

Gracias a `spring-boot-starter-web`, Tomcat se empaqueta junto con la aplicación. Entonces, al ejecutar `java -jar app.jar`, se inicia tanto el servidor como la aplicación al mismo tiempo, sin configuraciones adicionales.

## Explicación 2: ¿Existe Nginx embebido en Spring Boot?

No. Nginx es un servicio independiente y no viene incluido dentro de Spring Boot.

Normalmente se coloca delante de la aplicación como un **reverse proxy**. Recibe las solicitudes en los puertos 80/443 y luego las envía hacia Tomcat en el puerto 8080.

Se utiliza principalmente para manejar HTTPS, conectar varios servicios con un mismo dominio o distribuir tráfico. Estas funciones no las realiza directamente Tomcat.

## Explicación 3: ¿Qué es Actuator?

Actuator es un módulo de Spring Boot que agrega endpoints de monitoreo ya preparados, como `/actuator/health`, `/actuator/metrics` y `/actuator/info`.

Permite revisar el estado de la aplicación, obtener métricas del sistema y ayudar a herramientas externas, como Docker o balanceadores de carga, a comprobar si el servicio está funcionando correctamente.

---

# Refresh tokens

## Objetivo

Antes, cuando el access token expiraba después de 30 minutos, era necesario iniciar sesión nuevamente usando el correo y la contraseña.

Ahora el login entrega dos tokens: un **access token** de corta duración para acceder a la API y un **refresh token** con una duración mayor (7 días). Este último permite obtener un nuevo access token sin tener que ingresar la contraseña nuevamente.

## Diferenciar los tokens

El riesgo de tener dos JWT es que alguien mande el refresh token como si fuera un access token. Para evitarlo, cada token lleva un claim `type`:

```java
private static final String ACCESS_TOKEN_TYPE = "access";
private static final String REFRESH_TOKEN_TYPE = "refresh";
```

`JwtAuthenticationFilter` ahora valida `validateAccessToken(jwt)` en vez de `validateToken(jwt)` — si alguien manda un refresh token en el header `Authorization`, lo rechaza.

## RefreshTokenEntity

El refresh token también se guarda en base de datos (tabla `refresh_tokens`), no solo como JWT, para poder revocarlo antes de que expire:

```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;
}
```

## Endpoints nuevos

```txt
POST /api/auth/refresh   -> valida el refresh token y devuelve un par nuevo (rotación)
POST /api/auth/logout    -> revoca el refresh token recibido
```

Ambos son públicos: no se validan con access token, se validan con el propio refresh token dentro de `RefreshTokenService`.

## Rotación

Cada vez que se utiliza `/auth/refresh`, el refresh token usado se invalida y se genera uno nuevo.

Si alguien intenta utilizar nuevamente un token antiguo, por ejemplo, uno que fue robado, `RefreshTokenService.validateAndGetActiveToken()` lo rechaza porque ya tiene el estado `revoked = true`.

Además, durante el inicio de sesión se revocan los refresh tokens anteriores del usuario. Esto permite mantener una sola sesión activa y evita que existan varios tokens válidos al mismo tiempo.

## Capturas

Login (devuelve ambos tokens):

![Login con refresh token](assets/extra-login.png)

Intentar usar el refresh token como Bearer (rechazado):

![Refresh token rechazado como Bearer](assets/extra-refresh-como-bearer.png)

Refresh exitoso (tokens nuevos):

![Refresh exitoso](assets/extra-refresh-ok.png)

Reusar el refresh token ya rotado (rechazado):

![Refresh token reusado](assets/extra-refresh-reusado.png)

Logout y luego intentar refrescar con ese mismo token (rechazado):

![Refresh después de logout](assets/extra-refresh-despues-logout.png)

## Explicación 1: ¿Cuál es la diferencia entre access token y refresh token?

El **access token** tiene una duración corta (30 minutos en este caso) y se utiliza en cada petición a la API mediante el encabezado `Authorization: Bearer`.

El **refresh token** tiene una duración más larga (7 días) y no permite acceder directamente a los endpoints de la aplicación. Su única función es solicitar un nuevo access token desde `/auth/refresh` sin tener que ingresar nuevamente la contraseña.

## Explicación 2: ¿Por qué el refresh token no debe usarse en Authorization: Bearer?

Porque el refresh token tiene una duración mayor y, si alguien lo obtiene, podría causar más daño. Un access token robado solo permitiría acceso durante 30 minutos, mientras que un refresh token podría mantenerse válido durante 7 días.

Por esta razón, cada JWT tiene un campo `type` que indica si es un `access` o un `refresh`. El `JwtAuthenticationFilter` solo acepta tokens de tipo `access` en el encabezado `Authorization`. Si se envía un refresh token en ese lugar, la solicitud es rechazada con un error 401.

## Explicación 3: ¿Qué significa rotar un refresh token?

La rotación significa que cada vez que se usa un refresh token en `/auth/refresh`, este se invalida y se genera uno nuevo.

De esta forma, un mismo refresh token no puede utilizarse varias veces. Si alguien roba un token y lo usa, el usuario original notará que su token dejó de funcionar porque ya fue revocado.

---

# Práctica 15 - Documentación con Swagger/OpenAPI

## Objetivo

Que la API se documente sola a partir del código: cada endpoint y cada DTO explicado, probable desde el navegador, sin depender de Bruno/Postman para saber qué manda o devuelve cada ruta.

## Dependencia

```groovy
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9'
```

## OpenApiConfig

Registra el título/descripción de la API y el esquema `bearerAuth`, para que Swagger UI muestre el botón **Authorize** y mande el JWT en cada request de prueba:

```java
@Bean
public OpenAPI customOpenAPI() {
    SecurityScheme bearerScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

    return new OpenAPI()
            .info(new Info().title("API de programacion y plataformas web").version("1.0.0"))
            .components(new Components().addSecuritySchemes("bearerAuth", bearerScheme))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
}
```

## Abrir Swagger sin token

`SecurityConfig` ya exige token en casi todo, así que Swagger también quedaba bloqueado. Se agregó:

```java
.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
```

## Documentar endpoints

En los controllers, `@Operation` para el resumen y `@ApiResponses` para los códigos posibles:

```java
@Operation(summary = "iniciar sesión")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "login correcto, devuelve access token y refresh token"),
        @ApiResponse(responseCode = "401", description = "email o contraseña incorrectos")
})
@PostMapping("/login")
```

Se documentaron así los 4 endpoints de `AuthController` y los 10 de `ProductController`.

## Documentar DTOs

`@Schema` en la clase y en cada campo:

```java
@Schema(description = "Datos requeridos para iniciar sesión")
public class LoginRequestDto {

    @Schema(description = "Correo institucional o personal del usuario", example = "usera@ups.edu.ec")
    private String email;
}
```

Se aplicó a `LoginRequestDto`, `RegisterRequestDto`, `PaginationDto`, `CreateProductDto`, `UpdateProductDto`, `PartialUpdateProductDto` y `ProductResponseDto`.

## ¿Por qué Swagger puede quedar público si los endpoints siguen protegidos?

Porque son dos cosas diferentes. Las rutas `/swagger-ui/**` y `/v3/api-docs/**` solo muestran la documentación de la API, como los endpoints disponibles, los datos que reciben y las respuestas que devuelven.

Estas rutas no permiten acceder a información real ni omiten la seguridad. Si se quiere usar un endpoint protegido desde Swagger, primero es necesario iniciar sesión y usar el botón **Authorize** con un JWT válido, igual que con cualquier otro cliente HTTP.

## Capturas de pantalla

Swagger UI cargado, con los controllers agrupados por tag:

![Swagger UI cargado](assets/15-swagger-ui.png)

JSON de OpenAPI (`/api/v3/api-docs`):

![JSON OpenAPI](assets/15-openapi-json.png)

AuthController documentado:

![AuthController documentado](assets/15-authcontroller-documentado.png)

Botón Authorize con el esquema bearerAuth:

![Botón Authorize](assets/15-authorize-button.png)

Endpoint protegido sin token (401):

![Sin token](assets/15-endpoint-sin-token.png)

Mismo endpoint autorizado desde Swagger (200):

![Con token](assets/15-endpoint-con-token.png)

Endpoint solo-ADMIN con usuario normal (403):

![403 admin](assets/15-admin-403.png)

Mismo endpoint con usuario ADMIN (200):

![200 admin](assets/15-admin-200.png)

## Explicación 1: ¿Cuál es la diferencia entre Swagger UI y OpenAPI?

OpenAPI es la especificación que describe la API en un archivo JSON (`/api/v3/api-docs`). Ahí se define qué endpoints existen, qué datos reciben y qué respuestas devuelven.

Swagger UI es una interfaz gráfica que usa esa información para mostrar la documentación de forma más fácil de entender. Además, permite probar los endpoints directamente desde el navegador.

## Explicación 2: ¿Por qué Swagger puede ser público y los endpoints seguir protegidos?

Porque la documentación y la seguridad son cosas diferentes. Las rutas `/swagger-ui/**` y `/v3/api-docs/**` solo muestran información sobre la API y no ejecutan ninguna acción en la aplicación.

La protección de los endpoints se configura en `SecurityConfig`. Cuando alguien intenta usar un endpoint, el sistema verifica si tiene un JWT válido. Si no está autenticado, el acceso es rechazado.

## Explicación 3: ¿Cómo se configura Swagger para enviar un JWT?

En `OpenApiConfig` se crea un `SecurityScheme` de tipo `Bearer JWT` y se agrega como requisito de seguridad para la API.

Con esta configuración aparece el botón **Authorize** en Swagger UI. Al ingresar un token, Swagger lo envía automáticamente en el encabezado `Authorization: Bearer <token>` en cada petición que se haga desde la interfaz.

---

# Práctica 16 - Despliegue portable con Docker y Nginx

## Objetivo

Ejecutar la misma imagen Docker sin usar Docker Compose. Se crean los contenedores con `docker run`, usando Nginx como *reverse proxy* en el puerto 80, Spring Boot en una red interna y Postgres como base de datos externa. Toda la configuración se realiza con variables de entorno, sin guardar credenciales dentro de la imagen.


## Arquitectura

```txt
Mac (host)
└── red Docker: app-network
    ├── postgres-external   → Postgres "externo" (fallback documentado en la práctica)
    ├── spring-app          → Spring Boot, privado, sin puerto publicado
    └── nginx-proxy         → Nginx, publicado en :80 (hace de "Ubuntu Server")
```

## Comandos (sin Docker Compose)

```bash
docker network create app-network

docker run -d --name postgres-external --network app-network \
  -e POSTGRES_USER=ups -e POSTGRES_PASSWORD=ups123 -e POSTGRES_DB=devdb \
  -v postgres-external-data:/var/lib/postgresql/data \
  postgres:16

docker build -t fundamentos01-app:ubuntu-sim .

docker run -d --name spring-app --network app-network \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://postgres-external:5432/devdb \
  -e DB_USERNAME=ups \
  -e DB_PASSWORD=ups123 \
  -e JWT_SECRET=mySecretKeyForJWT2024MustBeAtLeast256BitsLongForHS256Algorithm \
  fundamentos01-app:ubuntu-sim

docker run -d --name nginx-proxy --network app-network -p 80:80 \
  -v "$(pwd)/nginx/nginx.conf:/etc/nginx/nginx.conf:ro" \
  nginx:alpine
```

`spring-app` no publica ningún puerto al host — solo es alcanzable dentro de `app-network`, igual que en la práctica original donde Spring Boot queda privado y solo Nginx da la cara.

## nginx.conf

```nginx
http {
    resolver 127.0.0.11 valid=10s;

    server {
        listen 80;

        location / {
            set $backend "spring-app:8080";
            proxy_pass http://$backend;
            proxy_set_header Host $host;
        }
    }
}
```

## Sobre el PostgreSQL "externo"

La arquitectura original se conecta a una base de datos Postgres que está en la máquina principal, fuera de Docker. En esta práctica se usó un contenedor de Postgres (postgres-external) dentro de la misma red de Docker y se configuró mediante DATABASE_URL, igual que cualquier base de datos externa.

Para la aplicación no importa si Postgres está en un contenedor, en la máquina principal o en otro servidor; solo utiliza la dirección de conexión que se le proporciona.

## Probando

`docker ps` con los tres contenedores corriendo:

![docker ps](assets/16-docker-ps.png)

Health check "desde Ubuntu Server" (dentro de la red Docker, sin pasar por Nginx):

![Health desde la red](assets/16-health-desde-red.png)

Health check "desde la máquina anfitriona" (a través de Nginx, puerto 80):

![Health desde el host](assets/16-health-desde-host.png)

Login consumido desde la máquina anfitriona con Bruno, apuntando a `http://localhost/api/auth/login` (puerto 80, no 8080):

![Login desde Bruno](assets/16-login-bruno.png)

## Explicación: conexión a PostgreSQL externo

La arquitectura original se conecta a una base de datos Postgres que está fuera de Docker, en la máquina principal. Como en esta práctica todo se hizo en una sola máquina, se utilizó un contenedor de Postgres (postgres-external) dentro de la misma red de Docker.

La aplicación Spring Boot se conecta usando la variable DATABASE_URL, igual que lo haría con una base de datos externa. Para la aplicación no hay diferencia si Postgres está en un contenedor, en la máquina principal o en otro servidor; solo necesita la dirección de conexión.




