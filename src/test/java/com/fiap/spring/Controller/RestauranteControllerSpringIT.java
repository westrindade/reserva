package com.fiap.spring.Controller;

import com.fiap.reserva.domain.entity.TipoCozinha;
import com.fiap.spring.Controller.Dto.EnderecoDto;
import com.fiap.spring.Controller.Dto.HorarioFuncionamentoDto;
import com.fiap.spring.Controller.Dto.RestauranteDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestauranteControllerSpringIT {
    @LocalServerPort
    private int porta;

    @BeforeEach
    void setUp() {
        RestAssured.port = porta;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CriarRestaurante {
        @Test
        void deveCriarRestaurante() {
            List<HorarioFuncionamentoDto> horariosFuncionamento = List.of(
                    new HorarioFuncionamentoDto(DayOfWeek.MONDAY, LocalDateTime.of(2023, 3, 15, 9, 0), LocalDateTime.of(2023, 3, 15, 13, 0)),
                    new HorarioFuncionamentoDto(DayOfWeek.TUESDAY, LocalDateTime.of(2023, 3, 16, 9, 0), LocalDateTime.of(2023, 3, 16, 13, 0))
            );
            final RestauranteDto restauranteDto = new RestauranteDto(
                "01771554000155",
                "Restaurante Criado",
                10,
                TipoCozinha.ITALIANA,
                horariosFuncionamento,
                new EnderecoDto("77816740", "Rua das Orquídeas", "123", "apto 301", "Jardim Pedra Alta", "Araguaína", "TO")
            );

            given()
                .body(restauranteDto)
                .contentType(ContentType.JSON)
                .when()
                    .post("/restaurante")
                .then()
                    .statusCode(HttpStatus.SC_CREATED);
        }


        @Test
        void naoDeveCriarRestauranteExistente() {
            List<HorarioFuncionamentoDto> horariosFuncionamento = List.of(
                    new HorarioFuncionamentoDto(DayOfWeek.MONDAY, LocalDateTime.of(2023, 3, 15, 9, 0), LocalDateTime.of(2023, 3, 15, 13, 0)),
                    new HorarioFuncionamentoDto(DayOfWeek.TUESDAY, LocalDateTime.of(2023, 3, 16, 9, 0), LocalDateTime.of(2023, 3, 16, 13, 0))
            );
            final RestauranteDto restauranteExistenteDto = new RestauranteDto(
                "95856819000161",
                "Restaurante Bidigaray",
                10,
                TipoCozinha.MEXICANA,
                horariosFuncionamento,
                new EnderecoDto("66816810", "Rua Peru", "273", "apto 301", "Pratinha", "Belém", "PA")
            );

            given()
                .contentType(ContentType.JSON)
                .body(restauranteExistenteDto)
                .when()
                .   post("/restaurante")
                .then()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", is("Restaurante não pode ser cadastrado, pois já existe"));
        }
    }

    @Nested
    class AlterarRestaurante {
        @Test
        void deveAlterarRestaurante() {
            List<HorarioFuncionamentoDto> horariosFuncionamento = List.of(
                    new HorarioFuncionamentoDto(DayOfWeek.MONDAY, LocalDateTime.of(2023, 3, 15, 9, 0), LocalDateTime.of(2023, 3, 15, 13, 0)),
                    new HorarioFuncionamentoDto(DayOfWeek.TUESDAY, LocalDateTime.of(2023, 3, 16, 9, 0), LocalDateTime.of(2023, 3, 16, 13, 0))
            );
            final RestauranteDto restauranteAtualizadoDto = new RestauranteDto(
                "95856819000161",
                "Restaurante Atualizado",
                20,
                TipoCozinha.ITALIANA,
                horariosFuncionamento,
                new EnderecoDto("66816810", "Rua Atualizada", "123", "apto 301", "Bairro Atualizado", "Cidade", "Estado")
            );

            given()
                .contentType(ContentType.JSON)
                .body(restauranteAtualizadoDto)
                .when()
                    .put("/restaurante")
                .then()
                    .statusCode(HttpStatus.SC_CREATED);
        }

        @Test
        void deveAlterarRestauranteComNovoHorarioFuncionamento() {
            List<HorarioFuncionamentoDto> horariosFuncionamentoAlterados = List.of(
                    new HorarioFuncionamentoDto(DayOfWeek.MONDAY, LocalDateTime.of(2023, 3, 15, 10, 0), LocalDateTime.of(2023, 3, 15, 14, 0)),
                    new HorarioFuncionamentoDto(DayOfWeek.TUESDAY, LocalDateTime.of(2023, 3, 16, 10, 0), LocalDateTime.of(2023, 3, 16, 14, 0))
            );
            RestauranteDto restauranteAtualizadoDto = new RestauranteDto(
                    "95856819000161",
                    "Restaurante Atualizado",
                    20,
                    TipoCozinha.ITALIANA,
                    horariosFuncionamentoAlterados,
                    new EnderecoDto("66816810", "Rua Atualizada", "123", "apto 301", "Bairro Atualizado", "Cidade", "Estado")
            );

            given()
                .contentType(ContentType.JSON)
                .body(restauranteAtualizadoDto)
                .when()
                    .put("/restaurante")
                .then()
                    .statusCode(HttpStatus.SC_CREATED)
                    .body("nome", equalTo(restauranteAtualizadoDto.nome()))
                    .body("horariosFuncionamento.size()", is(horariosFuncionamentoAlterados.size()));
        }

        @Test
        void naoDeveAlterarRestauranteInexistente() {
            List<HorarioFuncionamentoDto> horariosFuncionamento = List.of(
                    new HorarioFuncionamentoDto(DayOfWeek.MONDAY, LocalDateTime.of(2023, 3, 15, 9, 0), LocalDateTime.of(2023, 3, 15, 13, 0)),
                    new HorarioFuncionamentoDto(DayOfWeek.TUESDAY, LocalDateTime.of(2023, 3, 16, 9, 0), LocalDateTime.of(2023, 3, 16, 13, 0))
            );
            final RestauranteDto restauranteInexistenteDto = new RestauranteDto(
                "49279377000110",
                "Restaurante Inexistente",
                20,
                TipoCozinha.ITALIANA,
                horariosFuncionamento,
                new EnderecoDto("00000-000", "Rua Inexistente", "123", null, "Bairro Inexistente", "Cidade", "Estado")
            );

            given()
                .contentType(ContentType.JSON)
                .body(restauranteInexistenteDto)
                .when()
                    .put("/restaurante")
                .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Restaurante não pode ser alterado, pois não foi encontrado"));
        }
    }

    @Nested
    class ExcluirRestaurante {
        @Test
        void deveExcluirRestaurante() {
            final String cnpj = "64292159000100";

            given()
                .pathParam("cnpj", cnpj)
                .when()
                    .delete("/restaurante/{cnpj}", cnpj)
                .then()
                    .statusCode(HttpStatus.SC_NO_CONTENT);
        }

        @Test
        void naoDeveExcluirRestauranteInexistente() {
            final String cnpjInexistente = "12345678901000";

            given()
                .pathParam("cnpj", cnpjInexistente)
                .when()
                    .delete("/restaurante/{cnpj}")
                .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Restaurante não pode ser excluído, pois não foi encontrado"));
        }
    }

    @Nested
    class BuscarRestaurante {
        @Test
        void deveBuscarRestaurantePorCnpj() {
            final String cnpj = "95856819000161";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/{cnpj}", cnpj)
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("cnpj", equalTo(cnpj));
        }

        @Test
        void naoDeveEncontrarRestaurantePorCnpjInexistente() {
            final String cnpjInexistente = "47794381000191";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/{cnpj}", cnpjInexistente)
                .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Restaurante não encontrado"));
        }

        @Test
        void deveBuscarRestaurantePorNome() {
            final String nomeRestaurante  = "Restaurante Denis Benjamim";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/nome/{nome}", nomeRestaurante)
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("nome", equalTo(nomeRestaurante));
        }

        @Test
        void naoDeveEncontrarRestaurantePorNomeInexistente() {
            final String nomeInexistente = "Nome Inexistente";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/nome/{nome}", nomeInexistente)
                .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", equalTo("Restaurante não encontrado para o nome: " + nomeInexistente));
        }

        @Test
        void deveBuscarRestaurantePorTipoCozinha() {
            final String tipoCozinha = "JAPONESA";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/tipo-cozinha/{tipoCozinha}", tipoCozinha)
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("tipoCozinha", everyItem(equalTo(tipoCozinha)));
        }

        @Test
        void naoDeveEncontrarRestaurantePorTipoCozinhaInexistente() {
            final String tipoCozinhaInexistente = "VEGANA";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/tipo-cozinha/{tipoCozinha}", tipoCozinhaInexistente)
                .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Nenhum restaurante encontrado para o tipo de cozinha: " + tipoCozinhaInexistente));
        }

        @Test
        void deveBuscarRestaurantesPorCep() {
            final String cep = "66816810";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/localizacao/cep/{cep}", cep)
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("endereco.cep", everyItem(equalTo(cep)));
        }

        @Test
        void naoDeveEncontrarRestaurantesPorCepInexistente() {
            String cepInexistente = "66810000";

            given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/restaurante/localizacao/cep/{cep}", cepInexistente)
                .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Nenhum restaurante encontrado para o CEP: " + cepInexistente));
        }
    }
}

