CREATE OR ALTER PROCEDURE JB_INV_Obtener_ActualizarSecuencial
    @Tipo NVARCHAR(50),
    @NuevoSecuencial NVARCHAR(50) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @TempOutput TABLE (Prefijo NVARCHAR(50), UltimoValor INT);
    IF EXISTS (SELECT 1 FROM Secuencial WHERE Tipo = @Tipo)
    BEGIN
        UPDATE Secuencial
        SET ultimo_valor = ultimo_valor + 1
        OUTPUT INSERTED.Prefijo, INSERTED.Ultimo_Valor
        INTO @TempOutput
        WHERE Tipo = @Tipo;
        SELECT TOP 1 @NuevoSecuencial = CONCAT(Prefijo, '-', FORMAT(UltimoValor, '00000000'))
        FROM @TempOutput;
    END
    ELSE
    BEGIN
        INSERT INTO Secuencial (Prefijo, ultimo_valor, Tipo)
        VALUES (@Tipo, 1, @Tipo);

        SELECT @NuevoSecuencial = CONCAT(@Tipo, '-00000001')
        FROM Secuencial
        WHERE Tipo = @Tipo;
    END
END;