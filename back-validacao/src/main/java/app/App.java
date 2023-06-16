package app;

import controller.AutenticacaoController;
import controller.DividaController;
import io.javalin.Javalin;
import utils.GeradorToken;

import static io.javalin.apibuilder.ApiBuilder.*;

public class App {

    private static final int PORT = 7000;

    public static void main(String[] args) {
        GeradorToken geradorToken = new GeradorToken();

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
        }).start(PORT);
        app.routes(() -> {
            path("autenticacao", () -> {
                get(ctx -> {
                    ctx.status(200);
                    ctx.result("Server up");
                });
                post("autenticar", ctx -> {
                    AutenticacaoController.fazerLogin(ctx, geradorToken);
                });
                post("validar", ctx -> {
                    AutenticacaoController.validaToken(ctx, geradorToken);
                });
            });
            path("cliente", () -> {
                before(ctx -> {
                    String token = ctx.header("AUTH");
                    if(!geradorToken.validaTokenJwt(token)) {
                        ctx.status(401);
                    }
                });
                get(ctx -> {
                    String cpf = ctx.queryParam("cpf");
                    DividaController.procuraCliente(ctx, cpf);
                });
            });
            path("divida", () -> {
                before(ctx -> {
                    String token = ctx.header("AUTH");
                    if(!geradorToken.validaTokenJwt(token)) {
                        ctx.status(401);
                    }
                });
                post("cadastrar", ctx -> {
                    DividaController.cadastrarDivida(ctx);
                });
                delete("deletar", ctx -> {
                    DividaController.deletarDivida(ctx);
                });
                get("all", ctx -> {
                    DividaController.buscarTodasDividas(ctx);
                });
                get("comprovante", ctx -> {
                    DividaController.baixarComprovante(ctx);
                });
                get(ctx -> {
                    DividaController.procurarDividaPorId(ctx);
                });
                post("atualizar", ctx -> {
                    DividaController.atualizarDivida(ctx);
                });
            });
        });
    }
}
