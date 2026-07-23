# BeaGi Backend

API REST desarrollada para **BeaGi Moda Circular**, una tienda de ropa femenina enfocada inicialmente en la venta de abrigos y chaquetas.

El proyecto permite administrar categorías, productos, stock y pedidos mediante una arquitectura por capas construida con Java, Spring Boot y PostgreSQL.

## Objetivos del proyecto

Este backend fue creado con los siguientes objetivos:

* Administrar categorías de productos.
* Administrar productos y su disponibilidad.
* Persistir la información en PostgreSQL.
* Crear pedidos con uno o más productos.
* Calcular subtotales y totales en el servidor.
* Descontar automáticamente el stock.
* Evitar pedidos con productos inexistentes.
* Evitar pedidos con stock insuficiente.
* Revertir completamente una operación cuando ocurre un error.
* Servir como backend para el frontend de BeaGi Moda Circular.
* Aplicar buenas prácticas de desarrollo backend y control de versiones.

## Tecnologías utilizadas

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* Jakarta Validation
* PostgreSQL
* H2 Database para pruebas
* JUnit 5
* MockMvc
* Maven Wrapper
* Git y GitHub

## Funcionalidades actuales

### Categorías

* Crear categorías.
* Listar categorías.
* Buscar una categoría por ID.
* Actualizar categorías.
* Eliminar categorías.
* Responder con `404 Not Found` cuando una categoría no existe.

### Productos

* Crear productos.
* Listar productos.
* Buscar un producto por ID.
* Actualizar productos.
* Eliminar productos.
* Asociar productos con categorías.
* Validar nombre, precio, stock y disponibilidad.
* Responder con `404 Not Found` cuando un producto no existe.

### Pedidos

* Crear pedidos con uno o más productos.
* Listar pedidos.
* Guardar información del cliente.
* Calcular el subtotal de cada producto.
* Calcular el total completo del pedido.
* Guardar el precio unitario existente al momento de la compra.
* Descontar automáticamente el stock.
* Rechazar productos inexistentes.
* Rechazar cantidades superiores al stock disponible.
* Revertir todos los cambios si cualquier detalle del pedido falla.
* Asignar el estado inicial `PENDIENTE`.

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

Recibe las solicitudes HTTP y devuelve las respuestas de la API.

### Service

Contiene la lógica de negocio, como la creación de pedidos, el cálculo de totales y el control de stock.

### Repository

Permite consultar y guardar información utilizando Spring Data JPA.

### Model

Contiene las entidades que representan las tablas y relaciones de la base de datos.

### DTO

Define los datos que la API recibe en determinadas solicitudes.

### Exception

Contiene excepciones específicas para representar errores de negocio.

## Requisitos

Para ejecutar el proyecto se necesita:

* Java 21
* PostgreSQL
* Git
* Una terminal
* Visual Studio Code u otro editor compatible

No es necesario instalar Maven globalmente, porque el proyecto utiliza Maven Wrapper.

## Clonar el repositorio

