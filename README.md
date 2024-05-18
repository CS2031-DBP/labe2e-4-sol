# 📋 Proyecto E2E - Entrega 4 Autorización y Autenticación

## Descripción 💡

Este laboratorio se centra en añadir la capa de autenticación y autorización a la aplicación de la entrega anterior.
Para ello utilizaremos Spring Security y JWT.

En esta entrega deberás:

- Proteger endpoints por roles.
- Implementar autenticación y autorización con JWT.
- Implementar el servicio de JWT para manipular los tokens.
- Crear los endpoints de login `/auth/login` y registro `/auth/register` en un controlador con el mapping `/auth`.

## Requerimientos 📋

Este repositorio tiene ya en el `pom.xml` las dependencias necesarias para trabajar con Spring Security y JWT.
Spring Security es el mismo y asegura la totalidad de los endpoints por defecto.
Por parte de JWT, se tiene la dependencia `java-jwt` de auth0, si estas familiarizado con otra implementación puedes
utilizarla,
solo asegurate que el funcionamiento en conjunto con Spring Security genere los resultados detallados aquí.

## Evaluación 🚀

En la evaluación de este laboratorio se interpreta tus configuraciones de Spring Security y JWT como una caja negra
, solo se evaluará el comportamiento de los endpoints. Osea que tu solución rechace peticiones no autorizadas y permita
las autorizadas. Esto no significa que puedes hardcodear las respuestas, ya que los test hacen uso de Spring Security
por debajo.

Los test verifican las siguientes situaciones:

1. Usuario con un rol permitido pueda acceder a un endpoint definido para ese rol.
2. Usuario con un rol no permitido no puede acceder a un endpoint definido para un rol distinto.
3. Usuario no autenticado no pueda acceder a ningun endpoint protegido.
4. Retornar los errores adecuados cuando roles permitidos pero que no sean dueños del recurso tratan de modificar un recurso
   ajeno.
5. Los endpoints `/auth/login` y `/auth/register` funcionen correctamente y generen un token JWT válido.

### Hints 🤓

- DTOs los endpoits de login y registro:

    - `/auth/login` request:
        ```json
        {
            "username": "admin",
            "password": "admin"
        }
        ```
    - `/auth/login` response:
        ```json
        {
            "token": "eyJhbGciOiJIU..."
        } 
        ```
    - `/auth/register` request:
        ```json
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "johndoe@example.com",
          "password": "mysecretpassword",
          "phone": "123-456-7890",
          "isDriver": true,
          "category": "X",
          "vehicle": {
              "brand": "Toyota",
              "model": "Camry",
              "licensePlate": "ABC124",
              "fabricationYear": 2018,
              "capacity": 5
            }
        }
      ```
    - `/auth/register` response:
        ```json
        {
          "token": "eyJhb..."
        }
        ```


- En los `service`, se ha dejado métodos en los cuales se define el email del usuario como `String email = "email"`.
  En estos casos, es necesario acceder al correo electrónico del usuario autenticado utilizando Spring Security.
  Para manejar esto, así como lo mencionado en el punto 4 de la sección de Evaluación🚀, investige que es
  el `SecurityContext` y como funciona el `SecurityContextHolder`.


- Respecto a este último punto, hay situaciones en las cuales una solicitud no será rechazada automáticamente
  si el usuario tiene un rol permitido. Por ende, se requiere implementar y manejar excepciones personalizadas,
  como se hizo 2 laboratorios atrás.

### Roles permitidos por endpoints

#### Auth Controller

- Todos los endpoints de este controlador están abiertos para cualquier usuario.

#### Driver Controller

| Endpoint               | Roles Aceptados   |
|------------------------|-------------------|
| GET /driver/{id}       | DRIVER, PASSENGER |
| GET /driver/me         | DRIVER            |
| DELETE /driver/{id}    | DRIVER            |
| PATCH /driver/{id}     | DRIVER            |
| PATCH /driver/{id}/car | DRIVER            |

#### Passenger Controller

| Endpoint                                | Roles Aceptados   |
|-----------------------------------------|-------------------|
| GET /passenger/me                       | PASSENGER         |
| GET /passenger/{id}                     | PASSENGER, DRIVER |
| DELETE /passenger/{id}                  | PASSENGER         |
| PATCH /passenger/me                     | PASSENGER         |
| POST /passenger/places                  | PASSENGER         |
| GET /passenger/places                   | PASSENGER         |
| DELETE /passenger/places/{coordinateId} | PASSENGER         |

#### Review Controller

| Endpoint            | Roles Aceptados |
|---------------------|-----------------|
| POST /review/new    | PASSENGER       |
| DELETE /review/{id} | PASSENGER       |

#### Ride Controller

Aquí tienes la tabla de los endpoints con los roles aceptados:

| Endpoint                    | Roles Aceptados   |
|-----------------------------|-------------------|
| POST /ride/request          | PASSENGER         |
| PATCH /ride/assign/{rideId} | DRIVER            |
| PATCH /ride/{rideId}        | PASSENGER, DRIVER |
| PATCH /ride/{rideId}/status | DRIVER, PASSENGER |
| GET /ride/user              | PASSENGER         |

## Cambios en la entidad `Driver` ⚠️

Se ha eliminado la relación entre `Driver` y `Coordinate` remplazandola por una columna
llamada `hexAddress` de tipo `String`.
Este campo representa la ubicación geo-espacial del conductor calculada con el algoritmo `H3` de Uber.
Esto nos servirá más adelante cuando implementemos la funcionalidad de búsqueda de conductores cercanos.

Puedes encontrar más información sobre `H3` en el siguiente enlace: [H3](https://www.uber.com/en-PE/blog/h3/), y algunos
ejemplos de uso en el [repo](https://github.com/uber/h3-java) oficial de Uber.
Con esto trata de ir imaginando cómo implementarías la solución para hallar conductores cercanos teniendo longitud y latitud en
tiempo real.   


