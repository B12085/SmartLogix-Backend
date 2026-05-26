-- ============================================
-- Script de inicialización de datos de prueba
-- Microservicio de Inventario - SmartLogix
-- ============================================

-- Crear la base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS smartlogix_inventario;
USE smartlogix_inventario;

-- Insertar productos de prueba
INSERT INTO productos (codigo_sku, nombre, descripcion, cantidad_stock, ubicacion_bodega, precio)
VALUES
  ('SKU001', 'Laptop Dell XPS 13', 'Laptop ultraportátil con procesador Intel i7, 16GB RAM, SSD 512GB', 50, 'A-01-01', 1500.00),
  ('SKU002', 'Monitor LG 27 4K', 'Monitor IPS de 27 pulgadas con resolución 4K', 30, 'B-05-02', 450.00),
  ('SKU003', 'Teclado Mecánico RGB', 'Teclado mecánico con retroiluminación RGB personalizable', 120, 'C-03-01', 150.00),
  ('SKU004', 'Mouse Logitech MX', 'Mouse inalámbrico de precisión con batería recargable', 200, 'C-02-03', 99.99),
  ('SKU005', 'Auriculares Sony WH-1000', 'Auriculares inalámbricos con cancelación de ruido', 45, 'D-01-02', 350.00),
  ('SKU006', 'Hub USB-C', 'Hub USB-C con 7 puertos, incluye cargador rápido', 85, 'E-04-01', 79.99),
  ('SKU007', 'Cable HDMI 2.1', 'Cable HDMI 2.1 de 3 metros para 4K@120Hz', 250, 'F-02-02', 25.00),
  ('SKU008', 'Base para Laptop', 'Base refrigerada para laptop con 2 ventiladores', 60, 'A-06-01', 45.50);

-- Verificar los datos insertados
SELECT * FROM productos;

-- Ejemplo: Consultar un producto específico
-- SELECT * FROM productos WHERE codigo_sku = 'SKU001';

-- Ejemplo: Ver el stock total de todos los productos
-- SELECT SUM(cantidad_stock) as stock_total FROM productos;

-- Ejemplo: Productos con precio menor a $100
-- SELECT * FROM productos WHERE precio < 100 ORDER BY precio DESC;

-- Ejemplo: Productos por ubicación de bodega
-- SELECT ubicacion_bodega, COUNT(*) as cantidad_productos FROM productos GROUP BY ubicacion_bodega;
