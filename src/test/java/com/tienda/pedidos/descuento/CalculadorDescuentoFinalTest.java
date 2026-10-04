package com.tienda.pedidos.descuento;

import com.tienda.pedidos.dto.ItemPedido;
import com.tienda.pedidos.dto.PedidoRequest;
import com.tienda.pedidos.validacion.ContextoPedido;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CalculadorDescuentoFinalTest {

	@Test
	void debeAplicarBlackFriday() {
		SelectorEstrategiaDescuento selector = mock(SelectorEstrategiaDescuento.class);
		EstrategiaDescuento descuentoCliente = mock(EstrategiaDescuento.class);

		when(selector.seleccionar(anyString()))
		.thenReturn(descuentoCliente);

		when(descuentoCliente.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		DescuentoBlackFriday blackFriday =
		    new DescuentoBlackFriday(true);

		DescuentoCorporativo corporativo =
		    mock(DescuentoCorporativo.class);

		DescuentoVolumen volumen =
		    mock(DescuentoVolumen.class);

		when(corporativo.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		when(volumen.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		CalculadorDescuentoFinal calculador =
		    new CalculadorDescuentoFinal(
		    selector,
		    blackFriday,
		    corporativo,
		    volumen
		);

		double resultado =
		    calculador.calcular(crearContexto("ESTANDAR", 1));

		assertEquals(0.25, resultado);
	}

	@Test
	void debeAplicarDescuentoCorporativo() {
		SelectorEstrategiaDescuento selector = mock(SelectorEstrategiaDescuento.class);
		EstrategiaDescuento descuentoCliente = mock(EstrategiaDescuento.class);

		when(selector.seleccionar(anyString()))
		.thenReturn(descuentoCliente);

		when(descuentoCliente.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		DescuentoBlackFriday blackFriday =
		    mock(DescuentoBlackFriday.class);

		DescuentoCorporativo corporativo =
		    mock(DescuentoCorporativo.class);

		DescuentoVolumen volumen =
		    mock(DescuentoVolumen.class);

		when(blackFriday.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		when(corporativo.calcular(any(ContextoPedido.class)))
		.thenReturn(0.10);

		when(volumen.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		CalculadorDescuentoFinal calculador =
		    new CalculadorDescuentoFinal(
		    selector,
		    blackFriday,
		    corporativo,
		    volumen
		);

		double resultado =
		    calculador.calcular(crearContexto("ESTANDAR", 1));

		assertEquals(0.10, resultado);
	}

	@Test
	void debeAplicarDescuentoPorVolumen() {
		SelectorEstrategiaDescuento selector = mock(SelectorEstrategiaDescuento.class);
		EstrategiaDescuento descuentoCliente = mock(EstrategiaDescuento.class);

		when(selector.seleccionar(anyString()))
		.thenReturn(descuentoCliente);

		when(descuentoCliente.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		DescuentoBlackFriday blackFriday =
		    mock(DescuentoBlackFriday.class);

		DescuentoCorporativo corporativo =
		    mock(DescuentoCorporativo.class);

		DescuentoVolumen volumen =
		    mock(DescuentoVolumen.class);

		when(blackFriday.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		when(corporativo.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		when(volumen.calcular(any(ContextoPedido.class)))
		.thenReturn(0.12);

		CalculadorDescuentoFinal calculador =
		    new CalculadorDescuentoFinal(
		    selector,
		    blackFriday,
		    corporativo,
		    volumen
		);

		double resultado =
		    calculador.calcular(crearContexto("ESTANDAR", 21));

		assertEquals(0.12, resultado);
	}

	@Test
	void debeConservarDescuentoMayorEntreClienteYCampana() {
		SelectorEstrategiaDescuento selector = mock(SelectorEstrategiaDescuento.class);
		EstrategiaDescuento descuentoCliente = mock(EstrategiaDescuento.class);

		when(selector.seleccionar(anyString()))
		.thenReturn(descuentoCliente);

		// Cliente VIP obtiene 15 %
		when(descuentoCliente.calcular(any(ContextoPedido.class)))
		.thenReturn(0.15);

		DescuentoBlackFriday blackFriday =
		    mock(DescuentoBlackFriday.class);

		DescuentoCorporativo corporativo =
		    mock(DescuentoCorporativo.class);

		DescuentoVolumen volumen =
		    mock(DescuentoVolumen.class);

		// Black Friday ofrece 25 %
		when(blackFriday.calcular(any(ContextoPedido.class)))
		.thenReturn(0.25);

		when(corporativo.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		when(volumen.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		CalculadorDescuentoFinal calculador =
		    new CalculadorDescuentoFinal(
		    selector,
		    blackFriday,
		    corporativo,
		    volumen
		);

		double resultado =
		    calculador.calcular(crearContexto("VIP", 1));

		assertEquals(0.25, resultado);
	}

	@Test
	void debeConservarDescuentoDelClienteCuandoEsMayorQueLasCampanas() {
		SelectorEstrategiaDescuento selector = mock(SelectorEstrategiaDescuento.class);
		EstrategiaDescuento descuentoCliente = mock(EstrategiaDescuento.class);

		when(selector.seleccionar(anyString()))
		.thenReturn(descuentoCliente);

		// Descuento del cliente: 15 %
		when(descuentoCliente.calcular(any(ContextoPedido.class)))
		.thenReturn(0.15);

		DescuentoBlackFriday blackFriday =
		    mock(DescuentoBlackFriday.class);

		DescuentoCorporativo corporativo =
		    mock(DescuentoCorporativo.class);

		DescuentoVolumen volumen =
		    mock(DescuentoVolumen.class);

		when(blackFriday.calcular(any(ContextoPedido.class)))
		.thenReturn(0.0);

		when(corporativo.calcular(any(ContextoPedido.class)))
		.thenReturn(0.10);

		when(volumen.calcular(any(ContextoPedido.class)))
		.thenReturn(0.12);

		CalculadorDescuentoFinal calculador =
		    new CalculadorDescuentoFinal(
		    selector,
		    blackFriday,
		    corporativo,
		    volumen
		);

		double resultado =
		    calculador.calcular(crearContexto("VIP", 21));

		assertEquals(0.15, resultado);
	}

	private ContextoPedido crearContexto(
	    String tipoCliente,
	    int cantidad) {

		PedidoRequest request = new PedidoRequest();

		request.setClienteId(1L);

		request.setItems(List.of(
		                     new ItemPedido(1L, cantidad)
		                 ));

		ContextoPedido contexto =
		    new ContextoPedido(request);

		contexto.setTipoCliente(tipoCliente);

		return contexto;
	}
}