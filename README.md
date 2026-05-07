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

- MySQL 8 instalado y en ejecución
- El usuario `root` de MySQL debe tener la contraseña **`admin`**
- Maven 3.6+ (solo para entorno de desarrollo)

> **Nota**: Java y JavaFX están incluidos en el instalador `.exe`. No es necesario instalarlos por separado en el equipo donde se vaya a usar la aplicación.

---

## Instalación con el instalador .exe

1. Instalar MySQL 8 desde https://dev.mysql.com/downloads/installer
    - Durante la instalación, establecer la contraseña del usuario `root` como **`admin`**
2. Ejecutar `database.sql` desde MySQL Workbench para crear la base de datos
3. Ejecutar `GestorRestaurante-1.0.exe` como administrador
4. Seguir el asistente de instalación
5. Abrir el programa desde el acceso directo del escritorio

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

### 3. Verificar la configuración de MySQL en el código

Abre `src/main/java/com/restaurante/util/Conexion.java` y comprueba que los datos de conexión coinciden con tu instalación de MySQL:

```java
private static final String URL = "jdbc:mysql://localhost:3306/gestor_restaurante?useSSL=false&serverTimezone=Europe/Madrid";
private static final String USUARIO = "root";
private static final String PASSWORD = "admin";
```

Si tu MySQL tiene una contraseña diferente, cámbiala aquí antes de ejecutar.

### 4. Ejecutar la aplicación

```bash
mvn javafx:run
```

---

## Credenciales por defecto

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | Administrador |
| juan | cam123 | Camarero |

---

## Configuración de MySQL requerida

La aplicación se conecta a MySQL con estos parámetros:

| Parámetro | Valor |
|---|---|
| Host | localhost |
| Puerto | 3306 |
| Usuario | root |
| Contraseña | admin |
| Base de datos | gestor_restaurante |

> Si tu instalación de MySQL tiene una contraseña diferente para `root`, ejecuta esto en MySQL Workbench para cambiarla:
> ```sql
> ALTER USER 'root'@'localhost' IDENTIFIED BY 'admin';
> FLUSH PRIVILEGES;
> ```

---

## Estructura del proyecto

```
src/
└── main/
    ├── java/
    │   └── com/restaurante/
    │       ├── controller/    ← controladores JavaFX
    │       ├── dao/           ← acceso a base de datos
    │       ├── model/         ← clases Java (Mesa, Producto...)
    │       └── util/          ← utilidades (conexión, sesión, PDF)
    └── resources/
        └── com/restaurante/
            ├── views/         ← archivos FXML (pantallas)
            └── images/        ← icono de la aplicación
database.sql                   ← script de creación de la BD
instalador/                    ← instalador .exe para Windows
```

---

## Autor

Jorge — 2º DAM, Digitech