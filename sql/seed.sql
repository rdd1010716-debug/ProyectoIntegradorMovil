-- ============================================================
-- CHOSTITO MOBILE - SEED DATA (Datos de prueba)
-- ============================================================
-- Ejecutar despues de schema.sql
-- ============================================================

-- 1. CATEGORIAS
-- ============================================================
INSERT INTO public.categorias (nombre, descripcion, icono) VALUES
('Conciertos', 'Musica en vivo y festivales', ''),
('Teatro', 'Obras de teatro y dramaturgia', ''),
('Deportes', 'Eventos deportivos y competiciones', ''),
('Festivales', 'Festivales culturales y gastronomicos', ''),
('Cine', 'Estrenos y maratones cinematograficos', '');

-- 2. LUGARES
-- ============================================================
INSERT INTO public.lugares (nombre, direccion, ciudad, pais) VALUES
('Teatro Municipal', 'Calle Sucre 123', 'La Paz', 'Bolivia'),
('Estadio Hernando Siles', 'Av. Venezuela s/n', 'La Paz', 'Bolivia'),
('Cine Multicine', 'Prado 456', 'La Paz', 'Bolivia'),
('Monumental Stadium', 'Av. Cristobal de Mendoza', 'Santa Cruz', 'Bolivia'),
('Casa de la Cultura', 'Calle Bolivar 789', 'Cochabamba', 'Bolivia'),
('Centro de Convenciones', 'Av. Amaral 321', 'La Paz', 'Bolivia');

-- 3. USUARIOS DE PRUEBA (registrados manualmente)
-- ============================================================
-- NOTA: Estos usuarios deben crearse via Supabase Auth primero
-- Luego asignar roles manualmente en la tabla perfiles
-- 
-- Usuarios recomendados:
-- Admin: admin@chostito.com / Admin123
-- Organizador: org@chostito.com / Organizador123
-- Cliente: cliente@chostito.com / Cliente123
-- 
-- Para crear estos usuarios, usa la API de Supabase Auth o el panel
-- y luego actualiza el rol en la tabla perfiles:
-- 
-- UPDATE public.perfiles SET rol = 'Admin' WHERE email = 'admin@chostito.com';
-- UPDATE public.perfiles SET rol = 'Organizador' WHERE email = 'org@chostito.com';
-- UPDATE public.perfiles SET rol = 'Cliente' WHERE email = 'cliente@chostito.com';

-- 4. EVENTOS
-- ============================================================
-- Evento 1: Concierto
INSERT INTO public.eventos (id_organizador, id_categoria, id_lugar, titulo, eslogan, descripcion, fecha, hora, imagen_url, estado, precio_minimo)
VALUES (
    (SELECT id FROM public.perfiles WHERE rol = 'Organizador' LIMIT 1),
    1, -- Conciertos
    1, -- Teatro Municipal
    'Coldplay - Music of the Spheres',
    'Una experiencia cosmica de musica',
    'Coldplay regresa a Bolivia con su gira mundial Music of the Spheres. Un show inolvidable con luces, efectos especiales y las mejores canciones de la banda.',
    '2026-07-15',
    '20:00',
    'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=800&auto=format&fit=crop',
    'Publicado',
    150
);

-- Evento 2: Teatro
INSERT INTO public.eventos (id_organizador, id_categoria, id_lugar, titulo, eslogan, descripcion, fecha, hora, imagen_url, estado, precio_minimo)
VALUES (
    (SELECT id FROM public.perfiles WHERE rol = 'Organizador' LIMIT 1),
    2, -- Teatro
    1, -- Teatro Municipal
    'Romeo y Julieta',
    'El clasico de Shakespeare en escena',
    'La tragedia mas grande de todos los tiempos vuelve al Teatro Municipal. Una produccion moderna con actores locales e internacionales.',
    '2026-06-20',
    '19:30',
    'https://images.unsplash.com/photo-1503095396547-776806279b73?w=800&auto=format&fit=crop',
    'Publicado',
    80
);

