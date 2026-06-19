# Práctica 01 - Configuración de Spring Boot

Primera práctica de Spring Boot de la materia de Programación y Plataformas Web. Aquí
dejo el entorno listo, levanto el servidor y armo los primeros endpoints REST: uno de
estado y otros para un pequeño dominio de estudiantes.

## Datos del proyecto

- Group: `ec.edu.ups.icc`
- Artifact: `fundamentos01`
- Package: `ec.edu.ups.icc.fundamentos01`
- Java 25, Gradle (Groovy), Spring Boot 4.1.0
- Dependencias: Spring Web (starter `spring-boot-starter-webmvc`) y Spring Boot DevTools

## Cómo correrlo

Desde la carpeta del proyecto:

```bash
./gradlew bootRun
```

El servidor arranca con un Tomcat embebido en el puerto 8080. En `application.yml` dejé
configurada una ruta base `/api`, así que todos los endpoints cuelgan de ahí.

## Endpoints

Estado del servicio:

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

Lista de estudiantes:

```
GET http://localhost:8080/api/students
```

```json
[
  { "id": 2, "name": "Juan", "age": "30" },
  { "id": 1, "name": "Diego", "age": "10" }
]
```

Cantidad de estudiantes:

```
GET http://localhost:8080/api/students/count
```

```
Total Estudiantes: 2
```

## Estructura

```
src/main/java/ec/edu/ups/icc/fundamentos01/
├── Fundamentos01Application.java     Punto de entrada
├── StatusController.java             Endpoint de estado
└── students/
    ├── controllers/
    │   └── StudentController.java    Endpoints de estudiantes
    └── models/
        └── Student.java              Modelo de estudiante
```

La configuración va en `src/main/resources/application.yml`.

## Lo que entendí

Separé la lógica de estudiantes en su propio paquete (`students`), con el modelo y el
controlador en carpetas distintas. El controlador guarda una lista en memoria con dos
estudiantes y la expone con dos rutas: una que devuelve la lista completa y otra que
solo cuenta cuántos hay.

La anotación `@RestController` hace que lo que retorna cada método se convierta solo a
JSON. Con `@RequestMapping("/students")` defino la ruta base de la clase, y luego
`@GetMapping` y `@GetMapping("/count")` arman las sub-rutas. Como en `application.yml`
puse `context-path: /api`, esa ruta base se antepone a todo.

Lo del servidor embebido me quedó claro: no instalé Tomcat aparte, ya viene dentro del
proyecto. Por eso con `./gradlew bootRun` se levanta todo y en los logs aparece
"Tomcat started on port 8080" y luego "Started Fundamentos01Application".
