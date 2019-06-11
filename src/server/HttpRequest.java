package server;

import dao.ConnectionBD;
import model.LogRequest;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest extends Thread {

    private static final String BASE_PATH = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String BASE_PATH_PRIVATE_PAGES = BASE_PATH + "\\src\\pages";
    private static final String BASE_PATH_PUBLIC_PAGES = BASE_PATH + "\\public\\pages";
    private static final String BASE_PATH_PRIVATE = BASE_PATH + "\\src";
    private static final String BASE_PATH_PUBLIC = BASE_PATH + "\\public";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_HEAD = "HEAD";
    private List<String> request = new ArrayList<>();
    private String httpMethod;
    private String requestedPath;
    private String requestedOriginPath;
    private String requestedFile;
    private String requestedFileExtension = "html";
    private boolean accessPrivate = false;
    private String FILE_DEFAULT = "index.html";
    private int statusCode = 500;
    private String route;

    Connection conn = null;
    LogRequest log = null;

    String ip;
    HttpResponse response;
    Socket connectedClient;
    BufferedReader requestInformation;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public HttpRequest(Socket client) {
        response = new HttpResponse(client);
        connectedClient = client;
    }

    private void makeRequest(String lineRequest) {
        request.add(lineRequest);
        if(request.size() > 2) {
            String[] itemsRequest = request.get(0).split(" ");
            setHttpMethod(itemsRequest[0]);
            setRequestedPath(itemsRequest[1]);
            setRequestedOriginPath(itemsRequest[1]);
        }
    }

    private String getHttpMethod() {
        return httpMethod;
    }

    private void setHttpMethod(String httpMethod){
        this.httpMethod = httpMethod;
    }

    private String getRequestedPath() {
        return requestedPath;
    }

    private void setRequestedPath(String requestedPath) {
        String REGEX = "\"([/])\"";
        this.requestedPath = requestedPath.replaceAll(REGEX, "\\");
    }

    private String getRequestedOriginPath(){
        return this.requestedOriginPath;
    }

    private void setRequestedOriginPath(String path){
        this.requestedOriginPath = path;
    }

    private String getRequestedFile(){
        return this.requestedFile;
    }

    private void setRequestedFile(String requestedFile){
        this.requestedFile = requestedFile;
    }

    private String getRequestedFileExtension(){
        return this. requestedFileExtension;
    }

    private void setRequestedFileExtension(String extension){
        this.requestedFileExtension = extension;
    }

    private void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }

    private int getStatusCode(){
        return this.statusCode;
    }

    private void printRequest() {
        System.out.println("---------------------REQUEST---------------------------------");
        for (String s : request) {
            System.out.println(s);
        }
        System.out.println("------------------------------------------------------");
    }

    private void defineRender(String extension) {
        switch (extension) {
            case "html":
                response.setContentType("text/html");
                route = BASE_PATH_PRIVATE_PAGES + getRequestedPath();
                break;

            case "css":
                response.setContentType("text/css");
                route = BASE_PATH_PUBLIC + "\\assets\\" + getRequestedPath();
                break;

            case "js":
                response.setContentType("application/javascript");
                route = BASE_PATH_PUBLIC + "\\assets\\" + getRequestedPath();
                break;

            case "png":
                response.setContentType("image/png");
                route = BASE_PATH_PUBLIC + "\\assets\\" + getRequestedPath();
                break;

            case "jpg":
                response.setContentType("image/jpeg");
                route = BASE_PATH_PUBLIC + "\\assets\\" + getRequestedPath();
                break;

            case "jpeg":
                response.setContentType("image/jpeg");
                route = BASE_PATH_PUBLIC + "\\assets\\" + getRequestedPath();
                break;

            case "ico":
                response.setContentType("image/x-icon");
                route = BASE_PATH_PUBLIC + "\\assets\\" + getRequestedPath();
                break;

            default:
                response.setContentType("text/html");
                route = BASE_PATH_PUBLIC_PAGES + "\\errors\\error-404.html";
                break;
        }
    }

    private String fileIsPrivateMessage = "ERRO 404 - ESSA PÁGINA NÃO EXISTE (contexto privado)";
    private int fileSprivateStatusCode = 404;

    private File validExistisFile(String path) throws SQLException, IOException {
        File publicFile = new File(BASE_PATH_PUBLIC_PAGES + path);
        File publicFileWithExtension = new File(BASE_PATH_PUBLIC_PAGES + path + ".html");
        File privateFile = new File(BASE_PATH_PRIVATE_PAGES + path);
        File privateFileWithExtension = new File(BASE_PATH_PRIVATE_PAGES + path +".html");
        File indexFile = new File(BASE_PATH_PUBLIC + path);
        File fileResult = null;

        System.out.println("publicFile" + BASE_PATH_PUBLIC_PAGES + path);
        System.out.println("publicFileWithExtension" + BASE_PATH_PUBLIC_PAGES + path + ".html");
        System.out.println("privateFile" + BASE_PATH_PRIVATE_PAGES + path);
        System.out.println("privateFileWithExtension" + BASE_PATH_PRIVATE_PAGES + path + ".html");
        System.out.println("indexFile" + BASE_PATH_PUBLIC + path);

        System.out.println("EXISTE: " + privateFileWithExtension.exists());


        if(privateFile.exists() || privateFileWithExtension.exists()){

            if(privateFile.exists()){
                if(privateFile.isDirectory()){
                    fileIsPrivateMessage = "ERRO 403 - ACESSO NEGADO AO DIRETÓRIO PRIVADO";
                    fileSprivateStatusCode = 403;
                }
            }

            if(privateFileWithExtension.exists()){
                if(privateFileWithExtension.isFile()){
                    fileIsPrivateMessage = "SUCCESS - ARQUIVO ENCONTRADO (contexto privado)";
                    fileSprivateStatusCode = 200;

                    DynamicPage dynamicPages = new DynamicPage();
                    dynamicPages.mostAccessedPageReport(privateFileWithExtension);

                    fileResult = new File(privateFileWithExtension.getAbsolutePath());
                }
            }
        }

        if(publicFile.exists() || publicFileWithExtension.exists()){

            if(publicFile.exists()){
                if(publicFile.isDirectory()){
                    fileIsPrivateMessage = "ERRO 403 - ACESSO NEGADO (contexto publico)";
                    fileSprivateStatusCode = 403;
                }

                if(publicFile.isFile()){
                    fileIsPrivateMessage = "ARQUIVO : " + publicFile.getName() + " ENCONTRADO (contexto publico)";
                    fileSprivateStatusCode = 200;
                    fileResult = publicFile;
                }
            }

            if(publicFileWithExtension.exists()){
                if(publicFileWithExtension.isFile()){
                    fileIsPrivateMessage = "ARQUIVO " + publicFile.getName() + " ENCONTRADO (contexto publico)";
                    fileSprivateStatusCode = 200;
                    fileResult = publicFileWithExtension;
                }
            }
        }

        if(indexFile.exists()){
            if(indexFile.isFile()){
                fileIsPrivateMessage = "ARQUIVO : " + publicFile.getName() + " ENCONTRADO (contexto publico)";
                fileSprivateStatusCode = 200;
                fileResult = indexFile;
            }
        }

        setStatusCode(fileSprivateStatusCode);
        System.out.println(fileIsPrivateMessage);

        return fileResult;
    }

    public void run() {
        byte[] fileInBytes = new byte[0];
        int numOfBytes = 0;

        try {
            System.out.println("Client " + connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " connected!");
            setIp(""+connectedClient.getInetAddress());
            requestInformation = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
            makeRequest(requestInformation.readLine());
            while (requestInformation.ready()) {
                makeRequest(requestInformation.readLine());
            }

//			printRequest();

            if(getRequestedPath().equals("/")){
                setRequestedPath(FILE_DEFAULT);
            }

            int positionPoint = getRequestedPath().lastIndexOf(".");
            int positionBarra = getRequestedPath().lastIndexOf("/");

            String nameFile  = getRequestedPath().substring(positionBarra+1);
            setRequestedFile(nameFile);

            if(positionPoint != -1) {
                String extension = getRequestedPath().substring(positionPoint+1);

                setRequestedFileExtension(extension);

                if(getRequestedFileExtension().equals("html")){
                    setRequestedFile(getRequestedPath().substring(positionBarra+1,positionPoint));
                }
            }

            this.defineRender(getRequestedFileExtension());

            System.out.println(" ");
            System.out.println("URL: " + getRequestedPath());
            System.out.println("EXTENSION: " + getRequestedFileExtension());
            System.out.println("FILE OR DIRECTORY: " + getRequestedFile());

            File page = validExistisFile(getRequestedPath());

            System.out.println("deu certo???");
            System.out.println(page.getAbsolutePath());

			System.exit(0);

            numOfBytes = (int)page.length();
            FileInputStream infile = new FileInputStream(page.getAbsolutePath());
            fileInBytes = new byte[numOfBytes];
            infile.read();

//			switch (getHttpMethod()) {
//			case METHOD_GET:
//
//				break;
//			case METHOD_HEAD:
//
//				break;
//			default:
//
//				break;
//			}

            conn = ConnectionBD.conectar();

            log = new LogRequest(getRequestedOriginPath(), getHttpMethod(), getIp(), getStatusCode());
            log.register(conn);
            response.send(fileInBytes, numOfBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
