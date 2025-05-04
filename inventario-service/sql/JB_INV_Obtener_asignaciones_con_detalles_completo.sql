USE [facci_inventario]
GO
/****** Object:  StoredProcedure [dbo].[JB_INV_Obtener_asignaciones_con_detalles_completo]    Script Date: 05/03/25 23:19:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE or ALTER PROCEDURE [dbo].[JB_INV_Obtener_asignaciones_con_detalles_completo]
    @filtro_usuario NVARCHAR(255) = NULL,
    @estado NVARCHAR(50) = NULL,
    @grupo_activo NVARCHAR(100) = NULL,
    @nombre NVARCHAR(255) = NULL,
    @marca NVARCHAR(100) = NULL,
    @edificio NVARCHAR(100) = NULL,
    @seccion NVARCHAR(100) = NULL,
    @offset INT = 0,
    @limit INT = 10
AS
BEGIN
    SELECT 
	    aa.id_usuario AS idUsuario,
        CASE 
            WHEN aa.tipo_relacion = 'USUARIO' THEN u.nombre_completo
            WHEN aa.tipo_relacion = 'AREA' THEN ar.nombre_area
            ELSE NULL
        END AS nombre_asignado,
		a.id AS idArticulo,
		a.codigo_interno AS codigoInterno,
        a.codigo_origen AS codigoOrigen,
		aa.fecha_asignacion AS fechaAsignacion,
		aa.tipo_relacion AS tipoRelacion,
        a.estado as estadoArticulo,
		a.nombre AS nombreArticulo,
        a.marca as marcaArticulo,
		a.serie as serieArticulo,
		a.modelo as modeloArticulo,
        a.ubicacion as ubicacionArticulo,
        a.seccion as seccionArticulo,
        ga.codigo as grupoActivo,
		a.descripcion as descripcion,
		a.modificado_fecha as fechaEstado
    FROM 
        facci_inventario..articulo a
    LEFT JOIN 
        facci_inventario..articulo_asignacion aa ON a.id = aa.articulo_id
    LEFT JOIN 
        facci_configuracion..usuario u ON aa.id_usuario = u.id AND aa.tipo_relacion = 'USUARIO'
    LEFT JOIN 
        facci_configuracion..area ar ON aa.id_usuario = ar.id AND aa.tipo_relacion = 'AREA'
    LEFT JOIN
        facci_inventario..grupo_activo ga ON a.grupo_activo_id = ga.id
    WHERE 
        (NULLIF(@filtro_usuario, '') IS NULL 
         OR (aa.tipo_relacion = 'USUARIO' AND u.nombre_completo LIKE '%' + @filtro_usuario + '%')
         OR (aa.tipo_relacion = 'AREA' AND ar.nombre_area LIKE '%' + @filtro_usuario + '%'))
        AND
        (NULLIF(@estado, '') IS NULL OR a.estado = @estado)
        AND
        (NULLIF(@grupo_activo, '') IS NULL OR a.grupo_activo_id = @grupo_activo)
        AND
        (NULLIF(@nombre, '') IS NULL OR a.nombre LIKE '%' + @nombre + '%')
        AND
        (NULLIF(@marca, '') IS NULL OR a.marca = @marca)
        AND
        (NULLIF(@edificio, '') IS NULL OR a.ubicacion LIKE '%' + @edificio + '%')
        AND
        (NULLIF(@seccion, '') IS NULL OR a.seccion = @seccion)
    ORDER BY 
        a.nombre asc
    OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY;
END;