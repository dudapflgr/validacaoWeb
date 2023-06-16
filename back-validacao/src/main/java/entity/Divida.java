package entity;

import java.io.File;

public class Divida {

    private Long id;

    private Cliente cliente;

    private String descricao;

    private Double valor;

    private Integer situacao;

    private Integer numeroProcesso;

    private File arquivo;

    private String nomeArquivo;

    public Divida() {}

    public Divida(Long id, Cliente cliente, String descricao, Double valor, Integer situacao, Integer numeroProcesso, File arquivo, String nomeArquivo) {
        this.id = id;
        this.cliente = cliente;
        this.descricao = descricao;
        this.valor = valor;
        this.situacao = situacao;
        this.numeroProcesso = numeroProcesso;
        this.arquivo = arquivo;
        this.nomeArquivo = nomeArquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getSituacao() {
        return situacao;
    }

    public void setSituacao(int situacao) {
        this.situacao = situacao;
    }

    public Integer getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(int numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
