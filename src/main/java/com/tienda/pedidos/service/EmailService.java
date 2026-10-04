package com.tienda.pedidos.service;

public interface EmailService{

    public void enviar(
        String pedidoRequest,
        String asunto,
        String cuerpo
    );


}
