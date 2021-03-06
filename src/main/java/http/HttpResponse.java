package http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    List<String> headers = new ArrayList<>();
    private final static String  VERSION ="HTTP/1.1";

    byte[] body;

    public HttpResponse(HttpRequest req) {
        final String method = req.getMethod();
        switch (method) {
            case "GET":
                String url = req.getUrl();
                Path path = Paths.get(".", url);
                if (!Files.exists(path)) {
                    fillHeaders(HttpStatus.NOT_FOUND);
                    fillBody("<h1>The requested resource is not found</h1>");
                    return;
                }
                if (Files.isDirectory(path)) {
                    //show html listing for directory
                    fillHeaders(HttpStatus.OK);
                    fillBody("<h1>Unsupported operation exception</h1>");
                } else {
                    sendFile(path);
                }
                break;
            case "POST":
                System.out.println("post request");
                break;
            default:
                System.out.println("neither get, nor post");
        }

    }

    public void write(OutputStream os) throws IOException {
        //write headers
        headers.forEach(s -> writeString(os, s));
        //write empty line
        writeString(os, "");
        //write body
        os.write(body);
    }

    private void writeString(OutputStream os, String str) {
       try {
            os.write((str + "\r\n").getBytes(StandardCharsets.UTF_8));
       } catch (IOException e) {
            e.printStackTrace();
       }

    }

    private void fillHeaders(HttpStatus status) {
        headers.add(VERSION + " " + status);
        headers.add("simple http server");
        headers.add("Connection: close");
    }

    private void fillBody(String str) {
        body = str.getBytes(StandardCharsets.UTF_8);
    }

    private void sendFile(Path path) {
        try {
            body = Files.readAllBytes(path);
            fillHeaders(HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            fillHeaders(HttpStatus.SERVER_ERROR);
            fillBody("<p>Error showing file</p>");
        }
    }
}
