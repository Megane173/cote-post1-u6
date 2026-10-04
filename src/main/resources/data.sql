INSERT INTO clientes (id, tipo_cliente) VALUES
(1, 'NORMAL'),
(2, 'MOROSO'),
(3, 'VIP'),
(4, 'FRECUENTE');

INSERT INTO productos (id, precio) VALUES
(1, 100000);

INSERT INTO inventario (producto_id, stock) VALUES
(1, 100);

INSERT INTO facturas (cliente_id, monto, pagada) VALUES
(2, 500000, false);

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');

INSERT INTO pedidos
(cliente_id, subtotal, descuento, impuesto, total, fecha, estado)
VALUES
(4, 100000, 0, 19000, 119000, CURRENT_TIMESTAMP, 'CONFIRMADO');