package com.tienda.pedidos.service;

import org.springframework.stereotype.Service;

@Service 
public class ConsoleEmailService implements EmailService{


    @Override 
    public void enviar(
        String pedidoRequest,
        String asunto, 
        String cuerpo
    ){

        System.out.println("Para: "+pedidoRequest+"\n"+
            "Asunto: "+asunto+"\n"+
            "Cuerpo: "+cuerpo
        );
    }
}