package com.quicktutorialz.javalin;

import io.javalin.Context;
import io.javalin.Javalin;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MainApplication {

    private static Logger log = LoggerFactory.getLogger(MainApplication.class);

    private static Map<String, String> serviceMapping = new HashMap<>();
    static {
        serviceMapping.put("javalin-api", "http://localhost:7000");
        serviceMapping.put("another-service", "http://service.host.address");
    }


    public static void main(String[] args) {



        Javalin app = Javalin.create()
                .contextPath("/javalin-api-gateway")
                .enableAutogeneratedEtags()
                .enableCorsForOrigin("*")
              //  .enableDebugLogging()
              //  .enableStaticFiles("/public")
                .start(4000);



        app.get("/*", ctx->ctx.result("Hello world!"));

        app.post("/*", ctx-> {
            ctx.result(CompletableFuture.supplyAsync(() -> post(getServiceUrl(ctx), ctx.headerMap(), "application/json", ctx.bodyAsBytes()) ));
        });

        app.put("/*", ctx->ctx.result("Hello world!"));

        app.delete("/*", ctx->ctx.result("Hello world!"));

        app.patch("/*", ctx->ctx.result("Hello world!"));

        app.options("/*", ctx->ctx.result("Hello world!"));

        app.head("/*", ctx->ctx.result("Hello world!"));

        app.trace("/*", ctx->ctx.result("Hello world!"));

        app.connect("/*", ctx->ctx.result("Hello world!"));


        //completableFutureResponse
        app.post("/completable/you", ctx->{
    //        ctx.json( CompletableFuture.supplyAsync(()-> ctx.bodyAsClass(User.class)) );
        });

        //pathParam
        app.get("/hello/:name", ctx->{
            String name  = ctx.pathParam("name");
            ctx.result("Hello " + name);
        });

        //queryParam
        app.get("/hi", ctx->{
            String name  = ctx.queryParam("name");
            ctx.result("Hello " + name);
        });

        //formParam
        app.post("/ola", ctx->{
            String name  = ctx.formParam("name");
            String surname  = ctx.formParam("surname");
            ctx.result("Hello " + name + " " + surname);
        });


        //filtering all
        app.before(ctx->{log.info(ctx.path());});

        //filtering a Path
        app.before("/you/*", ctx->{log.info(ctx.ip());});

        //after Responding to a Path
        app.after("/you/*", ctx->{log.info(ctx.url());});

    }

    private static String getServiceUrl(final Context ctx) {
        String[] splittedUrl = ctx.url().split("/javalin-api-gateway");
        String serviceName = splittedUrl[1].split("/")[1];
        String queryString = ctx.queryString()!=null ? "?".concat(ctx.queryString()) : "";
        return serviceMapping.get(serviceName).concat(splittedUrl[1]).concat(queryString);
    }

    public static InputStream post(String url, Map<String, String> headers, String mediaType, byte[] content) {
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(composePostRequest(url, headers, mediaType, content)).execute();
            return response.body().byteStream();
        } catch (IOException e) {
            return new ByteArrayInputStream(e.getMessage().getBytes());
        }

    }








    @NotNull
    private static Request composePostRequest(String url, Map<String, String> headers, String mediaType, byte[] content) {
        return new Request.Builder()
                    .url(url)
                    .headers(Headers.of(headers))
                    .post(RequestBody.create(MediaType.parse(mediaType), content))
                    .build();
    }


}
