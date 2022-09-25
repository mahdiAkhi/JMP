package javaParser;


import com.github.javaparser.ast.CompilationUnit;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class App {


    public static void main(String[] args) throws IOException {
        /**
         * Create a Directory obj for OS walk
         * @arg path: path to project root
         */
        Directory dir = new Directory("E:\\Papers\\Thesis\\code\\repo scrapper\\repo\\dubbo");
//        a list of java files in project
        List<Path> javaFiles = dir.getJavaFiles();


        /**
         * each project saves in a json file as an array of json object like below
         * [
         *      {
         *      longName: packageName,
         *      methods:[
         *          {
         *              signature: signature,
         *              comments: comments,
         *              body: body
         *              }
         *      ]
         *
         *      }
         * ]
         */

//        model project object(json file)
        String project = "[";

//        model each file class
        String fileClass = "";

//        retrieving all file in project
        for (Path javaFile : javaFiles) {
            System.out.println(javaFile);
//          get each java file to extractor for extracting methods
            Extractor extractor = new Extractor(javaFile);
//          compilation unit for soft working with extractor output
            CompilationUnit cu = extractor.getCompilationUnit();

            if(cu.getTypes().size()<1){
                continue;
            }
            if( !cu.getType(0).isClassOrInterfaceDeclaration()) {
                continue;
            }

            if(cu.getType(0).asClassOrInterfaceDeclaration().isInterface())
                continue;

//          create longName
            String packageName = getPackageName(cu);
            String className = getClassName(cu);
            String longName = "{\"longName\":" + "\"" + packageName + "." + className + "\",";
            fileClass += longName + '\n';

            fileClass += "\"methods\":[";

//          an hashtable to store method objects
            var methods = extractor.extractMethods();

            if(methods.size()<1){
                fileClass+="]";
            }else{
            // convert hashtable to json object
            JSONObject converter = new JSONObject(methods);
            fileClass += converter + ",";
            }


            fileClass = fileClass.substring(0, fileClass.length() - 1);
            fileClass += "]},\n";

        }
        fileClass = fileClass.substring(0, fileClass.length() - 1);

        project +=fileClass+"\n]";
        File jsonoFile = new File("E:\\Papers\\Thesis\\code\\repo scrapper\\repo-scrapper\\src\\main\\java\\javaParser\\jsons\\dubbo1.json");
        jsonoFile.createNewFile();

        FileWriter writer = new FileWriter(jsonoFile);
        writer.write(project);
        writer.close();
    }

    private static String getPackageName(CompilationUnit cu) {
        String packageName="";
        if(!cu.getPackageDeclaration().equals(Optional.empty())){
            packageName = cu.getPackageDeclaration().get().toString();
            packageName =packageName.split(" ")[1].replace(";", "").trim();
        }else{
            packageName = getClassName(cu);
        }
        return packageName;
    }

    private static String getClassName(CompilationUnit cu) {
        if(cu.getType(0).isClassOrInterfaceDeclaration()){
            return cu.getType(0).asClassOrInterfaceDeclaration().getName().toString().trim();
        }
        return "interface";

    }
}