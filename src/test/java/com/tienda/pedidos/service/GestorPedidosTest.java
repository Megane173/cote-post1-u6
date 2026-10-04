package com.tienda.pedidos.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tienda.pedidos.descuento.CalculadorDescuentoFinal;
import com.tienda.pedidos.descuento.EstrategiaDescuento;
import com.tienda.pedidos.descuento.SelectorEstrategiaDescuento;
import com.tienda.pedidos.dto.ItemPedido;
import com.tienda.pedidos.dto.PedidoRequest;
import com.tienda.pedidos.dto.ResultadoPedido;
import com.tienda.pedidos.validacion.ContextoPedido;
import com.tienda.pedidos.validacion.ValidadorCliente;
import com.tienda.pedidos.validacion.ValidadorPedido;
import com.tienda.pedidos.validacion.ValidadorStock;

@ExtendWith(MockitoExtension.class)
class GestorPedidosTest {

	@Mock
	private ValidadorStock stock;

	@Mock
	private ValidadorCliente cliente;

	@Mock
	private ValidadorPedido primerValidador;

	@Mock
	private CalculadorDescuentoFinal calculadorDescuento;

	@Mock
	private EstrategiaDescuento estrategia;

	@Mock
	private PedidoRepository repository;

	@Mock
	private NotificacionPedidoService notificacion;

	private GestorPedidos gestorPedidos;

	@BeforeEach
	void setUp() {
		when(stock.encadenar(cliente)).thenReturn(primerValidador);

		gestorPedidos = new GestorPedidos(
		    stock,
		    cliente,
		    calculadorDescuento,
		    repository,
		    notificacion
		);
	}

	@Test
	void debeRechazarPedidoCuandoLaValidacionFalla() {
		PedidoRequest request = crearPedido();

		doAnswer(invocation -> {
			ContextoPedido contexto = invocation.getArgument(0);
			contexto.rechazar("Stock insuficiente");
			return null;
		}).when(primerValidador).validar(any(ContextoPedido.class));

		ResultadoPedido resultado =
		    gestorPedidos.procesarPedido(request);

		assertFalse(resultado.isConfirmado());
		assertEquals(
		    "Stock insuficiente",
		    resultado.getMotivoRechazo()
		);

		verify(primerValidador)
		.validar(any(ContextoPedido.class));

		verifyNoInteractions(
		    calculadorDescuento,
		    repository,
		    notificacion
		);
	}

	@Test
	void debeConfirmarPedidoYGuardarYNotificar() {
		PedidoRequest request = crearPedido();

		doAnswer(invocation -> {
			ContextoPedido contexto = invocation.getArgument(0);
			contexto.setTipoCliente("VIP");
			return null;
		}).when(primerValidador).validar(any(ContextoPedido.class));

		when(calculadorDescuento.calcular(any(ContextoPedido.class)))
		.thenReturn(0.10);

		when(repository.guardar(
		         any(ContextoPedido.class),
		         eq(0.10),
		         anyDouble(),
		         anyDouble()
		     )).thenReturn(1L);

		ResultadoPedido resultado =
		    gestorPedidos.procesarPedido(request);

		assertTrue(resultado.isConfirmado());
		assertEquals(1L, resultado.getPedidoId());

		verify(primerValidador)
		.validar(any(ContextoPedido.class));

		verify(calculadorDescuento)
		.calcular(any(ContextoPedido.class));

		verify(repository).guardar(
		    any(ContextoPedido.class),
		    eq(0.10),
		    anyDouble(),
		    anyDouble()
		);

		verify(notificacion).notificarConfirmacion(
		    any(ContextoPedido.class),
		    eq(1L),
		    eq(0.10),
		    anyDouble(),
		    anyDouble()
		);
	}

	@Test
	void debeCalcularElSubtotalAntesDeAplicarElDescuento() {
		PedidoRequest request = crearPedido();

		doAnswer(invocation -> {
			ContextoPedido contexto = invocation.getArgument(0);
			contexto.setTipoCliente("VIP");
			return null;
		}).when(primerValidador).validar(any(ContextoPedido.class));

		when(repository.obtenerPrecio(1L))
		.thenReturn(100_000.0);

		when(calculadorDescuento.calcular(any(ContextoPedido.class)))
		.thenAnswer(invocation -> {
			ContextoPedido contexto =
			invocation.getArgument(0);

			assertEquals(
			    200_000.0,
			    contexto.getSubtotal()
			);

			return 0.10;
		});

		when(repository.guardar(
		         any(ContextoPedido.class),
		         eq(0.10),
		         anyDouble(),
		         anyDouble()
		     )).thenReturn(1L);

		gestorPedidos.procesarPedido(request);

		verify(repository)
		.obtenerPrecio(1L);

		verify(calculadorDescuento)
		.calcular(any(ContextoPedido.class));
	}

	private PedidoRequest crearPedido() {
		PedidoRequest request = new PedidoRequest();

		request.setClienteId(3L);
		request.setClienteEmail("cliente@correo.com");

		request.setItems(List.of(
		                     new ItemPedido(1L, 2)
		                 ));

		return request;
        }
}
