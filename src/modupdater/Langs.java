package modupdater;

import arc.files.Fi;
import arc.func.Cons;
import arc.func.ConsT;
import arc.struct.*;
import arc.util.*;
import arc.util.Http.*;

import arc.util.serialization.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static arc.struct.StringMap.of;


public class Langs {
    static final String githubToken = OS.prop("githubtoken");

    public static void main(String[] args) throws IOException {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");

//        Langs langs = new Langs();
        Fi file = new Fi("mods.json");
//        System.out.println();
//        Jval list = Jval.read();
//
        String str = new String(file.read().readAllBytes(), StandardCharsets.UTF_8);
        Jval list = Jval.read(str);
        list.asArray().forEach(e -> {
            System.out.println(e.get("repo"));
            if (e.get("languages") == null)
                e.add("languages", getLanguages(String.valueOf(e.get("repo"))));
        });
        System.out.println(list.asString());
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");


//        System.out.printf("Your languages: " + langs.getLanguages("sk7725/BetaMindy"));
    }
    void simpleError(Throwable error){
        if(!(error instanceof HttpStatusException)){
            Log.info("&lc |&lr" + Strings.getSimpleMessage(error));
        }
    }

    public static String getLanguages(String name) {
        Seq<String> languages = Seq.with();

        query(name, resultik -> {
            String myStr = resultik.toString();
            if (myStr.contains("bundle_uk_UA.properties")) {
                System.out.println("~~~\nUkrainian is here\n~~~");
                languages.add("uk_UA");
            }
            System.out.println(resultik);
        });

        if (languages.size == 0)
            return "";
        else
            return languages.toString();
    }
    static void query(String url, Cons<Jval> cons){
        Http.get("https://api.github.com/repos/" + url + "/git/trees")
                .timeout(10000)
                .method(HttpMethod.GET)
                .header("authorization", githubToken)
                .header("accept", "application/vnd.github.baptiste-preview+json")
//                .error(this::handleError)
                .block(response -> {
                    Log.info("&lcSending search query. Status: @; Queries remaining: @/@", response.getStatus(), response.getHeader("X-RateLimit-Remaining"), response.getHeader("X-RateLimit-Limit"));
                    cons.get(Jval.read(response.getResultAsString()));
                });
    }
}