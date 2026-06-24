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

---

# Práctica 04 - Controladores y servicios

Saqué la lógica de los controladores y la moví a una capa de **servicios**. Ahora el
controlador solo recibe la petición y se la pasa al servicio; el servicio es el que hace
el trabajo (buscar, crear, actualizar, borrar) sobre la lista en memoria.

## Cómo se conecta

- El servicio se marca con `@Service` para que Spring lo cree y lo administre.
- El controlador lo recibe por el **constructor** (inyección de dependencias): no hace
  `new` del servicio, lo pone Spring solo.

```java
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;            // la interfaz

    public UserController(UserService userService) {  // Spring inyecta UserServiceImpl
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAll();                 // solo delega
    }
}
```

## Estructura nueva (por recurso)

```
users/
├── controllers/UserController.java   Solo recibe y delega
├── services/
│   ├── UserService.java              Interfaz: qué se puede hacer
│   └── UserServiceImpl.java          @Service: la lógica + la lista en memoria
├── dtos/  models/  mappers/          igual que antes
```

`products/` se replicó igual (`ProductService` + `ProductServiceImpl`).

Los endpoints son los mismos de la Práctica 03; la diferencia es interna. Cuando algo no
se encuentra, el servicio devuelve un `ErrorResponseDto` con un mensaje (los códigos HTTP
correctos llegan en la Práctica 07).

## Lo que entendí

El **servicio** es la clase que hace el trabajo de verdad; el **controlador** es solo la
puerta de entrada. Separarlos (cada uno con una sola responsabilidad) deja el código más
ordenado y listo para cuando se conecte una base de datos. La **inyección de
dependencias** es que yo solo pido `UserService` en el constructor y Spring me entrega la
implementación (`UserServiceImpl`, la marcada con `@Service`) sin que yo la cree a mano.

---

# Práctica 05 - Persistencia con PostgreSQL y JPA

Reemplacé la lista en memoria de `users` por una base de datos real: PostgreSQL corriendo
en Docker, conectado con Spring Data JPA + Hibernate.

## Cómo se conecta

- Levanté un contenedor `postgres-dev` con Docker (PostgreSQL 16) y una base `devdb`.
- En `application.yml` agregué los datos de conexión (`url`, `username`, `password`) y
  `ddl-auto: update`, para que Hibernate cree/actualice las tablas solo.
- Agregué las dependencias `spring-boot-starter-data-jpa` y el driver `postgresql`.

## Piezas nuevas (por recurso)

```
core/
└── entities/BaseEntity.java          id, createdAt, updatedAt, deleted (compartido)

users/
├── entities/UserEntity.java          @Entity, mapea la tabla "users"
└── repositories/UserRepository.java  extiende JpaRepository<UserEntity, Long>
```

`UserMapper` ahora también convierte `Model <-> Entity`, además de `Dto <-> Model` que ya
tenía.

## Cómo cambió el servicio

`UserServiceImpl` ya no tiene una `List<UserModel>`: usa `UserRepository`, que Spring
inyecta por el constructor igual que el servicio en la Práctica 04.

```java
private final UserRepository userRepository;

public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

Cada método llama a `userRepository` (`save`, `findById`, `findAll`) en vez de manipular
una lista. Si no existe el id, lanza un error simple (`IllegalStateException`); el manejo
de errores prolijo llega en una práctica posterior. El `delete` ya no borra la fila: solo
marca `deleted = true` (borrado lógico).

## Probando que persiste de verdad

```bash
docker exec -it postgres-dev psql -U ups -d devdb -c "SELECT * FROM users;"
```

Si reinicio la aplicación, los usuarios siguen ahí (antes, con la lista en memoria, se
perdían).

## Lo que entendí

El **repositorio** reemplaza por completo la lista en memoria: con `JpaRepository` ya
tengo `save`, `findById`, `findAll`, `delete`, etc. sin escribir SQL. La diferencia entre
**Entity** y **Model** es que la Entity sabe cómo se guarda en la tabla (tiene anotaciones
de JPA) y el Model es solo la representación interna de la aplicación; nunca se debe
exponer la Entity directamente al cliente. `BaseEntity` evita repetir `id`,
`createdAt`, `updatedAt` y `deleted` en cada entidad nueva.
