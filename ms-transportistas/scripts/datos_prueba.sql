-- Script de datos de prueba para la tabla de transportistas
-- Ejecutar este script después de iniciar el microservicio

-- Limpiar datos existentes (opcional)
-- DELETE FROM transportista;

-- Insertar transportistas de prueba
INSERT INTO transportista (id, nombre, patente, telefono, disponible) VALUES 
  (UUID(), 'Juan Pérez García', 'ABC-1234', '1234567890', true),
  (UUID(), 'Carlos López Martínez', 'DEF-5678', '0987654321', true),
  (UUID(), 'María González López', 'GHI-9012', '5555555555', true),
  (UUID(), 'Roberto Hernández Ruiz', 'JKL-3456', '4444444444', false),
  (UUID(), 'Ana Martínez Gómez', 'MNO-7890', '3333333333', true),
  (UUID(), 'Luis Rodríguez Flores', 'PQR-1234', '2222222222', true),
  (UUID(), 'Carmen Sánchez Torres', 'STU-5678', '1111111111', false),
  (UUID(), 'Pedro Díaz Castro', 'VWX-9012', '6666666666', true);

-- Verificar datos insertados
SELECT COUNT(*) as total_transportistas FROM transportista;
SELECT * FROM transportista;
