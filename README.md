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
**Antipatrón identificado:** God Object y Spaghetti Code combinados. GestorPedidos contiene un método procesarPedido() de aproximadamente 105 líneas que concentra múltiples responsabilidades independientes, lo que proporciona una primera señal del problema. Las responsabilidades reunidas en GestorPedidos, las cuales respaldan a GestorPedidos como un God Object son: Validacion de Stock (mezclada con acceso a la BD) en la linea 32-44; validacion de cliente y mora con excepcion por horario en la linea 47-65; Calculo de subtotal en la linea 68 a la 73; calculo de descuento en la linea 76-93; persistencia directa de pedidos via JDBC, en la linea 99-113; notificacion, contruye el mensaje y delega el envio a emailService, en la linea 116-133. Ademas, resaltar que todas las responsabilidades menos la notificación accedian directamente a la BD.

En cuanto a Spaghetti code, se puede encontrar en la seccion responsable a calculo de descuento, un nivel de anidamiento condicional de 2; en la seccion responsable de validacion de cliente y mora, se encuentra un nivel de anidamiento de 3.

procesarPedido() mezcla distintos niveles de abstracción en una misma secuencia: realiza operaciones de infraestructura mediante JdbcTemplate y SQL, toma decisiones propias del dominio como la validación de stock y las reglas de descuento, y finalmente construye directamente el texto de una notificación mediante StringBuilder. Por ejemplo, una consulta SQL, una condición de negocio y la construcción del correo aparecen como pasos consecutivos dentro del mismo método.

Actualmente, si se necesitara incorporar un nuevo tipo de cliente con reglas de descuento propias, sería necesario modificar el bloque de cálculo de descuento de procesarPedido(), actualmente ubicado aproximadamente entre las líneas 76 y 93. Habría que añadir otra rama condicional al conjunto if/else if que permita distinguir entre VIP y FRECUENTE, haciendo que la clase existente deba cambiar cada vez que aparezca un nuevo tipo de descuento.

**Patrón aplicado:** Se utilizó **Chain of Responsibility** para las validaciones porque existe una dependencia real de orden y de corte anticipado: el pedido debe superar primero la validación de stock y, solo si esta tiene éxito, continuar con la validación del cliente y su deuda. Cada validador puede decidir si rechaza el pedido o delega al siguiente, evitando ejecutar validaciones innecesarias. Se descartó una lista de predicados booleanos porque, aunque permitiría organizar las condiciones, no representa explícitamente la delegación entre validadores ni permite que cada uno controle el avance de la cadena.

Para el cálculo de descuentos se utilizó **Strategy**, ya que las reglas son alternativas determinadas por el tipo de cliente y no dependen de un orden de ejecución. El selector elige una única estrategia (`VIP`, `FRECUENTE` o `ESTANDAR`) y delega en ella el cálculo correspondiente. Se descartó incorporar estas reglas a la cadena de validación porque el descuento no es una validación secuencial ni requiere corte del flujo; hacerlo introduciría una responsabilidad distinta dentro de la cadena y obligaría a utilizar la delegación para un problema que puede resolverse mediante selección directa de una estrategia.


