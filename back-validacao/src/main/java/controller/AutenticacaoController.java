package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.request.TokenRequest;
import dto.request.UsuarioRequest;
import dto.response.ErrorResponse;
import dto.response.UsuarioResponse;
import io.javalin.http.Context;
import utils.GeradorToken;

public final class AutenticacaoController {

    public static void fazerLogin(Context ctx, GeradorToken geradorToken) {
        Gson gson = new GsonBuilder().create();
        try {
            UsuarioRequest usuarioRequest = gson.fromJson(ctx.body(), UsuarioRequest.class);
            if (usuarioRequest.getUserName() == null || usuarioRequest.getUserName().isBlank() || usuarioRequest.getPassword() == null || usuarioRequest.getPassword().isBlank()) {
                ctx.status(400);
                ErrorResponse errorResponse = new ErrorResponse(400, "Usuário ou senha inválidos!");
                ctx.json(gson.toJson(errorResponse));
            }
            if (usuarioRequest.getUserName().equals("duda@admin.com") && usuarioRequest.getPassword().equals("admin123")) {
                ctx.status(200);
                String token = geradorToken.gerarTokenJwt(usuarioRequest.getUserName());
                UsuarioResponse usuarioResponse = new UsuarioResponse(usuarioRequest.getUserName(), token);
                ctx.json(gson.toJson(usuarioResponse));
            } else {
                ctx.status(400);
                ErrorResponse errorResponse = new ErrorResponse(400, "Usuário ou senha incorretos!");
                ctx.json(gson.toJson(errorResponse));
            }
        } catch (Exception e) {
            ctx.status(400);
            ErrorResponse errorResponse = new ErrorResponse(400, "Erro ao gerar o token!");
            ctx.json(gson.toJson(errorResponse));
        }
    }

    public static void validaToken(Context ctx, GeradorToken geradorToken) {
        Gson gson = new GsonBuilder().create();
        try {
            TokenRequest tokenRequest = gson.fromJson(ctx.body(), TokenRequest.class);
            boolean tokenValido = geradorToken.validaTokenJwt(tokenRequest.getToken());
            if (tokenValido) {
                ctx.status(200);
            } else {
                ctx.status(401);
                ErrorResponse errorResponse = new ErrorResponse(401, "Usuário não autenticado!");
                ctx.json(gson.toJson(errorResponse));
            }
        } catch (Exception e) {
            ctx.status(401);
            ErrorResponse errorResponse = new ErrorResponse(400, "Erro ao validar token!");
            ctx.json(gson.toJson(errorResponse));
        }
    }
}
