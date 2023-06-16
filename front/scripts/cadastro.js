let idDivida;

function loadPage() {
    autenticado()
    if (window.location.search != "") {
        carregarDivida()
    }
}

function formataCPF() {
    let cpf = document.getElementById('CPF').value
    if (cpf.length == 11) {
        var numbers = /^[0-9]*$/
        if(cpf.match(numbers))
        {
            let cpfFormatado = cpf.substring(0,3)+'.'+cpf.substring(3,6)+'.'+ cpf.substring(6,9)+'-'+cpf.substring(9,11)
            document.getElementById('CPF').value = cpfFormatado
        } else {
            document.getElementById('CPF').value = ''
        }
    } 
}

function formataCEP() {
    let cep = document.getElementById('cep').value
    if (cep.length == 8) {
        var numbers = /^[0-9]*$/
        if(cep.match(numbers))
        {
            let cepFormatado = cep.substring(0,5)+'-'+cep.substring(5,8)
            document.getElementById('cep').value = cepFormatado
        } else {
            document.getElementById('cep').value = ''
        }
    } 
}

async function autenticado() {
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        let request ={
            "token":token
        }
        let response = await fetch(URL_AUTENTICACAO+'validar', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(request)
        });
        if (response.ok) {
            console.log("Pode cadastrar!")
        } else {
            console.log("Response",response.json)
            window.localStorage.setItem("token", "")
            window.location.href = "/login.html"
        }
    }
}

async function carregaCliente() {
    let cpf = document.getElementById('CPF').value
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        try {
            let response = await fetch(URL_CLIENTE+'?cpf='+cpf, {
                method: 'GET',
                headers: {
                  'AUTH' : token
                }
            })
            let result = await response.json();
            if(response.status == 200) {
                console.log("Response",result);
                document.getElementById('message').innerText = 'Cliente já cadastrado. Carregando informações...'
                setTimeout(()=> {
                    carregarCliente(result)
                 }
                 ,2500);
            }
            if(response.status == 400) {
                document.getElementById('message').innerText = 'CPF inválido!'
                habilitaClienteCadastro()
            }
            if(response.status == 401) {
                window.localStorage.setItem("token", "")
                window.localStorage.setItem("dividas", "")
                window.location.href = "/login.html"
            }
            if(response.status == 404) {
                document.getElementById('message').innerText = 'Cliente ainda não cadastrado!'
                habilitaClienteCadastro()
            }
        } catch(erro) {
            console.log(erro)
        }
    }
}

function carregarCliente(result) {
    document.getElementById('nome').value = result.nome
    document.getElementById('nome').disabled = true
    document.getElementById('email').value = result.email
    document.getElementById('email').disabled = true
    document.getElementById('cep').value = result.cep
    document.getElementById('cep').disabled = true
    document.getElementById('logradouro').value = result.logradouro
    document.getElementById('logradouro').disabled = true
    document.getElementById('numero').value = result.numero
    document.getElementById('numero').disabled = true
    document.getElementById('complemento').value = result.complemento
    document.getElementById('complemento').disabled = true
    document.getElementById('message').innerText = 'Cliente carregado!'
}

function habilitaClienteCadastro() {
    document.getElementById('nome').value = ''
    document.getElementById('nome').disabled = false
    document.getElementById('email').value = ''
    document.getElementById('email').disabled = false
    document.getElementById('cep').value = ''
    document.getElementById('cep').disabled = false
    document.getElementById('logradouro').value = ''
    document.getElementById('logradouro').disabled = false
    document.getElementById('numero').value = ''
    document.getElementById('numero').disabled = false
    document.getElementById('complemento').value = ''
    document.getElementById('complemento').disabled = false
}

function habilitaAntesEnvio() {
    document.getElementById('CPF').disabled = false
    document.getElementById('nome').disabled = false
    document.getElementById('email').disabled = false
    document.getElementById('cep').disabled = false
    document.getElementById('logradouro').disabled = false
    document.getElementById('numero').disabled = false
    document.getElementById('complemento').disabled = false
}

function carregarDivida() {
    idDivida = window.location.search.split('=')[1]
    console.log(idDivida)
    buscarDividaBanco(idDivida)
}

async function buscarDividaBanco(idDivida) {
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        try {
            let response = await fetch(URL_DIVIDA+'?id='+idDivida, {
                method: 'GET',
                headers: {
                  'AUTH' : token
                }
            })
            let result = await response.json();
            if(response.status == 200) {
                document.getElementById('btnCadastro').innerText = 'Atualizar'
                carregarInfosNoForm(result)
            }
            if(response.status == 400) {
                window.location.href = "/protegido/cadastro.html"
            }
            if(response.status == 401) {
                window.localStorage.setItem("token", "")
                window.localStorage.setItem("dividas", "")
                window.location.href = "/login.html"
            }
            if(response.status == 404) {
                "/protegido/cadastro.html"
            }
        } catch(erro) {
            console.log(erro)
        }
    }
}

