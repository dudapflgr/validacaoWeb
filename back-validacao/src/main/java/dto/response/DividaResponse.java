package dto.response;

public class DividaResponse {

    private Long id;

    private String nome;

    private String cpf;

    private Double valor;

    private String descricao;

    private int status;

    public DividaResponse(Long id, String nome, String cpf, Double valor, String descricao, int status) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.valor = valor;
        this.descricao = descricao;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(int valor) {
        valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
