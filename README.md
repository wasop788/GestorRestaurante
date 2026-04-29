# 🍽 GestorRestaurante

Aplicación de escritorio para la gestión integral de un restaurante, desarrollada como Proyecto Final del Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM).

---

## Descripción

GestorRestaurante es una aplicación de escritorio desarrollada en Java con JavaFX que permite gestionar de forma completa las operaciones diarias de un restaurante: mesas, pedidos, carta de productos, usuarios del sistema y configuración del negocio. Incluye generación automática de tickets en PDF al cobrar una mesa.

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17+ | Lenguaje principal |
| JavaFX | 21 | Interfaz gráfica de escritorio |
| Maven | 3.9 | Gestión de dependencias |
| MySQL | 8 | Base de datos |
| JDBC | - | Conexión a la base de datos |
| iText | 5.5.13 | Generación de tickets PDF |
| jpackage | JDK 25 | Generación del instalador .exe |

---

## Características principales

- **Login con roles**: acceso diferenciado para administrador y camarero
- **Gestión de mesas**: visualización en tiempo real del estado (libre/ocupada), añadir, editar y eliminar mesas
- **Gestión de pedidos**: añadir productos de la carta, calcular totales automáticamente
- **Cobro y ticket PDF**: generación automática de ticket al cerrar una mesa
- **Gestión de carta**: CRUD completo de productos por categoría (solo admin)
- **Gestión de usuarios**: crear, editar y eliminar cuentas del personal (solo admin)
- **Configuración del restaurante**: nombre, dirección, teléfono y CIF personalizables (aparecen en el PDF)
- **Instalador .exe**: instalable como cualquier programa de Windows

---

## Requisitos previos

- Java 17 o superior
- MySQL 8
- Maven 3.6+

---

## Instalación en entorno de desarrollo

### 1. Clonar el repositorio

```bash
git clone https://github.com/wasop788/GestorRestaurante.git
cd GestorRestaurante
```

### 2. Crear la base de datos

Abre MySQL Workbench y ejecuta el script:

```
database.sql
```

### 3. Configurar la contraseña de MySQL

Abre `src/main/java/com/restaurante/util/Conexion.java` y cambia la contraseña:

```java
private static final String PASSWORD = "tu_password";
```

### 4. Ejecutar la aplicación

```bash
mvn javafx:run
```

---

## Instalación con el instalador .exe

1. Instalar MySQL 8 y ejecutar `database.sql` desde MySQL Workbench
2. Ejecutar `GestorRestaurante-1.0.exe` como administrador
3. Seguir el asistente de instalación
4. Abrir el programa desde el acceso directo del escritorio

---

## Credenciales por defecto

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | Administrador |
| juan | cam123 | Camarero |

---

## Estructura del proyecto

```
src/
└── main/
    ├── java/
    │   └── com/restaurante/
    │       ├── controller/    # Controladores JavaFX
    │       ├── dao/           # Acceso a base de datos
    │       ├── model/         # Clases del modelo
    │       └── util/          # Utilidades (conexión, sesión, PDF)
    └── resources/
        └── com/restaurante/
            ├── views/         # Archivos FXML (pantallas)
            └── images/        # Icono de la aplicación
database.sql                   # Script de creación de la BD
```

---

## Autor

Jorge — 2º DAM, Digitech
