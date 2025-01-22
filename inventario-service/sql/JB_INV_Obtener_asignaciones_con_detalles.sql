CREATE OR ALTER PROCEDURE JB_INV_Obtener_asignaciones_con_detalles
    @filtro_articulo NVARCHAR(255) = NULL, 
    @filtro_usuario NVARCHAR(255) = NULL,
    @offset INT = 0,                      
    @limit INT = 10                       
AS
BEGIN
    SELECT 
        a.id AS idArticulo,
        a.codigo_interno AS codigoInterno,
        a.codigo_origen AS codigoOrigen,
		a.nombre AS nombreArticulo,
        aa.fecha_asignacion AS fechaAsignacion,
        aa.id_usuario AS idUsuario,
        aa.tipo_relacion AS tipoRelacion,
        CASE 
            WHEN aa.tipo_relacion = 'USUARIO' THEN u.nombre_completo
            WHEN aa.tipo_relacion = 'AREA' THEN ar.nombre_area
            ELSE NULL
        END AS nombre_asignado,
		a.estado as estadoArticulo
    FROM 
        facci_inventario..articulo a
    LEFT JOIN 
        facci_inventario..articulo_asignacion aa ON a.id = aa.articulo_id
    LEFT JOIN 
        facci_configuracion..usuario u ON aa.id_usuario = u.id AND aa.tipo_relacion = 'USUARIO'
    LEFT JOIN 
        facci_configuracion..area ar ON aa.id_usuario = ar.id AND aa.tipo_relacion = 'AREA'
    WHERE 
		(NULLIF(@filtro_articulo, '') IS NULL 
		 OR a.nombre LIKE '%' + @filtro_articulo + '%' 
		 OR a.codigo_origen LIKE '%' + @filtro_articulo + '%')
		AND 
		(NULLIF(@filtro_usuario, '') IS NULL 
		 OR (aa.tipo_relacion = 'USUARIO' AND u.nombre_completo LIKE '%' + @filtro_usuario + '%')
		 OR (aa.tipo_relacion = 'AREA' AND ar.nombre_area LIKE '%' + @filtro_usuario + '%'))
    ORDER BY 
        a.id 
    OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY;
END;
