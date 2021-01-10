package dev.gianlu;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String field;
    private static String elementId;
    private static String dirPath;
    private static String charSet;


    private static List<String> fileList(String directory) throws IOException {
        List<String> files = new ArrayList<>();
        Files.list(new File(directory).toPath()).forEach(path->{
            files.add(path.toString());
        });
        return files;
    }
    private static String parseJson(String fName) throws FileNotFoundException {
        String occurrence="";
        JSONParser parser = new JSONParser();
        Reader reader = new FileReader(fName);
        try {
            JSONObject obj = (JSONObject) parser.parse(reader);
            occurrence=obj.get(field).toString();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return occurrence;
    }
    private static void processAll(String ext){
        Path startDir=Paths.get(dirPath);
        Integer i=0;
        String response="";
        try {
            for(String res: fileList(dirPath)){
                if(res.matches(".*\\."+ext+"$")) {
                    i++;

                    if(ext=="json"){
                        response=parseJson(startDir.resolve(res).toString());
                    }
                    if(ext=="html"){
                        response=fromHtml(startDir.resolve(res).toString());
                    }
                    if(i%100==0){
                        System.out.print(i);
                        System.out.println(" "+response);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(i);


    }
    private static String fromHtml(String fName) throws IOException {
        File input = new File(fName);
        Document doc = (Document)Jsoup.parse(input, charSet);
        Element elem = doc.getElementById(elementId);
        String titolo = elem.text();
        return titolo;
    }
    public static void main(String[] args)  {

        if(args.length<4){
            System.out.println("Please use as: CMD 'dirpath' 'field' 'elementid' 'charset'");
            return;
        }

        dirPath = args[0];
        field = args[1];
        elementId = args[2];
        charSet = args[3];

        String []extensions=new String[]{"html","json"};

        for(String extension: extensions){
            processAll(extension);
        }

    }
}
