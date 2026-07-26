# BeaGi Backend

API REST desarrollada para **BeaGi Moda Circular**, una tienda de ropa femenina enfocada inicialmente en la venta de abrigos y chaquetas.

El proyecto permite administrar categorías, productos, stock y pedidos mediante una arquitectura por capas construida con Java, Spring Boot y PostgreSQL.

## Objetivos del proyecto

Este backend fue creado para:

- Administrar categorías de productos.
- Administrar productos, precios y disponibilidad.
- Persistir la información en PostgreSQL.
- Crear pedidos con uno o más productos.
- Guardar información del cliente.
- Calcular subtotales y totales en el servidor.
- Descontar automáticamente el stock.
- Evitar pedidos con productos inexistentes.
- Evitar pedidos con stock insuficiente.
- Revertir completamente una operación cuando ocurre un error.
- Consultar pedidos y modificar su estado.
- Servir como backend para el frontend de BeaGi Moda Circular.
- Aplicar buenas prácticas de desarrollo backend y control de versiones.

## Tecnologías utilizadas

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Jakarta Validation
- PostgreSQL
- H2 Database para pruebas
- JUnit 5
- MockMvc
- Maven Wrapper
- Git y GitHub

## Funcionalidades actuales

### Categorías

- Crear categorías.
- Listar categorías.
- Buscar una categoría por ID.
- Actualizar categorías.
- Eliminar categorías.
- Responder con `404 Not Found` cuando una categoría no existe.
- Validar los datos recibidos.

### Productos

- Crear productos.
- Listar productos.
- Buscar un producto por ID.
- Actualizar productos.
- Eliminar productos.
- Asociar productos con categorías.
- Validar nombre, precio, stock y disponibilidad.
- Responder con `404 Not Found` cuando un producto no existe.

### Pedidos

- Crear pedidos con uno o más productos.
- Listar todos los pedidos.
- Buscar un pedido por ID.
- Guardar información del cliente.
- Calcular el subtotal de cada producto.
- Calcular el total completo del pedido.
- Guardar el precio unitario existente al momento de la compra.
- Descontar automáticamente el stock.
- Rechazar productos inexistentes.
- Rechazar cantidades superiores al stock disponible.
- Revertir todos los cambios si cualquier detalle del pedido falla.
- Asignar automáticamente el estado inicial `PENDIENTE`.
- Actualizar el estado de un pedido.
- Permitir los estados `PENDIENTE`, `CONFIRMADO`, `ENTREGADO` y `CANCELADO`.
- Responder con `404 Not Found` cuando un pedido no existe.
- Responder con `400 Bad Request` cuando el estado recibido no es válido.

## Arquitectura

El proyecto utiliza una arquitectura por capas:

```text
src/main/java/cl/mateocuetoc/beagibackend/
├── controller/
├── dto/
├── exception/
├── model/
├── repository/
├── service/
└── BeagiBackendApplication.java
```

### Controller

Recibe las solicitudes HTTP, utiliza los servicios correspondientes y devuelve las respuestas de la API.

### Service

Contiene la lógica de negocio, como la creación de pedidos, el cálculo de totales, el control de stock y la actualización de estados.

### Repository

Permite consultar, guardar, actualizar y eliminar información utilizando Spring Data JPA.

### Model

Contiene las entidades que representan las tablas y relaciones de la base de datos.

### DTO

Define los datos que la API recibe en determinadas solicitudes, evitando recibir información innecesaria directamente desde el cliente.

### Exception

Contiene excepciones específicas para representar errores de negocio, como productos inexistentes o stock insuficiente.

## Requisitos

Para ejecutar el proyecto se necesita:

- Java 21
- PostgreSQL
- Git
- Una terminal
- Visual Studio Code u otro editor compatible

No es necesario instalar Maven globalmente porque el proyecto utiliza Maven Wrapper.

## Clonar el repositorio

```bash
mkdir -p ~/Proyectos
cd ~/Proyectos

git clone https://github.com/mateocuetoc-hub/beagi-backend.git
cd beagi-backend

chmod +x mvnw
code .
```

Si el proyecto ya está descargado:

```bash
cd ~/Proyectos/beagi-backend
git pull --ff-only origin main
code .
```

## Configuración de PostgreSQL

La aplicación utiliza la siguiente configuración local:

```text
Base de datos: beagi_db
Usuario: beagi_user
Puerto: 5432
```

La contraseña no se guarda dentro del repositorio.

