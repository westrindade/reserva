package com.fiap.reserva.infra.jdbc.restaurante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fiap.reserva.domain.entity.HorarioFuncionamento;
import com.fiap.reserva.domain.entity.Restaurante;
import com.fiap.reserva.domain.entity.TipoCozinha;
import com.fiap.reserva.domain.exception.BusinessException;
import com.fiap.reserva.domain.repository.RestauranteRepository;
import com.fiap.reserva.domain.vo.CnpjVo;
import com.fiap.reserva.domain.vo.EnderecoVo;
import com.fiap.reserva.infra.exception.TechnicalException;

public class RestauranteRepositoryImpl implements RestauranteRepository {

    final Connection connection;

    public RestauranteRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Restaurante buscarPorCnpj(CnpjVo cnpj) throws BusinessException {
        final StringBuilder query = new StringBuilder()
                .append("SELECT * FROM tb_restaurante as re ")
                .append("LEFT JOIN tb_restaurante_horarios as hf ")
                .append("ON re.cd_cnpj = hf.cd_restaurante ")
                .append("LEFT JOIN tb_restaurante_endereco as e ")
                .append("ON re.cd_cnpj = e.cd_restaurante ")
                .append("WHERE re.cd_cnpj = ? ")
                ;

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            ps.setString(1, cnpj.getNumero());

            try (final ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return contruirRestaurante(rs);
                }
            }
        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
        return null;
    }

    @Override
    public Restaurante buscarPorNome(String nome) throws BusinessException {
        final StringBuilder query = new StringBuilder()
                .append("SELECT * FROM tb_restaurante re ")
                .append("INNER JOIN tb_restaurante_horarios hf ")
                .append("ON re.cd_cnpj = hf.cd_restaurante ")
                .append("INNER JOIN tb_restaurante_endereco e ")
                .append("ON re.cd_cnpj = e.cd_restaurante ")
                .append("WHERE re.nm_restaurante = ? ")
                ;

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            ps.setString(1, nome);

            try (final ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return contruirRestaurante(rs);
                }
            }
        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
        return null;
    }

    @Override
    public List<Restaurante> buscarPorTipoCozinha(TipoCozinha tipoCozinha) throws BusinessException {
        final List<Restaurante> list = new ArrayList<>();
        final StringBuilder query = new StringBuilder()
                .append("SELECT * FROM tb_restaurante re ")
                .append("INNER JOIN tb_restaurante_horarios hf ")
                .append("ON re.cd_cnpj = hf.cd_restaurante ")
                .append("INNER JOIN tb_restaurante_endereco e ")
                .append("ON re.cd_cnpj = e.cd_restaurante ")
                .append("WHERE re.ds_tipo_cozinha = ? ")
                ;

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            ps.setString(1, tipoCozinha.name());

            try (final ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    list.add(contruirRestaurante(rs));
                }
            }
        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
        return list;
    }

    @Override
    public Integer obterLotacaoMaximaRestaurante(Restaurante restaurante) throws BusinessException {
        final StringBuilder query = new StringBuilder()
                .append("SELECT re.qt_capacidade_mesas total_Mesas FROM tb_restaurante re ")
                .append("WHERE re.cd_cnpj = ? ")
                ;

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            ps.setString(1, restaurante.getCnpjString());

            try (final ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return Integer.parseInt(rs.getString("total_Mesas"));
                }
            }
        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
        return null;
    }

    @Override
    public List<Restaurante> buscarPorCep(String cep) throws BusinessException {
        List<Restaurante> list = new ArrayList<>();
        final StringBuilder query = new StringBuilder()
                .append("SELECT * FROM tb_restaurante re ")
                .append("INNER JOIN tb_restaurante_endereco e ")
                .append("ON re.cd_cnpj = e.cd_restaurante ")
                .append("WHERE e.cd_cep = ? ");

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            ps.setString(1, cep);

            try (final ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    list.add(contruirRestaurante(rs));
                }
            }
        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
        return list;
    }

    @Override
    public Restaurante cadastrar(Restaurante restaurante) throws BusinessException {
        final StringBuilder query = new StringBuilder()
                .append("INSERT INTO tb_restaurante ")
                .append("(cd_cnpj, nm_restaurante, qt_capacidade_mesas, ds_tipo_cozinha) ")
                .append("VALUES ")
                .append("(?, ?, ?, ?) ")
                ;

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            int i = 1;
            ps.setString(i++, restaurante.getCnpjString());
            ps.setString(i++, restaurante.getNome());
            ps.setInt(i++, restaurante.getCapacidadeMesas());
            ps.setString(i++, restaurante.getTipoCozinha().name());
            ps.execute();

            return restaurante;

        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public Restaurante alterar(Restaurante restaurante) throws BusinessException {
        final StringBuilder query = new StringBuilder()
                .append("UPDATE tb_restaurante ")
                .append("SET nm_restaurante = ?, ")
                .append("qt_capacidade_mesas = ?, ")
                .append("ds_tipo_cozinha = ? ")
                .append("WHERE cd_cnpj = ? ")
                ;

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            int i = 1;
            ps.setString(i++, restaurante.getNome());
            ps.setInt(i++, restaurante.getCapacidadeMesas());
            ps.setString(i++, restaurante.getTipoCozinha().name());

            //WHERE
            ps.setString(i++, restaurante.getCnpjString());

            ps.executeUpdate();

            return restaurante;
        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public void excluir(CnpjVo cnpj) throws BusinessException {
        final StringBuilder query = new StringBuilder()
                .append("DELETE FROM tb_restaurante ")
                .append("WHERE cd_cnpj = ? ")
                ;

        try (final PreparedStatement ps = connection.prepareStatement(query.toString())) {
            int i = 1;
            ps.setString(i++, cnpj.getNumero());

            ps.execute();
        } catch (SQLException e) {
            throw new TechnicalException(e);
        }
    }

    private Restaurante contruirRestaurante(ResultSet rs) throws SQLException, BusinessException {
        CnpjVo cnpj = new CnpjVo(rs.getString("cd_cnpj"));
        String nome = rs.getString("nm_restaurante");
        TipoCozinha tipoCozinha = TipoCozinha.valueOf(rs.getString("ds_tipo_cozinha"));
        int capacidadeMesas = rs.getInt("qt_capacidade_mesas");

        EnderecoVo endereco = new EnderecoVo(
                rs.getString("cd_cep"),
                rs.getString("ds_logradouro"),
                rs.getString("ds_numero"),
                rs.getString("ds_complemento"),
                rs.getString("nm_bairro"),
                rs.getString("nm_cidade"),
                rs.getString("uf_estado")
        );

        // lista de HorarioFuncionamento
        List<HorarioFuncionamento> horariosFuncionamento = new ArrayList<>();
        // Lógica para adicionar horários ao horariosFuncionamento

        return new Restaurante(cnpj, nome, endereco, horariosFuncionamento, capacidadeMesas, tipoCozinha);
    }

//    private Restaurante contruirRestaurante(ResultSet rs) throws SQLException, BusinessException {
//        return new Restaurante(
//                rs.getString("cd_cnpj"),
//                rs.getString("nm_restaurante"),
//               null,// enderecoRepository.construirEndereco(rs),
//                Collections.emptyList(), // horarioFuncionamentoRepository.construirHorarioFuncionamento(rs), <- ta errado tem de ser uma lista
//                rs.getInt("qt_capacidade_mesas"),
//                TipoCozinha.valueOf(rs.getString("ds_tipo_cozinha"))
//        );
//    }


}
