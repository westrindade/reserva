package com.fiap.spring.Controller.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.reserva.domain.entity.Reserva;
import com.fiap.reserva.domain.entity.Restaurante;
import com.fiap.reserva.domain.entity.SituacaoReserva;
import com.fiap.reserva.domain.entity.Usuario;
import com.fiap.reserva.domain.exception.BusinessException;

public record ReservaDto(
    UUID numeroReserva,
    String emailUsuario,
    String cnpjRestaurante,
    LocalDateTime dataHora,
    SituacaoReserva statusReserva       
) {
    public Reserva toEntity() throws BusinessException{
        return new Reserva(
            numeroReserva,
            new Usuario(emailUsuario), 
            new Restaurante(cnpjRestaurante), 
            dataHora, 
            statusReserva
        );
    }
}