El archivo `application.properties` utiliza una variable de entorno:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/beagi_db
spring.datasource.username=beagi_user
spring.datasource.password=${DB_PASSWORD}
```

Para cargar la contraseña de manera segura:

```bash
unset DB_PASSWORD
read -s -p "Contraseña de beagi_user: " DB_PASSWORD
echo
export DB_PASSWORD
```

Para confirmar que la variable fue cargada sin mostrar su contenido:

```bash
printf 'Variable cargada: %s caracteres\n' "${#DB_PASSWORD}"
```

Para probar la conexión con PostgreSQL:

```bash
PGPASSWORD="$DB_PASSWORD" psql \
  -h localhost \
  -U beagi_user \
  -d beagi_db \
  -c '\conninfo'
```

## Ejecutar la aplicación

Desde la raíz del proyecto:

```bash
cd ~/Proyectos/beagi-backend
./mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

Para detener la aplicación:

```text
Ctrl + C
```

## Ejecutar las pruebas

Las pruebas utilizan H2 en memoria. Por lo tanto, no modifican los datos de PostgreSQL y no requieren la variable `DB_PASSWORD`.

Para ejecutar todas las pruebas:

```bash
cd ~/Proyectos/beagi-backend
./mvnw test
```

Estado actual:

```text
Tests run: 15
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Las pruebas cubren:

- Carga del contexto de Spring.
- CRUD de categorías.
- Validación de categorías.
- Creación de pedidos.
- Cálculo de subtotales y totales.
- Descuento automático de stock.
- Rechazo por stock insuficiente.
- Rollback transaccional.
- Rechazo de productos inexistentes.
- Consulta de pedidos por ID.
- Actualización correcta del estado de un pedido.
- Respuesta `404` al actualizar un pedido inexistente.
- Respuesta `400` al enviar un estado inválido.

## Endpoints

### Categorías

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/categorias` | Crear una categoría |
| `GET` | `/api/categorias` | Listar categorías |
| `GET` | `/api/categorias/{id}` | Buscar una categoría por ID |
| `PUT` | `/api/categorias/{id}` | Actualizar una categoría |
| `DELETE` | `/api/categorias/{id}` | Eliminar una categoría |

### Productos

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/productos` | Crear un producto |
| `GET` | `/api/productos` | Listar productos |
| `GET` | `/api/productos/{id}` | Buscar un producto por ID |
| `PUT` | `/api/productos/{id}` | Actualizar un producto |
| `DELETE` | `/api/productos/{id}` | Eliminar un producto |

### Pedidos

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/pedidos` | Crear un pedido |
| `GET` | `/api/pedidos` | Listar todos los pedidos |
| `GET` | `/api/pedidos/{id}` | Buscar un pedido por ID |
| `PATCH` | `/api/pedidos/{id}/estado` | Actualizar el estado de un pedido |

## Códigos HTTP utilizados

| Código | Significado |
| --- | --- |
| `200 OK` | La operación se realizó correctamente |
| `201 Created` | El recurso fue creado correctamente |
| `204 No Content` | El recurso fue eliminado correctamente |
| `400 Bad Request` | Los datos enviados no son válidos |
| `404 Not Found` | El recurso solicitado no existe |

## Ejemplos de uso

La aplicación debe estar ejecutándose antes de utilizar estos comandos.

### Listar categorías

```bash
curl -i http://localhost:8080/api/categorias
```

### Crear una categoría

```bash
curl -i -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Abrigos"
  }'
```

### Buscar una categoría por ID

```bash
curl -i http://localhost:8080/api/categorias/1
```

### Listar productos

```bash
curl -i http://localhost:8080/api/productos
```

### Crear un producto

Antes de ejecutar este ejemplo debe existir la categoría con ID `1`.

```bash
curl -i -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Abrigo negro",
    "descripcion": "Abrigo largo de mujer",
    "precio": 15990,
    "stock": 5,
    "disponible": true,
    "categoria": {
      "id": 1
    }
  }'
```

### Buscar un producto por ID

```bash
curl -i http://localhost:8080/api/productos/1
```

### Crear un pedido

Antes de ejecutar este ejemplo debe existir el producto con ID `1` y debe tener stock disponible.

```bash
curl -i -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCliente": "Cliente prueba",
    "telefonoCliente": "+56911112222",
    "direccionEntrega": "San Felipe",
    "observaciones": "Pedido de prueba",
    "detalles": [
      {
        "productoId": 1,
        "cantidad": 1
      }
    ]
  }'
```

### Listar pedidos

```bash
curl -i http://localhost:8080/api/pedidos
```

### Buscar un pedido por ID

```bash
curl -i http://localhost:8080/api/pedidos/1
```

### Actualizar el estado de un pedido

