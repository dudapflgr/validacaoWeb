package dao;

import dto.response.DividaResponse;
import entity.Cliente;
import entity.Divida;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DividaDao {

    public Cliente procurarClietePorCpf(String cpf) throws SQLException {
        Cliente cliente = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("SELECT id_cliente, nome, cpf, email, cep, logradouro, numero, complemento FROM cliente WHERE cpf LIKE ? ");
            pstmt.setString(1, cpf);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id_cliente");
                String nome = rs.getString("nome");
                String cpfCliente = rs.getString("cpf");
                String email = rs.getString("email");
                String cep = rs.getString("cep");
                String logradouro = rs.getString("logradouro");
                String numero = rs.getString("numero");
                String complemento = rs.getString("complemento");
                cliente = new Cliente(id, nome, cpfCliente, email, cep, logradouro, numero, complemento);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
            cliente = null;
        } finally {
            conn.close();
            pstmt.close();
        }

        return cliente;
    }

    public Cliente cadastrarCliente(Cliente cliente) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("INSERT INTO cliente(nome, email, cpf, cep, logradouro, numero, complemento) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getEmail());
            pstmt.setString(3, cliente.getCpf());
            pstmt.setString(4, cliente.getCep());
            pstmt.setString(5, cliente.getLogradouro());
            pstmt.setString(6, cliente.getNumero());
            pstmt.setString(7, cliente.getComplemento());
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                cliente.setId(generatedKeys.getLong(1));
            }
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
            cliente = null;
        } finally {
            conn.close();
            pstmt.close();
        }

        return cliente;
    }

    public Divida cadastrarDivida(Divida divida) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("INSERT INTO divida(id_cliente, descricao, valor, situacao, numero_processo, arquivo, nome_arquivo) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, divida.getCliente().getId());
            pstmt.setString(2, divida.getDescricao());
            pstmt.setDouble(3, divida.getValor());
            pstmt.setInt(4, divida.getSituacao());
            if (divida.getNumeroProcesso() != null) {
                pstmt.setInt(5, divida.getNumeroProcesso());
            } else {
                pstmt.setNull(5, Types.BIGINT);
            }
            if (divida.getArquivo() != null) {
                InputStream in = new FileInputStream(divida.getArquivo());
                pstmt.setBinaryStream(6, in);
                pstmt.setString(7, divida.getNomeArquivo());
            } else {
                pstmt.setBinaryStream(6, null);
                pstmt.setString(7, null);
            }
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                divida.setId(generatedKeys.getLong(1));
            }
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
            divida = null;
        } finally {
            conn.close();
            pstmt.close();
        }

        return divida;
    }

    public List<DividaResponse> buscarTodasDividas() throws SQLException {
        List<DividaResponse> dividas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("SELECT id_divida, nome, cpf, valor, descricao, situacao FROM divida INNER JOIN cliente ON divida.id_cliente = cliente.id_cliente");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id_divida");
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");;
                Double valor = rs.getDouble("valor");
                String descricao = rs.getString("descricao");
                int situacao = rs.getInt("situacao");
                DividaResponse dividaResponse = new DividaResponse(id, nome, cpf, valor, descricao, situacao);
                dividas.add(dividaResponse);
            }
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
        } finally {
            conn.close();
            pstmt.close();
        }
        return dividas;
    }

    public boolean deletarDivida(Long id) throws SQLException {
        boolean apagou = true;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("DELETE FROM divida WHERE id_divida = ?");
            pstmt.setLong(1, id);
            int i = pstmt.executeUpdate();
            if (i == 0) {
                apagou = false;
            }
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
            apagou = false;
        } finally {
            conn.close();
            pstmt.close();
        }
        return apagou;
    }

    public File buscarComprovante(Long id) throws SQLException {
        File comprovante = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("SELECT arquivo, nome_arquivo FROM divida WHERE id_divida = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String nomeArquivo = rs.getString("nome_arquivo");
                comprovante = new File(nomeArquivo);
                FileOutputStream output = new FileOutputStream(comprovante);
                InputStream input = rs.getBinaryStream("arquivo");
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
        } finally {
            conn.close();
            pstmt.close();
        }
        return comprovante;
    }

    public Divida buscarDividaPorId(Long id) throws SQLException {
        Divida divida = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("SELECT id_divida, divida.id_cliente, descricao, valor, situacao, numero_processo, arquivo, " +
                    "nome_arquivo, nome, email, cpf, cep, logradouro, numero, complemento FROM divida INNER JOIN cliente ON divida.id_cliente = cliente.id_cliente WHERE id_divida = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                long idDivida = rs.getLong("id_divida");
                long idCliente = rs.getLong("id_cliente");
                String descricao = rs.getString("descricao");
                Double valor = rs.getDouble("valor");
                int situacao = rs.getInt("situacao");
                Integer numeroProcesso = rs.getInt("numero_processo");
                String nomeArquivo = rs.getString("nome_arquivo");
                File comprovante = null;
                if (nomeArquivo != null ) {
                    comprovante = new File(nomeArquivo);
                    FileOutputStream output = new FileOutputStream(comprovante);
                    InputStream input = rs.getBinaryStream("arquivo");
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) > 0) {
                        output.write(buffer);
                    }
                }
                String nomeCliente = rs.getString("nome");
                String email = rs.getString("email");
                String cpf = rs.getString("cpf");
                String cep = rs.getString("cep");
                String logradouro = rs.getString("logradouro");
                String numero = rs.getString("numero");
                String complemento = rs.getString("complemento");
                Cliente cliente = new Cliente(idCliente, nomeCliente, cpf, email, cep, logradouro, numero, complemento);
                divida = new Divida(idDivida, cliente, descricao, valor, situacao, numeroProcesso, comprovante, nomeArquivo);
            }
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
            divida = null;
        } finally {
            conn.close();
            pstmt.close();
        }
        return divida;
    }

    public boolean atualizarCliente(Cliente cliente) throws SQLException {
        boolean atualizou = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("UPDATE cliente SET nome = ?, email = ?,  cep = ?, logradouro=?, numero=?, complemento=? WHERE cpf = ?");
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getEmail());
            pstmt.setString(3, cliente.getCep());
            pstmt.setString(4, cliente.getLogradouro());
            pstmt.setString(5, cliente.getNumero());
            pstmt.setString(6, cliente.getComplemento());
            pstmt.setString(7, cliente.getCpf());
            pstmt.executeUpdate();
            atualizou = true;
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
            atualizou = false;
        } finally {
            conn.close();
            pstmt.close();
        }
        return atualizou;
    }

    public boolean atualizarDivida(Divida divida) throws SQLException {
        boolean atualizou = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco_validacao", "root", "admin");
            pstmt = conn.prepareStatement("UPDATE divida SET descricao = ?, valor = ?,  situacao = ?, numero_processo = ?, arquivo = ?, nome_arquivo = ? WHERE id_divida = ?");
            pstmt.setString(1, divida.getDescricao());
            pstmt.setDouble(2, divida.getValor());
            pstmt.setInt(3, divida.getSituacao());
            if (divida.getNumeroProcesso() != null) {
                pstmt.setInt(4, divida.getNumeroProcesso());
            } else {
                pstmt.setNull(4, Types.BIGINT);
            }
            if (divida.getArquivo() != null) {
                InputStream in = new FileInputStream(divida.getArquivo());
                pstmt.setBinaryStream(5, in);
                pstmt.setString(6, divida.getNomeArquivo().toString());
            } else {
                pstmt.setBinaryStream(5, null);
                pstmt.setString(6, null);
            }
            pstmt.setLong(7, divida.getId());
            pstmt.executeUpdate();
            atualizou = true;
        } catch (Exception e) {
            System.out.println("Erro: "+ e.getMessage());
            atualizou = false;
        } finally {
            conn.close();
            pstmt.close();
        }
        return atualizou;
    }
}
