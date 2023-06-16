const URL_AUTENTICACAO = "http://localhost:7000/autenticacao/";
const URL_DIVIDA = "http://localhost:7000/divida/";
const URL_CLIENTE = "http://localhost:7000/cliente";

function logar() {
    document.querySelector('#messages').innerHTML = ''
    const username = document.querySelector('#userName').value
    const password = document.querySelector('#password').value
    let erros = validaCamposLogin(username, password)
    let request ={
        "userName":username,
        "password":password
    }
    if(!erros){
        autenticaAcesso(request)
    }
}

function validaCamposLogin(username, password) {
    var messagemErro = ''
    if(username == ''){
        messagemErro += '<p>Por favor informe um usuário!</p>'
    }
    if(password == ''){
        messagemErro += '<p>Por favor informe uma senha!</p>'
    }
    if (messagemErro != '') {
        document.querySelector('#messages').innerHTML += messagemErro
        return true
    }
    return false
}

async function autenticaAcesso(request) {
    try {
        let response = await fetch(URL_AUTENTICACAO+'autenticar', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(request)
        });  
        let result = await response.json()
        if(response.status == 200) {
            window.localStorage.setItem("token", result.token)
            window.location.href = "http://127.0.0.1:5500/protegido/index.html"
        }
        if(response.status == 400) {
            let erros = result.message
            document.querySelector('#messages').innerHTML = ('<b>'+erros+'</b>')
        }
    } catch (erro) {
        document.querySelector('#messages').innerHTML = ('<b>Servidor offline!</b>')
    }
}

function logout() {
    window.localStorage.setItem("token", "")
    window.localStorage.setItem("dividas", "")
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
        })
        if(response.status == 401) {
            window.localStorage.setItem("token", "")
            window.localStorage.setItem("dividas", "")
            window.location.href = "/login.html"
        }
    }
}