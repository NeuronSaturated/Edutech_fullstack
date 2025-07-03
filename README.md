# Edutech_fullstack

**Edutech_fullstack** es una API REST para la gestión de un sistema educativo: cursos, estudiantes, inscripciones, evaluaciones, notificaciones y reportes. Incluye:

- Documentación OpenAPI/Swagger (Springdoc)
- HATEOAS (Spring Hateoas)
- Pruebas unitarias con JUnit 5 + Mockito
- Informe de cobertura con JaCoCo
- Site estático generado por Maven (tests, cobertura, Javadoc…) 

---

## Requisitos previos

- **Java 21**  
- **Maven 3.9.10**  

---

## Instalación

```bash
git clone https://github.com/NeuronSaturated/Edutech_fullstack.git
cd Edutech_fullstack 
```
---


## Comandos útiles

Durante el desarrollo y entrega de esta evaluación hemos empleado:

| Acción                                                     | Comando                                                          |
| ---------------------------------------------------------- | ---------------------------------------------------------------- |
| Limpiar, compilar y ejecutar **todos** los tests           | `mvn clean test`                                                 |
| Ejecutar **únicamente** un test concreto                   | `mvn -Dtest=NombreClaseTest test`                                |
| Levantar la aplicación local en `localhost:8080`           | `mvn spring-boot:run`                                            |
| Empaquetar la aplicación en un JAR                         | `mvn clean package`                                              |
| Ejecutar el JAR generado                                   | `java -jar target/Edutech-0.0.1-SNAPSHOT.jar`                    |
| Generar documentación de la API (Swagger/OpenAPI)          | Arranca la app (`mvn spring-boot:run`) y abre                    |
|                                                            | `http://localhost:8080/swagger-ui.html`                          |
| Generar sitio estático de Maven (incluye JaCoCo, Javadoc…) | `mvn site`                                                       |
| Generar informe de cobertura JaCoCo **y** sitio            | `mvn clean test jacoco:report site`<br/>*(requiere plugin)*      |
| Abrir el índice del sitio en tu navegador                  | – **Linux/Mac**: `open target/site/index.html`<br/>              |
|                                                            | – **Windows PowerShell**: `Start-Process target\site\index.html` |
| Ver reporte de cobertura JaCoCo (HTML)                     | Abre `target/site/jacoco/index.html`                             |


> **Tip**:  
> - Para inspeccionar rápidamente un endpoint REST usa Swagger UI: 
```
mvn spring-boot:run
# luego abrir http://localhost:8080/swagger-ui.html
```

> - Si solo necesitas los informes generados por Maven (tests, cobertura, Javadoc…), ejecuta:
```
mvn site
open target/site/index.html

```  

---
## Estructura del proyecto

```
├── src
│   ├── main
│   │   ├── java/com/edutech/Edutech
│   │   │   ├── config           # Configuraciones 
│   │   │   ├── controller       # Controladores REST
│   │   │   ├── dto              # Data Transfer Objects
│   │   │   ├── model            # Entidades JPA
│   │   │   ├── repository       # Repositorios Spring Data
│   │   │   └── service          # Lógica de negocio
│   │   └── resources            # application.properties, etc.
│   └── test
│       ├── java/com/edutech/Edutech
│       │   ├── controller       # Tests de controladores (unitarios + HATEOAS)
│       │   └── service          # Tests de servicios con Mockito
│       └── resources            # application-test.properties
└── pom.xml                      # Configuración de Maven
```

---


## Uso de la API

Base URL: http://localhost:8080/swagger-ui/index.html#/

| Recurso                                                                              | Método | Ruta                                      | Descripción                                          |
| ------------------------------------------------------------------------------------ | ------ | ----------------------------------------- | ---------------------------------------------------- |
| **Cursos**                                                                           | GET    | `/cursos`                                 | Lista todos los cursos                               |
| **Curso por ID**                                                                     | GET    | `/cursos/{id}`                            | Obtiene un curso por su identificador                |
| **Cursos HATEOAS**                                                                   | GET    | `/cursos/hateoas`                         | Colección HAL con enlaces (`application/hal+json`)   |
| **Curso HATEOAS por ID**                                                             | GET    | `/cursos/{id}/hateoas`                    | Recurso HAL con enlaces                              |
| **Estudiantes**                                                                      | GET    | `/estudiantes`                            | Lista todos los estudiantes                          |
| **Estudiante por ID**                                                                | GET    | `/estudiantes/{id}`                       | Obtiene un estudiante por su identificador           |
| **Estudiante HATEOAS por ID**                                                        | GET    | `/estudiantes/{id}`<br/>(HAL JSON)        | Recurso HAL con enlaces                              |
| **Inscripciones**                                                                    | GET    | `/inscripciones`                          | Lista todas las inscripciones                        |
| **Inscripción por ID**                                                               | GET    | `/inscripciones/{id}`                     | Obtiene una inscripción por su identificador         |
| **Crear inscripción (payload)**                                                      | POST   | `/inscripciones`                          | JSON `{ "estudiante": {"id":…}, "curso": {"id":…} }` |
| **Crear inscripción (path IDs)**                                                     | POST   | `/inscripciones/estudiante/{e}/curso/{c}` | Crea inscripción pasando IDs en la URL               |
| **Actualizar inscripción**                                                           | PUT    | `/inscripciones/{id}`                     | Modifica inscripción                                 |
| **Eliminar inscripción**                                                             | DELETE | `/inscripciones/{id}`                     | Borra inscripción                                    |
| **Inscripciones HATEOAS**                                                            | GET    | `/inscripciones/hateoas`                  | Colección HAL con enlaces                            |
| **Inscripción HATEOAS por ID**                                                       | GET    | `/inscripciones/{id}/hateoas`             | Recurso HAL con enlaces                              |
| **Evaluaciones**, **Notificaciones**, **Reportes**, etc. siguen la misma convención. |        |                                           |                                                      |

---

## HATEOAS
Todos los endpoints ```/…/hateoas``` devuelven HAL JSON (```application/hal+json```) con:

- ```_links.self.href```

- Enlaces adicionales a recursos relacionados

---

## Documentación OpenAPI / Swagger

1. Arranca la aplicación:
```
mvn spring-boot:run
```

2. Abre en tu navegador:
- Swagger UI: ```http://localhost:8080/swagger-ui.html```
- Especificación JSON: ```http://localhost:8080/v3/api-docs```

3. Carpeta de capturas de las pruebas: https://drive.google.com/drive/folders/1p1mHlkbmlZqhTQfPotjYl2LOsgleFC6G?usp=sharing

---

## Pruebas unitarias y cobertura

- Ejecutar todos los tests:
```
mvn clean test
```

- Abrir reporte JaCoCo (HTML):
```
open target/site/jacoco/index.html
```

---

## Maven Site

Genera un sitio con Javadoc, informes de test, cobertura, etc.:
```
mvn site
```

Abre el índice en tu navegador:
- Linux/Mac: ```open target/site/index.html```

- Windows PowerShell: ```Start-Process target\site\index.html```


---

## Empaquetado y ejecución

```
# Empaquetar en JAR
mvn clean package

# Ejecutar JAR
java -jar target/Edutech-0.0.1-SNAPSHOT.jar
```