function carregarInfosNoForm(result) {
    document.getElementById('CPF').value = result.cliente.cpf
    document.getElementById('CPF').disabled = true
    document.getElementById('nome').value = result.cliente.nome
    document.getElementById('email').value = result.cliente.email
    document.getElementById('cep').value = result.cliente.cep
    document.getElementById('logradouro').value = result.cliente.logradouro
    document.getElementById('numero').value = result.cliente.numero
    document.getElementById('complemento').value = result.cliente.complemento

    document.getElementById('valor').value = result.valor
    document.getElementById('descricao').value = result.descricao
    document.getElementById('situacao').value = result.situacao
    document.getElementById('processo').value = result.numeroProcesso
    carregarComprovante(result.arquivo.path, result.id)
}

function cadastrarAtualizarDivida() {
    if (camposValidosEMensagensErro()) {
        habilitaAntesEnvio()
        let form = new FormData(document.getElementById('dividaForm'))
        form.append("id", idDivida)
        enviarDivida(form)
    } else {
        window.alert("Existem campos com erro. Verifique o formulário!")
    }
}

async function enviarDivida(form) {
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        let atualizar = 'atualizar'
        if (idDivida == null) {
            atualizar = 'cadastrar'
        }
        let response = await fetch(URL_DIVIDA+atualizar, {
            method: 'POST',
            headers: {
              'AUTH' : token
            },
            body: form
        })
        if(response.status == 200) {
            let mensagem = atualizar == 'atualizar' ? 'atualizada' : 'cadastrada'
            window.alert("Divida "+mensagem+" com sucesso!")
            window.location.href = "/protegido/lista.html"
        }
        if(response.status == 400) {
            let erros = await response.json()
            window.alert(erros.message)
        }
        if(response.status == 401) {
            window.localStorage.setItem("token", "")
            window.location.href = "/login.html"
        }
    }
}

function criarFomulario() {
    let form = new FormData()
    form.append('id', idDivida)
    form.append('nome', document.getElementById('nome').value)
    form.append('cpf', document.getElementById('CPF').value)
    form.append('email', document.getElementById('email').value)
    form.append('cep', document.getElementById('cep').value)
    form.append('logradouro', document.getElementById('logradouro').value)
    form.append('numero', document.getElementById('numero').value)
    form.append('complemento', document.getElementById('complemento').value)
    form.append('valor', document.getElementById('valor').value)
    form.append('descricao', document.getElementById('descricao').value)
    form.append('situacao', document.getElementById('situacao').value)
    form.append('numeroProcessoJustica', document.getElementById('processo').value)
    let file = document.getElementById('comprovante').files[0]
    if (file != undefined) {
        form.append('dataArquivo',file,'comprovante.pdf')
    }
    return form
}

function camposValidosEMensagensErro() {
    let valido = true;
    let cpf = document.getElementById('CPF').value
    let nome = document.getElementById('nome').value
    let email = document.getElementById('email').value
    let cep = document.getElementById('cep').value
    let logradouro = document.getElementById('logradouro').value
    let numero = document.getElementById('numero').value
    let valor = document.getElementById('valor').value
    let descricao = document.getElementById('descricao').value
    let situacao = document.getElementById('situacao').value

    if(!cpf || cpf.length == 0 || !cpf.trim()) {
        valido = false;
    }

    if(!nome || nome.length == 0 || !nome.trim()) {
        valido = false;
    }

    if(!email || email.length == 0 || !email.trim()) {
        valido = false;
    }
    if(!email.includes("@")) {
        valido = false;
    }

    if(!cep || cep.length == 0 || !cep.trim()) {
        valido = false;
    }

    if(!logradouro || logradouro.length == 0 || !logradouro.trim()) {
        valido = false;
    }

    if(!numero || numero.length == 0 || !numero.trim()) {
        valido = false;
    }

    if(!valor || valor.length == 0 || !valor.trim()) {
        valido = false;
    }
    if(valor <= 0) {
        valido = false;
    }

    if(!descricao || descricao.length == 0 || !descricao.trim()) {
        valido = false;
    }

    if(situacao == "") {
        valido = false;
    }

    return valido;
}

async function carregarComprovante(nome, idDivida) {
    let token = window.localStorage.getItem("token")
    if(token == null) {
        window.location.href = "/login.html"
    } else {
        try {
            let response = await fetch(URL_DIVIDA+'comprovante?id='+idDivida, {
                method: 'GET',
                headers: {
                  'AUTH' : token
                },
                responseType:'blob'
            })
            if(response.status == 200) {
                var comprovante = await response.blob()
                const file = new File([comprovante], nome,{type:"application/pdf", lastModified:new Date().getTime()}, 'utf-8')
                console.log(file)
                let container = new DataTransfer()
                container.items.add(file)
                document.getElementById('comprovante').files = container.files
            }
            if(response.status == 401) {
                window.localStorage.setItem("token", "")
                window.localStorage.setItem("dividas", "")
                window.location.href = "/login.html"
            }
        } catch(erro) {
            console.log(erro)
        }
    }
}