```bash
curl -i -X PATCH http://localhost:8080/api/pedidos/1/estado \
  -H "Content-Type: application/json" \
  -d '{
    "estado": "CONFIRMADO"
  }'
```

Los estados aceptados actualmente son:

```text
PENDIENTE
CONFIRMADO
ENTREGADO
CANCELADO
```

Si se envía un estado que no existe, por ejemplo `NO_EXISTE`, la API responde con `400 Bad Request`.

## Ejemplo de comunicación mediante JSON

El frontend puede enviar una solicitud como esta:

```json
{
  "nombreCliente": "Mateo",
  "telefonoCliente": "+56911112222",
  "direccionEntrega": "San Felipe",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}
```

El backend procesa la información, calcula los valores, descuenta el stock y responde con otro objeto JSON que contiene el pedido creado.

## Reglas de negocio de los pedidos

Los precios y totales se calculan en el servidor. El cliente no puede establecer manualmente el valor de un producto.

Al crear un pedido:

1. Se busca cada producto solicitado.
2. Se comprueba que el producto exista.
3. Se verifica que tenga stock suficiente.
4. Se copia su precio actual como precio unitario.
5. Se calcula el subtotal de cada detalle.
6. Se descuenta la cantidad correspondiente del stock.
7. Se calcula el total completo del pedido.
8. Se asigna el estado inicial `PENDIENTE`.
9. Se guarda el pedido junto con sus detalles.

El proceso utiliza `@Transactional`.

Esto significa que, si cualquier producto falla, el pedido completo se revierte y no queda stock descontado parcialmente.

## Actualización del estado de un pedido

El estado se modifica mediante:

```http
PATCH /api/pedidos/{id}/estado
```

Ejemplo del cuerpo JSON:

```json
{
  "estado": "ENTREGADO"
}
```

La API:

1. Busca el pedido solicitado.
2. Devuelve `404 Not Found` si el pedido no existe.
3. Comprueba que el estado pertenezca al enum `EstadoPedido`.
4. Devuelve `400 Bad Request` si el valor no es válido.
5. Actualiza y guarda el pedido.
6. Devuelve el pedido actualizado con `200 OK`.

## Base de datos de pruebas

Las pruebas automáticas utilizan H2 mediante el perfil `test`.

Archivo de configuración:

```text
src/test/resources/application-test.properties
```

Durante las pruebas:

- Se crea una base de datos temporal en memoria.
- Se genera el esquema automáticamente.
- No se utiliza PostgreSQL.
- No se necesita la contraseña real.
- Los datos desaparecen al terminar las pruebas.

## Estado actual del proyecto

El backend ya cuenta con:

- Persistencia mediante PostgreSQL.
- CRUD de categorías.
- CRUD de productos.
- Relación entre productos y categorías.
- Creación y listado de pedidos.
- Consulta de pedidos por ID.
- Actualización del estado de pedidos.
- Filtrado de pedidos por estado.
- Detalles de pedido.
- Cálculo de subtotales y total.
- Descuento automático de stock.
- Manejo de productos inexistentes.
- Manejo de stock insuficiente.
- Transacciones y rollback.
- Validación de solicitudes.
- Perfil de pruebas con H2.
- Pruebas de integración con MockMvc.
- 18 pruebas automáticas aprobadas.

## Próximos pasos

- Implementar reglas de transición entre estados.
- Impedir cambios inválidos, como volver un pedido `ENTREGADO` a `PENDIENTE`.
- Definir las reglas para cancelar pedidos.
- Reponer stock cuando corresponda.
- Crear DTOs de respuesta.
- Implementar un manejador global de errores.
- Añadir Swagger/OpenAPI.
- Agregar paginación.
- Mejorar la validación de solicitudes.
- Proteger el stock frente a pedidos concurrentes.
- Incorporar migraciones con Flyway.
- Separar los perfiles de desarrollo y producción.
- Conectar el backend con el frontend de BeaGi Moda Circular.
- Incorporar Docker.
- Preparar el despliegue de la API y PostgreSQL.
- Agregar integración continua con GitHub Actions.
- Incorporar autenticación para administración.

## Seguridad

Este repositorio no debe contener:

- Contraseñas.
- Credenciales de PostgreSQL.
- Tokens.
- Claves privadas.
- Variables de entorno reales.

Las credenciales deben configurarse localmente o mediante variables de entorno del servicio de despliegue.

## Autor

**Mateo Cueto**

Estudiante de Ingeniería en Informática en la Pontificia Universidad Católica de Valparaíso.

Proyecto desarrollado con fines de aprendizaje, portafolio e integración con BeaGi Moda Circular.s