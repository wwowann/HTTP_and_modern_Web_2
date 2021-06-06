import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Map<String, Map<String, Handler>> handlers;
    private final Handler notFoundHandler = ((request, out) -> {
        try {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    public ClientHandler(Socket socket, Map<String,
            Map<String, Handler>> handlers) {
        this.socket = socket;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream());) {
            Request request = Request.getParseRequest(in);
            var handlesMap = handlers.get(request.getMethodRequest());
            if (handlesMap == null) {
                notFoundHandler.handle(request, out);
                return;
            }
            var handler = handlesMap.get(request.getPathRequest());
            if (handler == null) {
                notFoundHandler.handle(request, out);
                return;
            }
            handler.handle(request, out);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}