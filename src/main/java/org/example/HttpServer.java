package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.Class.forName;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        boolean flag = true;
        while (flag) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = null;
            boolean firstLine = true;
            String request = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (firstLine) {
                    String[] firstlist = inputLine.split(" ");
                    try {
                        request = firstlist[1].split("=")[1];
                        request = request.split("\\(")[1];
                        request = request.split("\\)")[0];
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    firstLine = false;
                }if (!in.ready()) {
                    break;
                }
            }
            if (!Objects.equals(request, "")){
                try{
//                    System.out.println("entrooooo");
                    String[] requests = request.split(",");
                    if(requests.length==1) {
                        outputLine = getHeader() + getClass(request);
                    }else if(requests.length==2){
                        outputLine = getHeader() + getInvoke(request);
                    }
                }catch (Exception e){
//                    outputLine = getHtml();
                }
            }else {
                outputLine = getHtml();
            }


            out.println(outputLine);
            out.close();
            in.close();
        }
        clientSocket.close();
        serverSocket.close();
    }

    public static String getHtml(){
        return  "HTTP/1.1 200 OK\n\r"+
                "Content-type: text/html\n\r"+
                "\n\r"+
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Reflective ChatGPT GET</h1>\n" +
                "        <form action=\"/hello\">\n" +
                "            <label for=\"name\">Path:</label><br>\n" +
                "            <input type=\"text\" id=\"name\" name=\"name\" value=\"hola\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "        </form> \n" +
                "        <div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "        <script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/consulta?comando=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "\n" +
//                "        <h1>Form with POST</h1>\n" +
//                "        <form action=\"/hellopost\">\n" +
//                "            <label for=\"postname\">Name:</label><br>\n" +
//                "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\n" +
//                "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\n" +
//                "        </form>\n" +
//                "        \n" +
//                "        <div id=\"postrespmsg\"></div>\n" +
//                "        \n" +
//                "        <script>\n" +
//                "            function loadPostMsg(name){\n" +
//                "                let url = \"/hellopost?name=\" + name.value;\n" +
//                "\n" +
//                "                fetch (url, {method: 'POST'})\n" +
//                "                    .then(x => x.text())\n" +
//                "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = name.value);\n" +
//                "            }\n" +
//                "        </script>\n" +
                "    </body>\n" +
                "</html>";
    }


    public static String getHeader(){
        return "HTTP/1.1 200 OK\n\r"+
                "Content-type: text/html\n\r"+
                "\n\r";
    }
    public static String getClass(String clasS) throws ClassNotFoundException {
        Class<?> c = Class.forName(clasS);
        return c.getName().toString();
    }

    public static String getInvoke(String invoke) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> c = Class.forName(invoke.split(",")[0]);
        Method m = c.getDeclaredMethod(invoke.split(",")[1], (Class<?>) null);
        System.out.println(m.toString());
        return m.toString();
    }

}