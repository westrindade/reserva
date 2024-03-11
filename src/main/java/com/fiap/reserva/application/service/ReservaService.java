package com.fiap.reserva.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.fiap.reserva.application.usecase.reserva.AlterarReservaRestaurante;
import com.fiap.reserva.application.usecase.reserva.BuscarReservaRestaurante;
import com.fiap.reserva.application.usecase.reserva.BuscarReservaUsuario;
import com.fiap.reserva.application.usecase.reserva.CadastrarReserva;
import com.fiap.reserva.application.usecase.reserva.CancelarReservaRestaurante;
import com.fiap.reserva.application.usecase.reserva.ConcluirReservaRestaurante;
import com.fiap.reserva.domain.entity.Reserva;
import com.fiap.reserva.domain.entity.Restaurante;
import com.fiap.reserva.domain.entity.SituacaoReserva;
import com.fiap.reserva.domain.entity.Usuario;
import com.fiap.reserva.domain.exception.BusinessException;
import com.fiap.reserva.domain.exception.EntidadeNaoEncontrada;
import com.fiap.reserva.domain.repository.ReservaRepository;
import com.fiap.reserva.domain.vo.CnpjVo;
import com.fiap.reserva.domain.vo.EmailVo;

public class ReservaService {
    private static final int RESTAURANTE_SEM_RESERVAS_DISPONIVEIS = 0;
	private final ReservaRepository repository;
    private final RestauranteService restauranteService;
    private final UsuarioService usuarioService;

    public ReservaService(ReservaRepository repository, 
    		RestauranteService restauranteService,
			UsuarioService usuarioService) {
		this.repository = repository;
		this.restauranteService = restauranteService;
		this.usuarioService = usuarioService;
	}

    public Reserva criarReserva(final Reserva reserva) throws BusinessException{
    	if(reserva == null) {
    		throw new BusinessException("Informe uma reserva para ser cadastrada");
    	}
    	
    	validarSeRestaurantePossuiMesasDisponiveis(reserva);
        
    	return new CadastrarReserva(repository).executar(reserva);
    }

	private void validarSeRestaurantePossuiMesasDisponiveis(final Reserva reserva) throws BusinessException {
		final Stream<Reserva> reservasDoRestaurante = new BuscarReservaRestaurante(repository)
			.executar(reserva.getRestaurante())
			.stream()
			.filter(r -> r.getSituacao() == SituacaoReserva.RESERVADO);
    	
        final long totalReservasRestaurante = reservasDoRestaurante.count();
    	final int lotacaoRestaurante = restauranteService.obterLocacaoMaxRestaurante(reserva.getRestaurante());
        
        if (lotacaoRestaurante == 0 ||((lotacaoRestaurante - totalReservasRestaurante) <= RESTAURANTE_SEM_RESERVAS_DISPONIVEIS)){
            throw new BusinessException("Não existe disponibilidade para este dia");
        }
	}

	public Reserva alterarReserva(final Reserva reserva) throws BusinessException{
        return new AlterarReservaRestaurante(repository).executar(reserva);
    }

    public void cancelarReserva(final Reserva reserva) throws BusinessException{
        new CancelarReservaRestaurante(repository).executar(reserva);
    }
    
    public void concluirReserva(final Reserva reserva) throws BusinessException{
        new ConcluirReservaRestaurante(repository).executar(reserva);
    }

    public List<Reserva> getBuscarTodasReservaDoUsuarioPeloEmail(final EmailVo email) throws BusinessException{
        final Usuario usuario = usuarioService.getBuscarPor(new Usuario(email.getEndereco()));
        
        if(usuario == null) {
        	throw new EntidadeNaoEncontrada("Usuario não encontrado");
        }
        
        return new BuscarReservaUsuario(repository).executar(usuario);
    }

    public List<Reserva> getBuscarTodasRerservasRestaurantePeloCNPJ(final CnpjVo cnpj) throws BusinessException{
        final Restaurante restaurante = restauranteService.getBuscarPor(cnpj);
        
        if(restaurante == null) {
        	throw new EntidadeNaoEncontrada("Restaurante não encontrado");
        }
        
        return new BuscarReservaRestaurante(repository).executar(restaurante);
    }

	public List<Reserva> getBuscarReservasDoRestaurantePorSituacao(CnpjVo cnpjVo, SituacaoReserva situacaoReserva) throws BusinessException {
		return getBuscarTodasRerservasRestaurantePeloCNPJ(cnpjVo).stream().filter(r -> situacaoReserva == r.getSituacao()).toList();
	}
	
	public List<Reserva> getBuscarReservasDoUsuarioPorSituacao(EmailVo emailVo, SituacaoReserva situacaoReserva) throws BusinessException {
		return getBuscarTodasReservaDoUsuarioPeloEmail(emailVo).stream().filter(r -> situacaoReserva == r.getSituacao()).toList();
	}

	public Reserva getBuscarReservaPeloNumeroReserva(UUID numeroReserva) throws BusinessException {
		final Reserva reserva = repository.buscarPor(numeroReserva);
		
		if(reserva == null) {
			throw new EntidadeNaoEncontrada("Reserva não encontrada");
		}
		
		return reserva;
	}
}