```bash
mkdir -p ~/Proyectos
cd ~/Proyectos

git clone https://github.com/mateocuetoc-hub/beagi-backend.git
cd beagi-backend

chmod +x mvnw
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

Las pruebas utilizan H2 en memoria, por lo que no modifican los datos de PostgreSQL y no requieren la variable `DB_PASSWORD`.

```bash
./mvnw test
```

Estado actual de las pruebas:

```text
Tests run: 10
Failures: 0
Errors: 0
Skipped: 0
```

Las pruebas cubren:

* Carga del contexto de Spring.
* CRUD de categorías.
* Creación de pedidos.
* Cálculo de totales.
* Descuento de stock.
* Stock insuficiente.
* Rollback transaccional.
* Productos inexistentes.

## Endpoints

### Categorías

| Método   | Endpoint               | Descripción             |
| -------- | ---------------------- | ----------------------- |
| `POST`   | `/api/categorias`      | Crear una categoría     |
| `GET`    | `/api/categorias`      | Listar categorías       |
| `GET`    | `/api/categorias/{id}` | Buscar categoría por ID |
| `PUT`    | `/api/categorias/{id}` | Actualizar categoría    |
| `DELETE` | `/api/categorias/{id}` | Eliminar categoría      |

### Productos

| Método   | Endpoint              | Descripción            |
| -------- | --------------------- | ---------------------- |
| `POST`   | `/api/productos`      | Crear un producto      |
| `GET`    | `/api/productos`      | Listar productos       |
| `GET`    | `/api/productos/{id}` | Buscar producto por ID |
| `PUT`    | `/api/productos/{id}` | Actualizar producto    |
| `DELETE` | `/api/productos/{id}` | Eliminar producto      |

### Pedidos

| Método | Endpoint       | Descripción     |
| ------ | -------------- | --------------- |
| `POST` | `/api/pedidos` | Crear un pedido |
| `GET`  | `/api/pedidos` | Listar pedidos  |

## Ejemplos de uso

La aplicación debe estar ejecutándose antes de usar estos comandos.

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

### Listar productos

```bash
curl -i http://localhost:8080/api/productos
```

### Crear un producto

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

### Crear un pedido

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

## Reglas de negocio de los pedidos

Los precios y totales se calculan en el servidor. El cliente no puede establecer manualmente el valor de un producto.

Al crear un pedido:

1. Se busca cada producto solicitado.
2. Se comprueba que exista.
3. Se verifica que tenga stock suficiente.
4. Se copia su precio actual como precio unitario.
5. Se calcula el subtotal.
6. Se descuenta el stock.
7. Se calcula el total del pedido.
8. Se guarda el pedido con sus detalles.

El proceso utiliza `@Transactional`.

Esto significa que, si cualquier producto falla, el pedido completo se revierte y no queda stock descontado parcialmente.

## Base de datos de pruebas

Las pruebas automáticas utilizan H2 mediante el perfil `test`.

Archivo de configuración:

```text
src/test/resources/application-test.properties
```

Durante las pruebas:

* Se crea una base de datos temporal en memoria.
* Se genera el esquema automáticamente.
* No se usa PostgreSQL.
* No se utiliza la contraseña real.
* Los datos desaparecen al terminar las pruebas.

## Estado del proyecto

El backend ya cuenta con:

* Persistencia mediante PostgreSQL.
* CRUD de categorías.
* CRUD de productos.
* Relación entre productos y categorías.
* Creación y listado de pedidos.
* Detalles de pedido.
* Cálculo de subtotales y total.
* Descuento automático de stock.
* Manejo de producto inexistente.
* Manejo de stock insuficiente.
* Transacciones y rollback.
* Perfil de pruebas con H2.
* Pruebas de integración con MockMvc.

## Próximos pasos

* Agregar `GET /api/pedidos/{id}`.
* Implementar cambios de estado de pedidos.
* Definir reglas para cancelar pedidos.
* Reponer stock cuando corresponda.
* Crear DTOs de respuesta.
* Implementar un manejador global de errores.
* Añadir Swagger/OpenAPI.
* Mejorar la validación de solicitudes.
* Proteger el stock frente a pedidos concurrentes.
* Incorporar migraciones con Flyway.
* Separar perfiles de desarrollo y producción.
* Conectar el backend con el frontend de BeaGi Moda Circular.
* Preparar el despliegue de la API y PostgreSQL.

## Seguridad

Este repositorio no debe contener:

* Contraseñas.
* Credenciales de PostgreSQL.
* Tokens.
* Claves privadas.
* Variables de entorno reales.

Las credenciales deben configurarse localmente o mediante variables de entorno del servicio de despliegue.

## Autor

**Mateo Cueto**

Estudiante de Ingeniería en Informática en la Pontificia Universidad Católica de Valparaíso.

Proyecto desarrollado con fines de aprendizaje, portafolio e integración con BeaGi Moda Circular.
