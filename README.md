# Prácticas Spring Boot - fundamentos01

Proyecto de la materia Programación y Plataformas Web (UPS). Sobre el mismo proyecto voy
agregando cada práctica.

## Datos del proyecto

- Group: `ec.edu.ups.icc`
- Artifact: `fundamentos01`
- Java 25, Gradle (Groovy), Spring Boot 4.1.0
- Dependencias: Spring Web (`spring-boot-starter-webmvc`) y Spring Boot DevTools

## Cómo correrlo

```bash
./gradlew bootRun
```

Arranca un Tomcat embebido en el puerto 8080. En `application.yml` dejé una ruta base
`/api`, así que todos los endpoints cuelgan de ahí.

---

# Práctica 01 - Configuración

Dejar el entorno listo (Java + Gradle + Spring Boot) y levantar un primer endpoint de
estado.

```
GET http://localhost:8080/api/api/status
```

```json
{
  "service": "Spring Boot API",
  "status": "running",
  "timestamp": "2026-06-19T02:43:22.606358Z"
}
```

> Nota: la ruta queda con `/api` doble porque al `context-path: /api` se le suma el
> `@GetMapping("/api/status")` del controlador.

---

# Práctica 02 - Estructura del proyecto

Organizar el código en paquetes por dominio. Agregué un módulo `students` con su
controlador y su modelo en carpetas separadas.

```
GET http://localhost:8080/api/students          -> lista de estudiantes
GET http://localhost:8080/api/students/count     -> cantidad de estudiantes
```

```json
[
  { "id": 2, "name": "Juan", "age": "30" },
  { "id": 1, "name": "Diego", "age": "10" }
]
```

---

# Práctica 03 - API REST

CRUD REST completo, en memoria (sin servicios ni base de datos todavía), usando DTOs,
modelo y un mapper. Lo hice para `users` y lo repliqué para `products`.

## Endpoints

Mismos 6 métodos para cada recurso (`/users` y `/products`):

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/users` | Lista todos |
| GET | `/api/users/{id}` | Uno por id |
| POST | `/api/users` | Crea (id autogenerado) |
| PUT | `/api/users/{id}` | Reemplaza todo |
| PATCH | `/api/users/{id}` | Actualiza solo lo que llega |
| DELETE | `/api/users/{id}` | Elimina |

Para productos es igual cambiando `/users` por `/products`.

## Ejemplos

Crear un usuario:

```
POST /api/users
{ "name": "Ana", "email": "ana@ups.edu.ec", "password": "1234" }
```

```json
{ "id": 1, "name": "Ana", "email": "ana@ups.edu.ec" }
```

La respuesta nunca devuelve `password` ni `passwordHash`. Si el id no existe, responde
`404` con un mensaje:

```json
{ "message": "Usuario no encontrado con id 99" }
```

Un producto tiene `id`, `name`, `price`, `stock`:

```json
{ "id": 1, "name": "Teclado", "price": 25.5, "stock": 10 }
```

## Estructura (por recurso)

```
users/
├── controllers/UserController.java   Los 6 endpoints
├── dtos/                             CreateUserDto, UpdateUserDto, PartialUpdateUserDto, UserResponseDto
├── models/UserModel.java             Datos internos del usuario
└── mappers/UserMapper.java           DTO <-> modelo (genera passwordHash y createdAt)
```

`products/` tiene la misma estructura.

## Lo que entendí

Cada recurso separa lo que entra (los DTOs `Create`/`Update`/`PartialUpdate`) de lo que
sale (`ResponseDto`), y el modelo interno queda escondido. El mapper es el que traduce
entre ellos, así no expongo datos sensibles como la contraseña.

El controlador guarda los registros en una lista en memoria y genera los ids con un
contador. Con `ResponseEntity` controlo el código HTTP: `201` al crear, `200` al
consultar o actualizar, `204` al eliminar y `404` cuando el id no existe. La diferencia
entre `PUT` y `PATCH` es que PUT reemplaza todos los campos y PATCH solo cambia los que
llegan con valor.
