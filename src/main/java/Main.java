import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
//    public static List<String> validPaths = List.of("/page.html", "/index.html", "/spring.svg", "/spring.png");

    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);
        server.listen();
        server.addHandler("GET", "/page.html", ((request, out) -> {
            final var filePath = Path.of(".", "public", request.getPathRequest());
            final String mimeType;
            try {
                mimeType = Files.probeContentType(filePath);
                final long length;
                length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: 0\r\n" + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }));
    }
}