-- Evento 3: Deportes
INSERT INTO public.eventos (id_organizador, id_categoria, id_lugar, titulo, eslogan, descripcion, fecha, hora, imagen_url, estado, precio_minimo)
VALUES (
    (SELECT id FROM public.perfiles WHERE rol = 'Organizador' LIMIT 1),
    3, -- Deportes
    2, -- Estadio Hernando Siles
    'Bolivia vs Argentina - Eliminatorias',
    'La altura es nuestra ventaja',
    'Partido crucial de las eliminatorias sudamericanas para la Copa del Mundo 2026. Bolivia recibe a la campeona del mundo en La Paz.',
    '2026-09-05',
    '16:00',
    'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800&auto=format&fit=crop',
    'Publicado',
    200
);

-- Evento 4: Festival
INSERT INTO public.eventos (id_organizador, id_categoria, id_lugar, titulo, eslogan, descripcion, fecha, hora, imagen_url, estado, precio_minimo)
VALUES (
    (SELECT id FROM public.perfiles WHERE rol = 'Organizador' LIMIT 1),
    4, -- Festivales
    6, -- Centro de Convenciones
    'Festival Gastronomico La Paz',
    'Sabores de todo el mundo en un solo lugar',
    'El festival gastronomico mas grande de Bolivia. Mas de 50 restaurantes, food trucks, degustaciones, talleres de cocina y shows en vivo.',
    '2026-08-10',
    '11:00',
    'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800&auto=format&fit=crop',
    'Publicado',
    50
);

-- Evento 5: Cine
INSERT INTO public.eventos (id_organizador, id_categoria, id_lugar, titulo, eslogan, descripcion, fecha, hora, imagen_url, estado, precio_minimo)
VALUES (
    (SELECT id FROM public.perfiles WHERE rol = 'Organizador' LIMIT 1),
    5, -- Cine
    3, -- Cine Multicine
    'Maraton Marvel: Fase 6',
    'Toda la saga en una noche epica',
    'Maraton de 12 horas con las mejores peliculas de la Fase 6 del Universo Cinematografico Marvel. Incluye descansos, comida y merchandising.',
    '2026-06-28',
    '14:00',
    'https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=800&auto=format&fit=crop',
    'Publicado',
    60
);

-- Evento 6: Concierto (Borrador)
INSERT INTO public.eventos (id_organizador, id_categoria, id_lugar, titulo, eslogan, descripcion, fecha, hora, imagen_url, estado, precio_minimo)
VALUES (
    (SELECT id FROM public.perfiles WHERE rol = 'Organizador' LIMIT 1),
    1, -- Conciertos
    4, -- Monumental Stadium
    'Bad Bunny - Most Wanted Tour',
    'El conejo malo llega a Bolivia',
    'Bad Bunny presenta su nueva gira Most Wanted Tour en Santa Cruz. Un show espectacular con todos sus exitos.',
    '2026-10-20',
    '21:00',
    'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=800&auto=format&fit=crop',
    'Borrador',
    300
);

-- 5. ENTRADAS (para cada evento)
-- ============================================================
-- Evento 1: Coldplay
INSERT INTO public.entradas (id_evento, tipo, precio, cantidad_total, cantidad_disponible) VALUES
(1, 'General', 150, 500, 500),
(1, 'VIP', 350, 100, 100),
(1, 'Platinum', 500, 50, 50);

-- Evento 2: Romeo y Julieta
INSERT INTO public.entradas (id_evento, tipo, precio, cantidad_total, cantidad_disponible) VALUES
(2, 'General', 80, 300, 300),
(2, 'VIP', 180, 50, 50);

-- Evento 3: Bolivia vs Argentina
INSERT INTO public.entradas (id_evento, tipo, precio, cantidad_total, cantidad_disponible) VALUES
(3, 'Norte', 200, 2000, 2000),
(3, 'Sur', 250, 3000, 3000),
(3, 'Preferencia', 400, 1000, 1000),
(3, 'VIP Box', 800, 100, 100);

-- Evento 4: Festival Gastronomico
INSERT INTO public.entradas (id_evento, tipo, precio, cantidad_total, cantidad_disponible) VALUES
(4, 'Pase General', 50, 1000, 1000),
(4, 'Pase VIP', 120, 200, 200);

-- Evento 5: Maraton Marvel
INSERT INTO public.entradas (id_evento, tipo, precio, cantidad_total, cantidad_disponible) VALUES
(5, 'General', 60, 200, 200),
(5, 'VIP', 120, 30, 30);

