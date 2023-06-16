CREATE DATABASE banco_validacao;

USE banco_validacao;

CREATE TABLE cliente (
	id_cliente INT NOT NULL AUTO_INCREMENT,
	nome VARCHAR(100) NOT NULL,
	email VARCHAR(30) UNIQUE NOT NULL,
    cpf CHAR(14) UNIQUE NOT NULL,
    cep CHAR(9) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(100),
	PRIMARY KEY (id_cliente)
);

CREATE TABLE divida (
	id_divida INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    descricao VARCHAR(150) NOT NULL,
    valor DOUBLE NOT NULL,
    situacao INT NOT NULL,
    numero_processo INT,
    arquivo LONGBLOB,
    nome_arquivo VARCHAR(100),
    PRIMARY KEY (id_divida),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

SELECT * FROM cliente;

SELECT * FROM divida;

SELECT id_divida, nome, cpf, valor, descricao, situacao FROM divida INNER JOIN cliente ON divida.id_cliente = cliente.id_cliente;
