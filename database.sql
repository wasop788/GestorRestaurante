-- =============================================
-- Script de creación de base de datos
-- Proyecto: Gestor Restaurante
-- DAM - Proyecto Final
-- =============================================

CREATE DATABASE IF NOT EXISTS gestor_restaurante
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gestor_restaurante;

-- Tabla de usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('admin', 'camarero') NOT NULL DEFAULT 'camarero',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de mesas
CREATE TABLE mesas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    capacidad INT NOT NULL DEFAULT 4,
    estado ENUM('libre', 'ocupada', 'reservada') NOT NULL DEFAULT 'libre'
);

-- Tabla de productos (carta)
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(8,2) NOT NULL,
    categoria ENUM('entrante', 'principal', 'postre', 'bebida') NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de pedidos
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_mesa INT NOT NULL,
    id_usuario INT NOT NULL,
    estado ENUM('abierto', 'cerrado', 'pagado') NOT NULL DEFAULT 'abierto',
    fecha_apertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP NULL,
    total DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (id_mesa) REFERENCES mesas(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Tabla de líneas de pedido
CREATE TABLE lineas_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(8,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);

-- =============================================
-- Datos de prueba
-- =============================================

INSERT INTO usuarios (nombre, usuario, password, rol) VALUES
('Administrador', 'admin', 'admin123', 'admin'),
('Juan García', 'juan', 'cam123', 'camarero');

INSERT INTO mesas (numero, capacidad, estado) VALUES
(1, 2, 'libre'), (2, 2, 'libre'), (3, 4, 'libre'),
(4, 4, 'libre'), (5, 4, 'libre'), (6, 6, 'libre'),
(7, 6, 'libre'), (8, 8, 'libre');

INSERT INTO productos (nombre, descripcion, precio, categoria) VALUES
('Pan con tomate', 'Pan artesano con tomate y aceite', 2.50, 'entrante'),
('Croquetas caseras', 'Media ración de croquetas de jamón', 6.00, 'entrante'),
('Ensalada mixta', 'Lechuga, tomate, cebolla y atún', 7.50, 'entrante'),
('Secreto ibérico', 'Con patatas fritas y pimientos', 16.00, 'principal'),
('Pollo a la brasa', 'Medio pollo con guarnición', 12.00, 'principal'),
('Merluza a la plancha', 'Con verduras al vapor', 14.50, 'principal'),
('Tiramisú', 'Casero, con bizcocho y mascarpone', 5.50, 'postre'),
('Brownie con helado', 'Brownie caliente con helado de vainilla', 5.00, 'postre'),
('Agua mineral', '50cl', 1.50, 'bebida'),
('Refresco', 'Coca-Cola, Fanta o Sprite', 2.50, 'bebida'),
('Vino de la casa', 'Copa de vino tinto o blanco', 3.00, 'bebida'),
('Cerveza', 'Caña o botella', 2.00, 'bebida');