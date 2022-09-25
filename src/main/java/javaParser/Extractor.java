package javaParser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class Extractor {

    private final Path path;
    public CompilationUnit cu;
    private String code = null;

    public Extractor(Path path) {
        this.path = path;
        this.readFile();
        this.cu = StaticJavaParser.parse(this.code);
    }


    public CompilationUnit getCompilationUnit() {

        return this.cu;
    }

    private void readFile() {

        File file = new File(this.path.toString());
        String text = "";

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                text += scanner.nextLine().replaceAll("\n", "") + "\n";
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        this.code = text;

    }

    public String getCode() {
        if (code == null) {
            this.readFile();
        }
        return this.code;
    }

    public Hashtable<String, JSONObject> extractMethods() {

        if (this.code == null)
            this.readFile();

        Hashtable<String, JSONObject> methods = new Hashtable<>();


        var methodList = cu.findAll(MethodDeclaration.class);
        for (int i = 0; i < methodList.size(); i++) {

            var signature = methodList.get(i).getDeclarationAsString();
            var comment = (methodList.get(i).hasJavaDocComment()) ? methodList.get(i).getJavadoc().get().toString() : "";

            String body = "";
            if (!methodList.get(i).getBody().equals(Optional.empty())) {
                body = methodList.get(i).getBody().get().toString();
            }
            var method = methodList.get(i).toString();
            var a = new Method(
                    signature,
                    comment,
                    body,
                    method
            );
            Gson gson = new Gson();
            methods.put(i + "", new JSONObject(gson.toJson(a)) );
        }
        return methods;
    }
}


