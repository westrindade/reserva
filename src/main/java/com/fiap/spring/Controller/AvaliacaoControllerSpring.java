package com.fiap.spring.Controller;

import com.fiap.reserva.application.controller.AvaliacaoControllerApplication;
import com.fiap.reserva.domain.entity.Avaliacao;
import com.fiap.spring.Controller.Dto.AvaliacaoDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Avaliação", description = "Avaliação do usuário após reserva")
@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoControllerSpring {

    private AvaliacaoControllerApplication avaliacaoController;

    @Operation(summary = "Realiza avaliação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoDto.class, description = "Avaliacao")) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)) }),
    })
    @PostMapping
    public ResponseEntity<?> avaliar(@RequestBody AvaliacaoDto avaliacaoDto){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoController.avaliar(avaliacaoDto));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Busca Avaliação")
    @ApiOperation("Busca Avaliação por cnpj")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoDto.class, description = "Avaliacao")) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)) }),
    })
    @GetMapping("/{cnpj}")
    public ResponseEntity<?> buscarTodasPorCnpj(@PathVariable @ApiParam(value = "Cnpj do restaurante", example = "11 caracteres alfanumericos")
                                                    String cnpj) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    avaliacaoController.getBuscarTodos(cnpj)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}