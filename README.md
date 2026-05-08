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

## Descarga e instalación

### Descargar el instalador

El instalador está disponible en la sección **Releases** del repositorio:

```
https://github.com/wasop788/GestorRestaurante/releases
```

Descarga el archivo `GestorRestaurante-1.0.exe` de la última release.

### Pasos de instalación

1. Instalar MySQL 8 desde https://dev.mysql.com/downloads/installer
    - Durante la instalación, establecer la contraseña del usuario `root` como **`admin`**
2. Abrir MySQL Workbench y ejecutar el script `database.sql` del repositorio para crear la base de datos
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

## Generar el instalador

Para generar un nuevo instalador desde el código fuente:

**Paso 1 — Compilar el proyecto:**
```bash
mvn clean package
copy target\GestorRestaurante-full.jar dist\
```

**Paso 2 — Crear el runtime con JavaFX:**
```bash
jlink --module-path "C:\Users\jorge\.m2\repository\org\openjfx\javafx-controls\21;C:\Users\jorge\.m2\repository\org\openjfx\javafx-fxml\21;C:\Users\jorge\.m2\repository\org\openjfx\javafx-graphics\21;C:\Users\jorge\.m2\repository\org\openjfx\javafx-base\21;C:\Users\jorge\.jdks\openjdk-25\jmods" --add-modules java.base,java.sql,java.desktop,java.naming,java.security.jgss,javafx.controls,javafx.fxml,javafx.graphics,javafx.base --output runtime-fx
```

**Paso 3 — Generar el instalador:**
```bash
jpackage --input dist\ --main-jar GestorRestaurante-full.jar --main-class com.restaurante.MainApp --name "GestorRestaurante" --app-version "1.0" --vendor "Digitech" --description "Sistema de gestion para restaurante" --win-shortcut --win-menu --win-dir-chooser --icon src\main\resources\com\restaurante\images\icono.ico --dest instalador\ --type exe --runtime-image runtime-fx
```

El instalador generado se sube a GitHub Releases, no al repositorio.

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
dist/                          ← JAR y dependencias para el instalador
runtime-fx/                    ← runtime Java+JavaFX para el instalador
```

---

## Autor

Jorge — 2º DAM, Digitech