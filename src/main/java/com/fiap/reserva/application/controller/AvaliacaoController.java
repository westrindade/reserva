package com.fiap.reserva.application.controller;
import java.util.List;

import com.fiap.reserva.application.usecase.avaliacao.BuscarAvaliacaoPorRestaurante;
import com.fiap.reserva.application.usecase.avaliacao.DeixarAvaliação;
import com.fiap.reserva.application.usecase.restaurante.BuscarRestaurante;
import com.fiap.reserva.application.usecase.usuario.BuscarUsuario;
import com.fiap.reserva.domain.entity.Avaliacao;
import com.fiap.reserva.domain.entity.Restaurante;
import com.fiap.reserva.domain.entity.Usuario;
import com.fiap.reserva.domain.exception.BusinessException;
import com.fiap.reserva.domain.repository.AvaliacaoRepository;
import com.fiap.reserva.domain.repository.RestauranteRepository;
import com.fiap.reserva.domain.repository.UsuarioRepository;
import com.fiap.reserva.domain.vo.CnpjVo;
import com.fiap.reserva.domain.vo.EmailVo;

public class AvaliacaoController {

    private final AvaliacaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository restauranteRepository;

    public AvaliacaoController(AvaliacaoRepository avaliacaoRepository, UsuarioRepository usuarioRepository, RestauranteRepository restauranteRepository) {
        this.repository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public void avaliar(final String email, final String cnpj, final int pontuacao, final String comentario){
        final Avaliacao avaliacao = construirAvaliacao(email,cnpj,pontuacao,comentario);

        new DeixarAvaliação(repository).executar(avaliacao);
    }

    public List<Avaliacao> getBuscarTodos(final String cnpj) throws BusinessException{
        final Restaurante restaurante = new BuscarRestaurante(restauranteRepository).getRestaurantePor(new CnpjVo(cnpj));
        return new BuscarAvaliacaoPorRestaurante(repository).todasAvaliacoes(restaurante);
    }

    private Avaliacao construirAvaliacao(final String email, final String cnpj, final int pontuacao, final String comentario) {
        try {
            final Usuario usuario = new BuscarUsuario(usuarioRepository).getUsuarioPor(new EmailVo(email));
            final Restaurante restaurante =  new BuscarRestaurante(restauranteRepository).getRestaurantePor(new CnpjVo(cnpj));
            return new Avaliacao(usuario,restaurante,pontuacao,comentario);
        } catch (BusinessException e) {
            System.err.println("Erro de negócios: " + e.getMessage());

            return null;
        }
    }
}