-- Evento 6: Bad Bunny (Borrador - sin stock aun)
INSERT INTO public.entradas (id_evento, tipo, precio, cantidad_total, cantidad_disponible) VALUES
(6, 'General', 300, 5000, 5000),
(6, 'VIP', 600, 500, 500);

-- 6. ASIENTOS VIP (solo para eventos que tienen VIP)
-- ============================================================
-- Evento 1: Coldplay VIP - 100 asientos
INSERT INTO public.asientos (id_entrada, seccion, numero, estado)
SELECT 
    (SELECT id FROM public.entradas WHERE id_evento = 1 AND tipo = 'VIP'),
    'Zona A',
    'A' || i,
    'Activa'
FROM generate_series(1, 25) AS i;

INSERT INTO public.asientos (id_entrada, seccion, numero, estado)
SELECT 
    (SELECT id FROM public.entradas WHERE id_evento = 1 AND tipo = 'VIP'),
    'Zona B',
    'B' || i,
    'Activa'
FROM generate_series(1, 25) AS i;

INSERT INTO public.asientos (id_entrada, seccion, numero, estado)
SELECT 
    (SELECT id FROM public.entradas WHERE id_evento = 1 AND tipo = 'VIP'),
    'Zona C',
    'C' || i,
    'Activa'
FROM generate_series(1, 25) AS i;

INSERT INTO public.asientos (id_entrada, seccion, numero, estado)
SELECT 
    (SELECT id FROM public.entradas WHERE id_evento = 1 AND tipo = 'VIP'),
    'Zona D',
    'D' || i,
    'Activa'
FROM generate_series(1, 25) AS i;

-- Evento 2: Teatro VIP - 50 asientos
INSERT INTO public.asientos (id_entrada, seccion, numero, estado)
SELECT 
    (SELECT id FROM public.entradas WHERE id_evento = 2 AND tipo = 'VIP'),
    'Platea',
    LPAD(i::TEXT, 2, '0'),
    'Activa'
FROM generate_series(1, 50) AS i;

-- Evento 3: VIP Box - 100 asientos
INSERT INTO public.asientos (id_entrada, seccion, numero, estado)
SELECT 
    (SELECT id FROM public.entradas WHERE id_evento = 3 AND tipo = 'VIP Box'),
    'Box Norte',
    'BX' || i,
    'Activa'
FROM generate_series(1, 50) AS i;

INSERT INTO public.asientos (id_entrada, seccion, numero, estado)
SELECT 
    (SELECT id FROM public.entradas WHERE id_evento = 3 AND tipo = 'VIP Box'),
    'Box Sur',
    'BY' || i,
    'Activa'
FROM generate_series(1, 50) AS i;

-- 7. USUARIOS DE PRUEBA (instrucciones)
-- ============================================================
-- Para que la app sea 100% funcional, crea estos usuarios en Supabase Auth:
--
-- 1. Ve a Authentication -> Users -> Add User
-- 2. Crea estos 3 usuarios con sus emails y contraseñas:
--
--    Email: admin@chostito.com
--    Password: Admin123!
--    Metadata: {"nombre": "Administrador", "rol": "Admin"}
--
--    Email: organizador@chostito.com
--    Password: Organizador123!
--    Metadata: {"nombre": "Organizador Principal", "rol": "Organizador"}
--
--    Email: cliente@chostito.com
--    Password: Cliente123!
--    Metadata: {"nombre": "Cliente Demo", "rol": "Cliente"}
--
-- 3. El trigger handle_new_user creara automaticamente los perfiles
--
-- Si los perfiles se crearon sin rol correcto, ejecuta:
--
-- UPDATE public.perfiles SET rol = 'Admin' WHERE email = 'admin@chostito.com';
-- UPDATE public.perfiles SET rol = 'Organizador' WHERE email = 'organizador@chostito.com';
-- UPDATE public.perfiles SET rol = 'Cliente' WHERE email = 'cliente@chostito.com';

-- 8. FAVORITOS DE PRUEBA
-- ============================================================
-- (Opcional) Para el usuario cliente:
-- INSERT INTO public.favoritos (id_usuario, id_evento) VALUES
-- ((SELECT id FROM public.perfiles WHERE email = 'cliente@chostito.com'), 1),
-- ((SELECT id FROM public.perfiles WHERE email = 'cliente@chostito.com'), 3);

-- ============================================================
-- LISTO! La base de datos tiene datos de prueba funcionales.
-- ============================================================
