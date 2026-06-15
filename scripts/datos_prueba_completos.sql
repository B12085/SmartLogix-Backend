-- ============================================
-- DATOS DE PRUEBA - SmartLogix (BD Compartida)
-- ============================================
-- Base de datos única: smartlogix
-- Tablas: producto, pedido, transportista

-- ============================================
-- 1. TABLA PRODUCTO (ms-logistics-base)
-- ============================================
INSERT INTO producto (id, sku, nombre, descripcion, cantidad_stock, precio) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'PROD-001', 'Laptop Dell Inspiron', 'Laptop 15 pulgadas Intel i7', 15, 899.99),
('550e8400-e29b-41d4-a716-446655440002', 'PROD-002', 'Monitor Samsung 27"', 'Monitor 4K UltraHD', 25, 349.99),
('550e8400-e29b-41d4-a716-446655440003', 'PROD-003', 'Mouse Logitech MX', 'Mouse inalámbrico profesional', 50, 79.99),
('550e8400-e29b-41d4-a716-446655440004', 'PROD-004', 'Teclado Mecánico', 'Teclado RGB con switches Cherry', 30, 159.99),
('550e8400-e29b-41d4-a716-446655440005', 'PROD-005', 'Webcam HD 1080p', 'Cámara web con micrófono', 40, 49.99),
('550e8400-e29b-41d4-a716-446655440006', 'PROD-006', 'Cable HDMI 3m', 'Cable HDMI 2.1 de alta velocidad', 100, 15.99),
('550e8400-e29b-41d4-a716-446655440007', 'PROD-007', 'SSD 512GB NVMe', 'Disco estado sólido Samsung 970', 20, 69.99),
('550e8400-e29b-41d4-a716-446655440008', 'PROD-008', 'Hub USB 7 puertos', 'Distribuidor USB 3.0', 35, 39.99);

-- ============================================
-- 2. TABLA PEDIDO (ms-pedidos)
-- ============================================
INSERT INTO pedido (numero_pedido, cliente, descripcion, sku_producto, cantidad_solicitada, estado, fecha) VALUES
('PED-001', 'Juan García', 'Pedido de laptop para oficina', 'PROD-001', 2, 'PENDIENTE', NOW()),
('PED-002', 'María López', 'Pedido de monitores para desarrollo', 'PROD-002', 3, 'CONFIRMADO', NOW()),
('PED-003', 'Carlos Rodríguez', 'Pedido de accesorios varios', 'PROD-003', 5, 'PROCESANDO', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('PED-004', 'Ana Martínez', 'Pedido de teclados mecánicos', 'PROD-004', 4, 'ENVIADO', DATE_SUB(NOW(), INTERVAL 5 DAY)),
('PED-005', 'Pedro González', 'Pedido de webcams', 'PROD-005', 2, 'ENTREGADO', DATE_SUB(NOW(), INTERVAL 10 DAY)),
('PED-006', 'Laura Sánchez', 'Pedido de cables HDMI', 'PROD-006', 10, 'PENDIENTE', NOW()),
('PED-007', 'Jorge Hernández', 'Pedido de SSDs para upgrades', 'PROD-007', 3, 'CONFIRMADO', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('PED-008', 'Sofia Torres', 'Pedido de hubs USB', 'PROD-008', 2, 'PROCESANDO', NOW());

-- ============================================
-- 3. TABLA TRANSPORTISTA (ms-transportistas)
-- ============================================
INSERT INTO transportista (id, nombre, patente, telefono, disponible) VALUES
(UUID(), 'Juan Pérez López', 'ABC-1234', '1234567890', TRUE),
(UUID(), 'María García Rodríguez', 'XYZ-9876', '9876543210', TRUE),
(UUID(), 'Carlos Martínez Soto', 'DEF-5678', '5555555555', TRUE),
(UUID(), 'Ana López Fernández', 'GHI-1111', '1111111111', FALSE),
(UUID(), 'Pedro González Castro', 'JKL-2222', '2222222222', TRUE),
(UUID(), 'Rosa Díaz Moreno', 'MNO-3333', '3333333333', TRUE),
(UUID(), 'Miguel Ramírez Vega', 'PQR-4444', '4444444444', FALSE),
(UUID(), 'Carmen Flores Santos', 'STU-5555', '5555555556', TRUE);

-- ============================================
-- VERIFICACIÓN DE DATOS
-- ============================================
-- Cantidad de productos
-- SELECT COUNT(*) as total_productos FROM producto;

-- Cantidad de pedidos
-- SELECT COUNT(*) as total_pedidos FROM pedido;

-- Cantidad de transportistas disponibles
-- SELECT COUNT(*) as transportistas_disponibles FROM transportista WHERE disponible = TRUE;

-- ============================================
-- INFORMACIÓN ÚTIL
-- ============================================
-- Para usar una contraseña diferente:
-- SET @password = 'tu_contraseña';
-- UPDATE producto SET ... WHERE ...

-- Para limpiar datos (CUIDADO - esto elimina todo):
-- DELETE FROM pedido;
-- DELETE FROM producto;
-- DELETE FROM transportista;

-- Para resetear auto-increment (si es necesario):
-- ALTER TABLE pedido AUTO_INCREMENT = 1;
