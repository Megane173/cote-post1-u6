# Post-contenido — Unidad 6: Antipatrones de Diseño

## Descripción
Repositorio del post-contenido de la Unidad 6 de Patrones de Diseño
de Software — Sexto Semestre. Un único proyecto Spring Boot
(pedidos-service/) con dos partes: diagnóstico y refactorización de
un antipatrón combinado en GestorPedidos, y diagnóstico y corrección
de un segundo antipatrón introducido al hacer crecer el mismo
proyecto con tres campañas de descuento.

## Decisiones de diseño

### Parte 1 — GestorPedidos
**Antipatrón identificado:** God Object y Spaghetti Code combinados. GestorPedidos contiene un método procesarPedido() de aproximadamente 105 líneas que concentra múltiples responsabilidades independientes, lo que proporciona una primera señal del problema. Las responsabilidades reunidas en GestorPedidos, las cuales respaldan a GestorPedidos como un God Object son: Validacion de Stock (mezclada con acceso a la BD) en la linea 32-44; validacion de cliente y mora con excepcion por horario en la linea 47-65; Cálculo de subtotal en la linea 68 a la 73; cálculo de descuento en la línea 76-93; persistencia directa de pedidos via JDBC, en la linea 99-113; notificacion, contruye el mensaje y delega el envio a emailService, en la línea 116-133. Ademas, resaltar que todas las responsabilidades menos la notificación accedian directamente a la BD.

En cuanto a Spaghetti code, se puede encontrar en la sección responsable a cálculo de descuento, un nivel de anidamiento condicional de 2; en la sección responsable de validacion de cliente y mora, se encuentra un nivel de anidamiento de 3.

procesarPedido() mezcla distintos niveles de abstracción en una misma secuencia: realiza operaciones de infraestructura mediante JdbcTemplate y SQL, toma decisiones propias del dominio como la validación de stock y las reglas de descuento, y finalmente construye directamente el texto de una notificación mediante StringBuilder. Por ejemplo, una consulta SQL, una condición de negocio y la construcción del correo aparecen como pasos consecutivos dentro del mismo método.

Actualmente, si se necesitara incorporar un nuevo tipo de cliente con reglas de descuento propias, sería necesario modificar el bloque de cálculo de descuento de procesarPedido(), actualmente ubicado aproximadamente entre las líneas 76 y 93. Habría que añadir otra rama condicional al conjunto if/else if que permita distinguir entre VIP y FRECUENTE, haciendo que la clase existente deba cambiar cada vez que aparezca un nuevo tipo de descuento.

**Patrón aplicado:** Se utilizó **Chain of Responsibility** para las validaciones porque existe una dependencia real de orden y de corte anticipado: el pedido debe superar primero la validación de stock y, solo si esta tiene éxito, continuar con la validación del cliente y su deuda. Cada validador puede decidir si rechaza el pedido o delega al siguiente, evitando ejecutar validaciones innecesarias. Se descartó una lista de predicados booleanos porque, aunque permitiría organizar las condiciones, no representa explícitamente la delegación entre validadores ni permite que cada uno controle el avance de la cadena.

Para el cálculo de descuentos se utilizó **Strategy**, ya que las reglas son alternativas determinadas por el tipo de cliente y no dependen de un orden de ejecución. El selector elige una única estrategia (VIP, FRECUENTE o ESTANDAR) y delega en ella el cálculo correspondiente. Se descartó incorporar estas reglas a la cadena de validación porque el descuento no es una validación secuencial ni requiere corte del flujo; hacerlo introduciría una responsabilidad distinta dentro de la cadena y obligaría a utilizar la delegación para un problema que puede resolverse mediante selección directa de una estrategia.

### Parte 2 — Crecimiento del proyecto
**Antipatrón identificado:** El problema de diseño de la Parte 2 corresponde al antipatrón Golden Hammer: se reutilizó Chain of Responsibility para implementar las nuevas promociones simplemente porque ya había sido utilizado con éxito para las validaciones de la Parte 1, aunque las características del nuevo problema son diferentes.

La evidencia principal está en GestorPedidos, donde las tres promociones se incorporan directamente a la cadena mediante:

this.primerValidador = stock.encadenar(cliente)
    .encadenar(blackFriday)
    .encadenar(corporativo)
    .encadenar(volumen);

Sin embargo, PromocionBlackFriday, PromocionCorporativo y PromocionVolumen no tienen una dependencia de orden entre sí ni realizan corte anticipado. Las tres ejecutan su lógica independientemente y ninguna rechaza el pedido. Esto contrasta con ValidadorStock y ValidadorCliente, donde sí existe una razón para delegar secuencialmente: un pedido rechazado por stock no debe continuar hacia la validación del cliente.

Además, las promociones no cumplen conceptualmente el contrato de un ValidadorPedido. Por ejemplo, PromocionBlackFriday únicamente modifica el contexto mediante aplicarDescuentoCampana(0.25) y nunca decide si el pedido puede continuar. PromocionCorporativo consulta el NIT y también modifica el descuento, mientras que PromocionVolumen calcula las unidades del pedido y modifica el mismo campo. Por tanto, estas clases utilizan la cadena como mecanismo para acceder al contexto y producir un valor, en lugar de utilizarla para validar y decidir si delegan al siguiente eslabón.

Si dos campañas necesitaran combinarse, la cadena podria adaptarse para eso, pero Chain of Responsibility no define como responsabilidad de sus eslabones producir valores ni establece cómo deben combinarse los valores producidos por varios eslabones. Sería necesario introducir una regla adicional, como sumar los descuentos, tomar el mayor o aplicar una prioridad. En el código actual esta decisión se implementa mediante el estado compartido descuentoCampana y Math.max(). Esto evidencia que la cadena está funcionando principalmente como un mecanismo de ejecucion secuenciasl para ejecutar varias reglas de descuento, no como una verdadera cadena de validaciones.

