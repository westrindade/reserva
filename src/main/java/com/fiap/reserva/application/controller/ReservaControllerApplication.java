package com.fiap.reserva.application.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.fiap.reserva.application.service.ReservaService;
import com.fiap.reserva.domain.entity.Reserva;
import com.fiap.reserva.domain.exception.BusinessException;
import com.fiap.reserva.domain.vo.CnpjVo;
import com.fiap.reserva.domain.vo.EmailVo;
import com.fiap.spring.Controller.Dto.ReservaDto;

public class ReservaControllerApplication {
    private ReservaService service;

    public ReservaDto cadastrarReserva(ReservaDto reservaDto)throws BusinessException{
        final Reserva reservaEntity = service.cadastrarReserva(reservaDto.toEntity());
        
        return toReservaDTO(reservaEntity);
    }

    public ReservaDto alterarReserva(final ReservaDto reservaDto) throws BusinessException{
        final Reserva reservaEntity = service.alterarReserva(reservaDto.toEntity());
        
        return toReservaDTO(reservaEntity);
    }

    public ReservaDto cancelarReserva(final ReservaDto reservaDto) throws BusinessException{
        final Reserva reservaEntity = service.cancelarReserva(reservaDto.toEntity());

        return toReservaDTO(reservaEntity);
    }

    public ReservaDto baixarReserva(final ReservaDto reservaDto) throws BusinessException{
        final Reserva reservaEntity = service.baixarReserva(reservaDto.toEntity());

        return toReservaDTO(reservaEntity);
    }

    //Acho que nao devemos excluir apenas cancelar importante para metricas que nunca iremos usar kkk
    public void excluirReserva(final ReservaDto reservaDto) throws BusinessException{
        service.excluirReserva(reservaDto.toEntity());
    }

    public List<ReservaDto> getBuscarTodasReservaDoUsuarioPeloEmail(final String email) throws BusinessException {
        final List<Reserva> reservas = service.getBuscarTodasReservaDoUsuarioPeloEmail(new EmailVo(email));

        return reservas.stream()
                .map(this::toReservaDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaDto> getBuscarTodasRerservasRestaurantePeloCNPJ(final String cnpj) throws BusinessException {
        final List<Reserva> reservas = service.getBuscarTodasRerservasRestaurantePeloCNPJ(new CnpjVo(cnpj));

        return reservas.stream()
                .map(this::toReservaDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaDto> getBuscarTodas(final ReservaDto reservaDto) throws BusinessException {
        final List<Reserva> reservas = service.getBuscarTodasRerservas(reservaDto.toEntity());

        return reservas.stream()
                .map(this::toReservaDTO)
                .collect(Collectors.toList());
    }

    public ReservaDto getObter(final ReservaDto reservaDto) throws BusinessException {
        return toReservaDTO(service.getObter(reservaDto.toEntity()));
    }

    private ReservaDto toReservaDTO(final Reserva reservaEntity) {
        return new ReservaDto(
            reservaEntity.getNumeroReserva(),
            reservaEntity.getUsuario().getEmailString(), 
            reservaEntity.getRestaurante().getCnpjString(),
            reservaEntity.getDataHora(), 
            reservaEntity.getSituacao()
        );
    }
}
