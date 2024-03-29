package com.fiap.reserva.application.usecase.usuario;

import com.fiap.reserva.domain.entity.Usuario;
import com.fiap.reserva.domain.exception.BusinessException;
import com.fiap.reserva.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuscarUsuarioTest {
    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private BuscarUsuario buscarUsuario;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void deveRetornarUsuarioBuscandoPorEmail() throws BusinessException {
        Usuario usuario = new Usuario("Matheus", "teste@teste.com");
        when(repository.buscarPor(usuario.getEmail())).thenReturn(usuario);

        final Usuario usuarioEsperado = buscarUsuario.getUsuario(usuario.getEmail());
        assertNotNull(usuarioEsperado);
        assertEquals(usuario, usuarioEsperado);
        verify(repository).buscarPor(usuario.getEmail());
    }

    @Test
    void naoDeveRetornarUsuarioBuscandoPorNull() {
        final Throwable throwable = assertThrows(BusinessException.class, () -> buscarUsuario.getUsuario(null));
        assertEquals("Email é obrigatorio para realizar a busca!", throwable.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void deveRetornarNullBuscandoPorUsuarioInexistente() throws BusinessException {
        Usuario usuario = new Usuario("Matheus", "teste@teste.com");
        when(repository.buscarPor(usuario.getEmail())).thenReturn(usuario);

        Usuario usuarioInexistente = new Usuario("Matheus2", "teste2@teste.com");

        final Usuario usuarioEsperado = buscarUsuario.getUsuario(usuarioInexistente.getEmail());
        assertNull(usuarioEsperado);
        verify(repository).buscarPor(usuarioInexistente.getEmail());
    }
}