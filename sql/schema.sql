-- ============================================================
-- CHOSTITO MOBILE - SCHEMA SQL PARA SUPABASE
-- ============================================================
-- Ejecutar todo este archivo en el SQL Editor de Supabase
-- ============================================================

-- Nota: uuid-ossp ya viene habilitado por defecto en Supabase
-- No es necesario crear la extension

-- 1. TABLAS
-- ============================================================

-- Perfiles (extiende auth.users de Supabase)
CREATE TABLE IF NOT EXISTS public.perfiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    nombre TEXT NOT NULL,
    telefono TEXT,
    rol TEXT NOT NULL CHECK (rol IN ('Cliente', 'Organizador', 'Admin')),
    foto_url TEXT,
    fecha_registro TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Categorias
CREATE TABLE IF NOT EXISTS public.categorias (
    id SERIAL PRIMARY KEY,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT,
    icono TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Lugares
CREATE TABLE IF NOT EXISTS public.lugares (
    id SERIAL PRIMARY KEY,
    nombre TEXT NOT NULL,
    direccion TEXT,
    ciudad TEXT NOT NULL,
    pais TEXT NOT NULL DEFAULT 'Bolivia',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Eventos
CREATE TABLE IF NOT EXISTS public.eventos (
    id SERIAL PRIMARY KEY,
    id_organizador UUID REFERENCES public.perfiles(id),
    id_categoria INTEGER REFERENCES public.categorias(id),
    id_lugar INTEGER REFERENCES public.lugares(id),
    titulo TEXT NOT NULL,
    eslogan TEXT,
    descripcion TEXT,
    fecha DATE NOT NULL,
    hora TIME,
    imagen_url TEXT,
    estado TEXT NOT NULL DEFAULT 'Borrador' CHECK (estado IN ('Publicado', 'Borrador', 'Cancelado', 'Finalizado')),
    precio_minimo NUMERIC(10,2) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Entradas (tipos de entrada por evento)
CREATE TABLE IF NOT EXISTS public.entradas (
    id SERIAL PRIMARY KEY,
    id_evento INTEGER REFERENCES public.eventos(id) ON DELETE CASCADE,
    tipo TEXT NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    cantidad_total INTEGER NOT NULL DEFAULT 0,
    cantidad_disponible INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Asientos (para entradas VIP)
CREATE TABLE IF NOT EXISTS public.asientos (
    id SERIAL PRIMARY KEY,
    id_entrada INTEGER REFERENCES public.entradas(id) ON DELETE CASCADE,
    seccion TEXT NOT NULL DEFAULT 'General',
    numero TEXT NOT NULL,
    estado TEXT NOT NULL DEFAULT 'Activa' CHECK (estado IN ('Activa', 'Ocupada', 'Reservada')),
    UNIQUE(id_entrada, numero)
);

-- Reservas
CREATE TABLE IF NOT EXISTS public.reservas (
    id SERIAL PRIMARY KEY,
    id_usuario UUID REFERENCES public.perfiles(id),
    fecha_reserva TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    estado TEXT NOT NULL DEFAULT 'Pendiente' CHECK (estado IN ('Pendiente', 'Confirmada', 'Cancelada')),
    total NUMERIC(10,2) DEFAULT 0,
    cantidad_entradas INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Reserva Items
CREATE TABLE IF NOT EXISTS public.reserva_items (
    id SERIAL PRIMARY KEY,
    id_reserva INTEGER REFERENCES public.reservas(id) ON DELETE CASCADE,
    id_entrada INTEGER REFERENCES public.entradas(id),
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL,
    id_asiento INTEGER REFERENCES public.asientos(id) ON DELETE SET NULL,
    UNIQUE(id_reserva, id_entrada, id_asiento)
);

-- Pagos
CREATE TABLE IF NOT EXISTS public.pagos (
    id SERIAL PRIMARY KEY,
    id_reserva INTEGER REFERENCES public.reservas(id) ON DELETE CASCADE,
    metodo_pago TEXT NOT NULL DEFAULT 'QR',
    codigo_transaccion TEXT UNIQUE,
    monto NUMERIC(10,2) NOT NULL,
    estado TEXT NOT NULL DEFAULT 'Pendiente' CHECK (estado IN ('Pendiente', 'Completado', 'Fallido')),
    fecha_pago TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Favoritos
CREATE TABLE IF NOT EXISTS public.favoritos (
    id SERIAL PRIMARY KEY,
    id_usuario UUID REFERENCES public.perfiles(id) ON DELETE CASCADE,
    id_evento INTEGER REFERENCES public.eventos(id) ON DELETE CASCADE,
    fecha_agregado TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(id_usuario, id_evento)
);

-- Entradas Vendidas (para validacion QR)
CREATE TABLE IF NOT EXISTS public.entradas_vendidas (
    id SERIAL PRIMARY KEY,
    id_reserva_item INTEGER REFERENCES public.reserva_items(id) ON DELETE CASCADE,
    codigo_qr TEXT NOT NULL UNIQUE,
    estado TEXT NOT NULL DEFAULT 'Activa' CHECK (estado IN ('Activa', 'Usada', 'Cancelada')),
    fecha_uso TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. ROW LEVEL SECURITY (RLS)
-- ============================================================
ALTER TABLE public.perfiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.categorias ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lugares ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.eventos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.entradas ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.asientos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reservas ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reserva_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.pagos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.favoritos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.entradas_vendidas ENABLE ROW LEVEL SECURITY;

-- Políticas básicas (permitir todo a usuarios autenticados para desarrollo)
-- Usamos DROP + CREATE porque PostgreSQL no soporta CREATE POLICY IF NOT EXISTS
DROP POLICY IF EXISTS "Allow all perfiles" ON public.perfiles;
CREATE POLICY "Allow all perfiles" ON public.perfiles FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all categorias" ON public.categorias;
CREATE POLICY "Allow all categorias" ON public.categorias FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all lugares" ON public.lugares;
CREATE POLICY "Allow all lugares" ON public.lugares FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all eventos" ON public.eventos;
CREATE POLICY "Allow all eventos" ON public.eventos FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all entradas" ON public.entradas;
CREATE POLICY "Allow all entradas" ON public.entradas FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all asientos" ON public.asientos;
CREATE POLICY "Allow all asientos" ON public.asientos FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all reservas" ON public.reservas;
CREATE POLICY "Allow all reservas" ON public.reservas FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all reserva_items" ON public.reserva_items;
CREATE POLICY "Allow all reserva_items" ON public.reserva_items FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all pagos" ON public.pagos;
CREATE POLICY "Allow all pagos" ON public.pagos FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all favoritos" ON public.favoritos;
CREATE POLICY "Allow all favoritos" ON public.favoritos FOR ALL USING (true);

DROP POLICY IF EXISTS "Allow all entradas_vendidas" ON public.entradas_vendidas;
CREATE POLICY "Allow all entradas_vendidas" ON public.entradas_vendidas FOR ALL USING (true);

-- 3. FUNCIONES (RPC)
-- ============================================================

-- Funcion: crear reserva con items
CREATE OR REPLACE FUNCTION public.crear_reserva(
    p_id_usuario UUID,
    p_items JSONB,
    p_ids_entradas INTEGER[] DEFAULT NULL
) RETURNS JSONB AS $$
DECLARE
    v_reserva_id INTEGER;
    v_item JSONB;
    v_entrada RECORD;
    v_asiento_id INTEGER;
    v_total NUMERIC := 0;
    v_cantidad_total INTEGER := 0;
BEGIN
    -- Crear reserva
    INSERT INTO public.reservas (id_usuario, estado, total, cantidad_entradas)
    VALUES (p_id_usuario, 'Pendiente', 0, 0)
    RETURNING id INTO v_reserva_id;

    -- Insertar items normales
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items)
    LOOP
        SELECT * INTO v_entrada FROM public.entradas WHERE id = (v_item->>'idEvento')::INTEGER;
        
        IF FOUND THEN
            INSERT INTO public.reserva_items (id_reserva, id_entrada, cantidad, precio_unitario)
            VALUES (v_reserva_id, v_entrada.id, (v_item->>'cantidad')::INTEGER, v_entrada.precio);
            
            v_total := v_total + (v_entrada.precio * (v_item->>'cantidad')::INTEGER);
            v_cantidad_total := v_cantidad_total + (v_item->>'cantidad')::INTEGER;
            
            -- Actualizar stock
            UPDATE public.entradas 
            SET cantidad_disponible = cantidad_disponible - (v_item->>'cantidad')::INTEGER
            WHERE id = v_entrada.id;
        END IF;
    END LOOP;

    -- Insertar items VIP (asientos)
    IF p_ids_entradas IS NOT NULL THEN
        FOR v_asiento_id IN SELECT unnest(p_ids_entradas)
        LOOP
            SELECT * INTO v_entrada FROM public.entradas e
            JOIN public.asientos a ON a.id_entrada = e.id
            WHERE a.id = v_asiento_id;
            
            IF FOUND THEN
                INSERT INTO public.reserva_items (id_reserva, id_entrada, cantidad, precio_unitario, id_asiento)
                VALUES (v_reserva_id, v_entrada.id, 1, v_entrada.precio, v_asiento_id);
                
                v_total := v_total + v_entrada.precio;
                v_cantidad_total := v_cantidad_total + 1;
                
                -- Marcar asiento ocupado
                UPDATE public.asientos SET estado = 'Ocupada' WHERE id = v_asiento_id;
                
                -- Actualizar stock entrada
                UPDATE public.entradas 
                SET cantidad_disponible = cantidad_disponible - 1
                WHERE id = v_entrada.id;
            END IF;
        END LOOP;
    END IF;

    -- Actualizar totales de reserva
    UPDATE public.reservas 
    SET total = v_total, cantidad_entradas = v_cantidad_total
    WHERE id = v_reserva_id;

    RETURN jsonb_build_object('id', v_reserva_id, 'total', v_total, 'cantidad_entradas', v_cantidad_total);
END;
$$ LANGUAGE plpgsql;

-- Funcion: simular pago
CREATE OR REPLACE FUNCTION public.simular_pago(
    p_reserva_id INTEGER,
    p_metodo TEXT DEFAULT 'QR'
) RETURNS JSONB AS $$
DECLARE
    v_reserva RECORD;
    v_codigo TEXT;
    v_item RECORD;
    v_codigo_qr TEXT;
BEGIN
    SELECT * INTO v_reserva FROM public.reservas WHERE id = p_reserva_id;
    IF NOT FOUND THEN
        RETURN jsonb_build_object('error', 'Reserva no encontrada');
    END IF;

    v_codigo := 'TRX-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(v_reserva.id::TEXT, 6, '0');

    -- Crear pago
    INSERT INTO public.pagos (id_reserva, metodo_pago, codigo_transaccion, monto, estado)
    VALUES (p_reserva_id, p_metodo, v_codigo, v_reserva.total, 'Completado');

    -- Confirmar reserva
    UPDATE public.reservas SET estado = 'Confirmada' WHERE id = p_reserva_id;

    -- Generar codigos QR para cada entrada
    FOR v_item IN SELECT * FROM public.reserva_items WHERE id_reserva = p_reserva_id
    LOOP
        v_codigo_qr := 'QR-' || v_reserva.id || '-' || v_item.id || '-' || MD5(RANDOM()::TEXT);
        INSERT INTO public.entradas_vendidas (id_reserva_item, codigo_qr, estado)
        VALUES (v_item.id, v_codigo_qr, 'Activa');
    END LOOP;

    RETURN jsonb_build_object('success', true, 'codigo_transaccion', v_codigo, 'monto', v_reserva.total);
END;
$$ LANGUAGE plpgsql;

-- Funcion: escanear QR
CREATE OR REPLACE FUNCTION public.escanear_qr(p_codigo TEXT)
RETURNS JSONB AS $$
DECLARE
    v_entrada RECORD;
BEGIN
    SELECT 
        ev.id,
        ev.codigo_qr,
        ev.estado,
        ev.fecha_uso,
        ri.id_entrada,
        e.tipo,
        ev2.titulo as evento,
        p.nombre as comprador,
        p.email as email_comprador,
        pa.codigo_transaccion
    INTO v_entrada
    FROM public.entradas_vendidas ev
    JOIN public.reserva_items ri ON ri.id = ev.id_reserva_item
    JOIN public.entradas e ON e.id = ri.id_entrada
    JOIN public.eventos ev2 ON ev2.id = e.id_evento
    JOIN public.reservas r ON r.id = ri.id_reserva
    JOIN public.perfiles p ON p.id = r.id_usuario
    LEFT JOIN public.pagos pa ON pa.id_reserva = r.id
    WHERE ev.codigo_qr = p_codigo;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('valido', false, 'mensaje', 'Entrada no encontrada');
    END IF;

    IF v_entrada.estado = 'Usada' THEN
        RETURN jsonb_build_object('valido', false, 'mensaje', 'Entrada ya fue utilizada', 'fecha_uso', v_entrada.fecha_uso);
    END IF;

    -- Marcar como usada
    UPDATE public.entradas_vendidas 
    SET estado = 'Usada', fecha_uso = NOW() 
    WHERE codigo_qr = p_codigo;

    RETURN jsonb_build_object(
        'valido', true,
        'tipo', v_entrada.tipo,
        'evento', v_entrada.evento,
        'comprador', v_entrada.comprador,
        'email_comprador', v_entrada.email_comprador,
        'codigo_transaccion', v_entrada.codigo_transaccion
    );
END;
$$ LANGUAGE plpgsql;

-- Funcion: dashboard stats
CREATE OR REPLACE FUNCTION public.get_dashboard_stats()
RETURNS JSONB AS $$
DECLARE
    v_total_eventos INTEGER;
    v_entradas_vendidas INTEGER;
    v_total_usuarios INTEGER;
    v_total_recaudado NUMERIC;
BEGIN
    SELECT COUNT(*) INTO v_total_eventos FROM public.eventos;
    SELECT COUNT(*) INTO v_entradas_vendidas FROM public.reserva_items;
    SELECT COUNT(*) INTO v_total_usuarios FROM public.perfiles WHERE rol = 'Cliente';
    SELECT COALESCE(SUM(monto), 0) INTO v_total_recaudado FROM public.pagos WHERE estado = 'Completado';

    RETURN jsonb_build_object(
        'total_eventos', v_total_eventos,
        'entradas_vendidas', v_entradas_vendidas,
        'total_usuarios', v_total_usuarios,
        'total_recaudado', v_total_recaudado
    );
END;
$$ LANGUAGE plpgsql;

-- Funcion: mis ventas (organizador)
CREATE OR REPLACE FUNCTION public.get_mis_ventas(p_id_organizador UUID)
RETURNS JSONB AS $$
BEGIN
    RETURN (
        SELECT jsonb_agg(jsonb_build_object(
            'id', e.id,
            'titulo', e.titulo,
            'fecha', e.fecha,
            'estado', e.estado,
            'imagen_url', e.imagen_url,
            'entradas_vendidas', COALESCE(vendidas.cnt, 0),
            'entradas_totales', COALESCE(totales.cnt, 0),
            'total_recaudado', COALESCE(recaudado.monto, 0)
        ))
        FROM public.eventos e
        LEFT JOIN (
            SELECT e2.id_evento, COUNT(*) as cnt
            FROM public.entradas e2
            JOIN public.reserva_items ri ON ri.id_entrada = e2.id
            JOIN public.reservas r ON r.id = ri.id_reserva
            WHERE r.estado = 'Confirmada'
            GROUP BY e2.id_evento
        ) vendidas ON vendidas.id_evento = e.id
        LEFT JOIN (
            SELECT id_evento, SUM(cantidad_total) as cnt
            FROM public.entradas
            GROUP BY id_evento
        ) totales ON totales.id_evento = e.id
        LEFT JOIN (
            SELECT e2.id_evento, SUM(p.monto) as monto
            FROM public.entradas e2
            JOIN public.reserva_items ri ON ri.id_entrada = e2.id
            JOIN public.reservas r ON r.id = ri.id_reserva
            JOIN public.pagos p ON p.id_reserva = r.id
            WHERE r.estado = 'Confirmada' AND p.estado = 'Completado'
            GROUP BY e2.id_evento
        ) recaudado ON recaudado.id_evento = e.id
        WHERE e.id_organizador = p_id_organizador
    );
END;
$$ LANGUAGE plpgsql;

-- Funcion: todas las ganancias (admin)
CREATE OR REPLACE FUNCTION public.get_todas_ganancias()
RETURNS JSONB AS $$
BEGIN
    RETURN (
        SELECT jsonb_agg(jsonb_build_object(
            'id', e.id,
            'titulo', e.titulo,
            'organizador', p.nombre,
            'entradas_vendidas', COALESCE(vendidas.cnt, 0),
            'total_recaudado', COALESCE(recaudado.monto, 0)
        ))
        FROM public.eventos e
        JOIN public.perfiles p ON p.id = e.id_organizador
        LEFT JOIN (
            SELECT e2.id_evento, COUNT(*) as cnt
            FROM public.entradas e2
            JOIN public.reserva_items ri ON ri.id_entrada = e2.id
            JOIN public.reservas r ON r.id = ri.id_reserva
            WHERE r.estado = 'Confirmada'
            GROUP BY e2.id_evento
        ) vendidas ON vendidas.id_evento = e.id
        LEFT JOIN (
            SELECT e2.id_evento, SUM(p.monto) as monto
            FROM public.entradas e2
            JOIN public.reserva_items ri ON ri.id_entrada = e2.id
            JOIN public.reservas r ON r.id = ri.id_reserva
            JOIN public.pagos p ON p.id_reserva = r.id
            WHERE r.estado = 'Confirmada' AND p.estado = 'Completado'
            GROUP BY e2.id_evento
        ) recaudado ON recaudado.id_evento = e.id
    );
END;
$$ LANGUAGE plpgsql;

-- Funcion: generar codigo QR para pago
CREATE OR REPLACE FUNCTION public.generar_qr_pago(p_reserva_id INTEGER)
RETURNS JSONB AS $$
DECLARE
    v_reserva RECORD;
    v_codigo TEXT;
BEGIN
    SELECT * INTO v_reserva FROM public.reservas WHERE id = p_reserva_id;
    IF NOT FOUND THEN
        RETURN jsonb_build_object('error', 'Reserva no encontrada');
    END IF;

    v_codigo := 'QR-PAY-' || v_reserva.id || '-' || TO_CHAR(NOW(), 'YYYYMMDD-HH24MISS');

    RETURN jsonb_build_object(
        'qr_data', v_codigo,
        'codigo_transaccion', v_codigo,
        'monto', v_reserva.total,
        'reserva_id', v_reserva.id
    );
END;
$$ LANGUAGE plpgsql;

-- Trigger: crear perfil automaticamente al registrarse
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.perfiles (id, email, nombre, rol, fecha_registro)
    VALUES (NEW.id, NEW.email, COALESCE(NEW.raw_user_meta_data->>'nombre', NEW.email), COALESCE(NEW.raw_user_meta_data->>'rol', 'Cliente'), NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Eliminar trigger si existe y recrearlo
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_new_user();
