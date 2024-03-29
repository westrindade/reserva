package com.fiap.reserva.application.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fiap.reserva.application.service.ReservaService;
import com.fiap.reserva.domain.entity.Reserva;
import com.fiap.reserva.domain.entity.SituacaoReserva;
import com.fiap.reserva.domain.exception.BusinessException;
import com.fiap.reserva.domain.vo.CnpjVo;
import com.fiap.reserva.domain.vo.EmailVo;
import com.fiap.spring.Controller.Dto.ReservaDto;

public class ReservaControllerApplication {
    private final ReservaService service;

    public ReservaControllerApplication(ReservaService service) {
		this.service = service;
	}

	public ReservaDto criarReserva(ReservaDto reservaDto)throws BusinessException{
        final Reserva reservaEntity = service.criarReserva(reservaDto.toEntity());
        
        return toDTO(reservaEntity);
    }

    public void cancelarReserva(final UUID numeroReserva) throws BusinessException{
        service.cancelarReserva(numeroReserva);
    }
    
    public void concluirReserva(final UUID numeroReserva) throws BusinessException{
    	service.concluirReserva(numeroReserva);
    }

    public List<ReservaDto> getBuscarReservasDoUsuarioPeloEmail(final String email) throws BusinessException {
        final List<Reserva> reservas = service.getBuscarTodasReservaDoUsuarioPeloEmail(new EmailVo(email));

        return reservas.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ReservaDto> getBuscarTodasRerservasRestaurantePeloCNPJ(final String cnpj) throws BusinessException {
        final List<Reserva> reservas = service.getBuscarTodasRerservasRestaurantePeloCNPJ(new CnpjVo(cnpj));

        return reservas.stream()
                .map(this::toDTO)
                .toList();
    }

    ReservaDto toDTO(final Reserva reservaEntity) {
    	if(reservaEntity == null) {
    		return null;
    	}
    	
        return new ReservaDto(
            reservaEntity.getNumeroReserva(),
            reservaEntity.getUsuario().getEmailString(), 
            reservaEntity.getRestaurante().getCnpjString(),
            reservaEntity.getDataHora(), 
            reservaEntity.getSituacao()
        );
    }

	public List<ReservaDto> getBuscarReservasDoRestaurantePorSituacao(String cnpj, SituacaoReserva situacaoReserva) throws BusinessException  {
		return service.getBuscarReservasDoRestaurantePorSituacao(new CnpjVo(cnpj), situacaoReserva)
			.stream()
			.map(this::toDTO)
			.toList();
	}

	public List<ReservaDto> getBuscarReservasDoUsuarioPelaSituacao(String emailUsuario, SituacaoReserva situacaoReserva) throws BusinessException {
		return service.getBuscarReservasDoUsuarioPorSituacao(new EmailVo(emailUsuario), situacaoReserva)
			.stream()
			.map(this::toDTO)
			.toList();
	}

	public ReservaDto getBuscarReservaPeloNumeroReserva(UUID numeroReserva) throws BusinessException {
		return toDTO(service.getBuscarReservaPeloNumeroReserva(numeroReserva));
	}

	public List<ReservaDto> getBuscarReservaDoRestaurantePeloData(String cnpjVo, LocalDate data) throws BusinessException {
		return getBuscarTodasRerservasRestaurantePeloCNPJ(cnpjVo)
			.stream()
			.filter(r -> r.dataHora().toLocalDate().isEqual(data))
			.toList();
	}
}
