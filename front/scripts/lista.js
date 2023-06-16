async function carregarDividas() {
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        try {
            let response = await fetch(URL_DIVIDA+'all', {
                method: 'GET',
                headers: {
                  'AUTH' : token
                }
            })
            let tabela = document.getElementById("tabela")
            let result = await response.json()
            if(response.status == 200) {
                console.log("Response",result)
                if (result.length == 0) {
                    let tr = document.createElement("tr")
                    tr.setAttribute('id','trMessage')
                    let td = document.createElement("td")
                    td.colSpan = 7
                    td.innerHTML = '<b>Nenhuma dívida!</b>'
                    tr.append(td);
                    tabela.appendChild(tr);
                }
                carregarDividasNaTabela(result, false)
                localStorage.setItem('dividas', JSON.stringify(result))
            }
        } catch(erro) {
            let dividas = localStorage.getItem('dividas')
            carregarDividasNaTabela(JSON.parse(dividas), true)
        }
    }
}

function carregarDividasNaTabela(result, localmente) {
    let bodyTabela = document.getElementById('body-tabela')
    result.forEach(divida => {
        let tr = document.createElement('tr')
        let id = document.createElement('td')
        let nome = document.createElement('td')
        let cpf = document.createElement('td')
        let valor = document.createElement('td')
        let descricao = document.createElement('td')
        let situacao = document.createElement('td')
        let acao = document.createElement('td')
        tr.setAttribute('id',divida.id)
        id.innerText = divida.id
        nome.innerText = divida.nome
        cpf.innerText = divida.cpf
        valor.innerText = 'R$'+divida.valor
        descricao.innerText = divida.descricao
        situacao.innerText = divida.status == 1? 'Pendente':'Pago'
        if (localmente) {
            acao.innerHTML = 'Ações não disponíveis'
        } else {
            acao.innerHTML = '<button onclick="deletarDivida(this)" value="excluir" class="botao">Deletar</button><button onclick="editarDivida(this)" value="editar" class="botao">Editar</button><button onclick="baixarDivida(this)" value="baixar" class="botao">Baixar comprovante</button>'
        }
        tr.append(id)
        tr.append(nome)
        tr.append(cpf)
        tr.append(valor)
        tr.append(descricao)
        tr.append(situacao)
        tr.append(acao)

        bodyTabela.append(tr)
    });
}

function deletarDivida(row) {
    let confirma = window.confirm('Deseja remover dívida?')
  
    if (confirma == true) {
        let id = row.parentNode.parentNode.getAttribute('id')
        apagarDivida(id)
    } else {
        alert('Nenhuma ação realizada!')
    }
}

async function apagarDivida(id) {
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        try {
            let response = await fetch(URL_DIVIDA+'deletar?id='+id, {
                method: 'DELETE',
                headers: {
                  'AUTH' : token
                }
            })
            if(response.status == 200) {
                alert('Divida apagada com sucesso!')
                window.location.href = "/protegido/lista.html"
            }
            if(response.status == 400) {
                alert('Erro ao apagar a dívida!')
            }
            if(response.status == 401) {
                window.localStorage.setItem("token", "")
                window.localStorage.setItem("dividas", "")
                window.location.href = "/login.html"
            }
        } catch(erro) {
            alert('Erro ao apagar dívida!')
        }
    }
}

function baixarDivida(row) {
    let id = row.parentNode.parentNode.getAttribute('id')
    baixarComprovante(id)
}

async function baixarComprovante(id) {
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        try {
            let response = await fetch(URL_DIVIDA+'comprovante?id='+id, {
                method: 'GET',
                headers: {
                  'AUTH' : token
                },
                responseType:'blob'
            })
            if(response.status == 200) {
                var comprovante = await response.blob()
                var comprovanteURL = URL.createObjectURL(comprovante)
                
                const anchor = document.createElement("a")
                anchor.href = comprovanteURL
                anchor.download = 'comprovante.pdf'
                
                document.body.appendChild(anchor)
                anchor.click()
                document.body.removeChild(anchor)

                URL.revokeObjectURL(comprovanteURL)
            }
            if(response.status == 400) {
                alert('Comprovante não foi cadastrado!')
            }
            if(response.status == 401) {
                window.localStorage.setItem("token", "")
                window.localStorage.setItem("dividas", "")
                window.location.href = "/login.html"
            }
        } catch(erro) {
            alert('Erro ao baixar comprovante!')
        }
    }
}

function editarDivida(row) {
    let id = row.parentNode.parentNode.getAttribute('id')
    window.location.href = "/protegido/cadastro.html?id="+id
}