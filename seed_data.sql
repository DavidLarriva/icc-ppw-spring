-- ============================================================
-- Carga masiva de datos de prueba para la Práctica 10 (paginación).
--
-- Inserta 300 productos de prueba asociados al primer usuario activo
-- y los vincula a la primera categoría activa (tabla intermedia
-- product_categories, relación ManyToMany de la Práctica 09).
--
-- Ejecutar desde la carpeta donde está este archivo:
--   docker exec -i postgres-dev psql -U ups -d devdb < seed_data.sql
-- ============================================================

-- 1) 300 productos de prueba para el primer usuario activo
INSERT INTO products (name, price, stock, deleted, created_at, user_id)
SELECT
    'Producto Seed ' || g,
    round((random() * 2000)::numeric, 2),
    (random() * 100)::int,
    false,
    now(),
    (SELECT id FROM users WHERE deleted = false ORDER BY id LIMIT 1)
FROM generate_series(1, 300) AS g;

-- 2) Asociar cada producto seed a la primera categoría activa
INSERT INTO product_categories (product_id, category_id)
SELECT
    p.id,
    (SELECT id FROM categories WHERE deleted = false ORDER BY id LIMIT 1)
FROM products p
WHERE p.name LIKE 'Producto Seed %'
  AND NOT EXISTS (
      SELECT 1 FROM product_categories pc WHERE pc.product_id = p.id
  );

-- 3) Asociar los productos seed de id par también a la segunda categoría activa
--    (si existe una segunda categoría), para probar el ManyToMany real
INSERT INTO product_categories (product_id, category_id)
SELECT
    p.id,
    (SELECT id FROM categories WHERE deleted = false ORDER BY id OFFSET 1 LIMIT 1)
FROM products p
WHERE p.name LIKE 'Producto Seed %'
  AND p.id % 2 = 0
  AND (SELECT COUNT(*) FROM categories WHERE deleted = false) > 1
  AND NOT EXISTS (
      SELECT 1 FROM product_categories pc
      WHERE pc.product_id = p.id
        AND pc.category_id = (SELECT id FROM categories WHERE deleted = false ORDER BY id OFFSET 1 LIMIT 1)
  );
