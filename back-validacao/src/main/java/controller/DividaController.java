package controller;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DividaDao;
import dto.response.DividaResponse;
import dto.response.ErrorResponse;
import entity.Cliente;
import entity.Divida;
import io.javalin.http.Context;
import utils.Validador;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public final class DividaController {

    public static void cadastrarDivida(Context ctx) {
        Gson gson = new GsonBuilder().create();
        try {
            String erros = "";
            String cpf = ctx.formParam("CPF");
            String nome = ctx.formParam("nome");
            String email = ctx.formParam("email");
            String cep = ctx.formParam("cep");
            String logradouro = ctx.formParam("logradouro");
            String numero = ctx.formParam("numero");
            String complemento = ctx.formParam("complemento");
            Double valor = Double.valueOf(ctx.formParam("valor"));
            String descricao = ctx.formParam("descricao");
            Integer situacao = Integer.parseInt(ctx.formParam("situacao"));
            Integer processo = ctx.formParam("processo").getBytes().length != 0 ? Integer.parseInt(ctx.formParam("processo")) : null;
            InputStream comprovante = ctx.uploadedFile("comprovante").content().readAllBytes().length != 0 ? ctx.uploadedFile("comprovante").content() : null;

            if (cpf == null || cpf == "" || cpf.length() > 14 || !Validador.cpfValido(cpf)) {
                erros += "Informe um CPF válido!\n";
            }

            if (nome == null || nome == "" || nome.length() > 100) {
                erros += "Informe um nome válido!\n";
            }

            if (email == null || email == "" || email.length() > 30 || !Validador.emailValido(email)) {
                erros += "Informe um e-mail válido!\n";
            }

            if (cep == null || cep == "" || cep.length() > 9 || !Validador.cepValido(cep)) {
                erros += "Informe um CEP válido!\n";
            }

            if (logradouro == null || logradouro == "" || logradouro.length() > 100) {
                erros += "Informe um logradouro válido!\n";
            }

            if (numero == null || numero == "" || numero.length() > 20) {
                erros += "Informe um número válido!\n";
            }

            if (complemento != null && complemento.length() > 100) {
                erros += "Informe um complemento válido!\n";
            }

            if (valor == null || valor.doubleValue() <= 0.0) {
                erros += "Informe um valor válido!\n";
            }

            if (descricao == null || descricao == "") {
                erros += "Informe uma descrição válida!\n";
            }

            if (situacao == null || situacao > 2 || situacao < 1) {
                erros += "Informe uma situação válida!\n";
            }

            if (erros != "") {
                throw new Exception(erros);
            }

            DividaDao dividaDao = new DividaDao();
            Cliente cliente = dividaDao.procurarClietePorCpf(cpf);

            if (cliente == null) {
                cliente = new Cliente();
                cliente.setCpf(cpf);
                cliente.setNome(nome);
                cliente.setEmail(email);
                cliente.setCep(cep);
                cliente.setLogradouro(logradouro);
                cliente.setNumero(numero);
                cliente.setComplemento(complemento);
                cliente = dividaDao.cadastrarCliente(cliente);
            }

            Divida divida = new Divida();
            divida.setCliente(cliente);
            divida.setDescricao(descricao);
            divida.setSituacao(situacao);
            divida.setValor(valor);
            if (processo != null) {
                divida.setNumeroProcesso(processo);
            }
            if (comprovante != null) {
                divida.setNomeArquivo(ctx.uploadedFile("comprovante").filename());
                File arquivo = new File(divida.getNomeArquivo());
                Files.write(comprovante.readAllBytes(), arquivo);
                divida.setArquivo(arquivo);
            }
            divida = dividaDao.cadastrarDivida(divida);
            if (divida == null) {
                throw new Exception("Erro ao cadastrar dívida!");
            }
            ctx.status(200);
        } catch (NumberFormatException e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, "Algum atributo informado está no formato inválido!");
            ctx.json(gson.toJson(errorResponse));
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            ctx.json(gson.toJson(errorResponse));
        }
    }

    public static void procuraCliente(Context ctx, String cpf) {
        Gson gson = new GsonBuilder().create();
        try {
            if (cpf == null || cpf == "" || !Validador.cpfValido(cpf)) {
                throw new Exception("CPF Inválido!");
            }
            DividaDao dividaDao = new DividaDao();
            Cliente cliente = dividaDao.procurarClietePorCpf(cpf);
            if (cliente == null) {
                ctx.status(404);
                ErrorResponse errorResponse = new ErrorResponse(404, "Cliente não encontrado!");
                ctx.json(gson.toJson(errorResponse));
            } else {
                ctx.status(200);
                ctx.json(gson.toJson(cliente));
            }
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            ctx.json(gson.toJson(errorResponse));
        }
    }

    public static void buscarTodasDividas(Context ctx) {
        Gson gson = new GsonBuilder().create();
        try {
            DividaDao dividaDao = new DividaDao();
            List<DividaResponse> dividas = dividaDao.buscarTodasDividas();
            ctx.status(200);
            ctx.json(gson.toJson(dividas));
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            ctx.json(gson.toJson(errorResponse));
            System.out.println(e.getMessage());
        }
    }

    public static void deletarDivida(Context ctx) {
        Gson gson = new GsonBuilder().create();
        try {
            Long id = Long.valueOf(ctx.queryParam("id"));
            DividaDao dividaDao = new DividaDao();
            if (dividaDao.deletarDivida(id)) {
                ctx.status(200);
            } else {
                throw new Exception("Erro ao apagar a dívida!");
            }
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            ctx.json(gson.toJson(errorResponse));
            System.out.println(e.getMessage());
        }
    }

    public static void baixarComprovante(Context ctx) {
        Gson gson = new GsonBuilder().create();
        try {
            Long id = Long.valueOf(ctx.queryParam("id"));
            DividaDao dividaDao = new DividaDao();
            File comprovante = dividaDao.buscarComprovante(id);
            if (comprovante != null) {
                FileInputStream fi = new FileInputStream(comprovante);
                ctx.status(200);
                ctx.result(fi.readAllBytes());
            } else {
                throw new Exception("Erro ao baixar comprovante!");
            }
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            ctx.json(gson.toJson(errorResponse));
            System.out.println(e.getMessage());
        }
    }

    public static void procurarDividaPorId(Context ctx) {
        Gson gson = new GsonBuilder().create();
        try {
            Long id = Long.valueOf(ctx.queryParam("id"));
            DividaDao dividaDao = new DividaDao();
            Divida divida = dividaDao.buscarDividaPorId(id);
            if (divida == null) {
                throw new Exception("Divida não existe!");
            }
            ctx.status(200);
            ctx.json(gson.toJson(divida));
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            ctx.json(gson.toJson(errorResponse));
            System.out.println(e.getMessage());
        }
    }

    public static void atualizarDivida(Context ctx) {
        Gson gson = new GsonBuilder().create();
        try {
            String erros = "";
            Long id = Long.valueOf(ctx.formParam("id"));
            String cpf = ctx.formParam("CPF");
            String nome = ctx.formParam("nome");
            String email = ctx.formParam("email");
            String cep = ctx.formParam("cep");
            String logradouro = ctx.formParam("logradouro");
            String numero = ctx.formParam("numero");
            String complemento = ctx.formParam("complemento");
            Double valor = Double.valueOf(ctx.formParam("valor"));
            String descricao = ctx.formParam("descricao");
            Integer situacao = Integer.parseInt(ctx.formParam("situacao"));
            Integer processo = ctx.formParam("processo").getBytes().length != 0 ? Integer.parseInt(ctx.formParam("processo")) : null;
            InputStream comprovante = ctx.uploadedFile("comprovante").content().readAllBytes().length != 0 ? ctx.uploadedFile("comprovante").content() : null;

            if (cpf == null || cpf == "" || cpf.length() > 14 || !Validador.cpfValido(cpf)) {
                erros += "Informe um CPF válido!\n";
            }

            if (nome == null || nome == "" || nome.length() > 100) {
                erros += "Informe um nome válido!\n";
            }

            if (email == null || email == "" || email.length() > 30 || !Validador.emailValido(email)) {
                erros += "Informe um e-mail válido!\n";
            }

            if (cep == null || cep == "" || cep.length() > 9 || !Validador.cepValido(cep)) {
                erros += "Informe um CEP válido!\n";
            }

            if (logradouro == null || logradouro == "" || logradouro.length() > 100) {
                erros += "Informe um logradouro válido!\n";
            }

            if (numero == null || numero == "" || numero.length() > 20) {
                erros += "Informe um número válido!\n";
            }

            if (complemento != null && complemento.length() > 100) {
                erros += "Informe um complemento válido!\n";
            }

            if (valor == null || valor.doubleValue() <= 0.0) {
                erros += "Informe um valor válido!\n";
            }

            if (descricao == null || descricao == "") {
                erros += "Informe uma descrição válida!\n";
            }

            if (situacao == null || situacao > 2 || situacao < 1) {
                erros += "Informe uma situação válida!\n";
            }

            if (erros != "") {
                throw new Exception(erros);
            }

            DividaDao dividaDao = new DividaDao();
            Cliente cliente = new Cliente();

            cliente.setCpf(cpf);
            cliente.setNome(nome);
            cliente.setEmail(email);
            cliente.setCep(cep);
            cliente.setLogradouro(logradouro);
            cliente.setNumero(numero);
            cliente.setComplemento(complemento);
            boolean atualizouCliente = dividaDao.atualizarCliente(cliente);

            Divida divida = new Divida();
            divida.setId(id);
            divida.setCliente(cliente);
            divida.setDescricao(descricao);
            divida.setSituacao(situacao);
            divida.setValor(valor);
            if (processo != null) {
                divida.setNumeroProcesso(processo);
            }
            if (comprovante != null) {
                divida.setNomeArquivo(ctx.uploadedFile("comprovante").filename());
                File arquivo = new File(divida.getNomeArquivo());
                Files.write(comprovante.readAllBytes(), arquivo);
                divida.setArquivo(arquivo);
            }
            boolean atualizouDivida = dividaDao.atualizarDivida(divida);
            if (!atualizouDivida || !atualizouCliente) {
                throw new Exception("Erro ao atualizar dívida!");
            }
            ctx.status(200);
        } catch (NumberFormatException e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, "Algum atributo informado está no formato inválido!");
            ctx.json(gson.toJson(errorResponse));
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            ctx.json(gson.toJson(errorResponse));
        }
    }
}
