import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private final int PORT;
    private final Map<String, Map<String, Handler>> handlers;

    public Server(int PORT) {
        this.PORT = PORT;
        this.handlers = new ConcurrentHashMap<>();
    }

    public void listen() throws IOException {
        int MAX_POOL = 64;
        ServerSocket serverSocket = new ServerSocket(PORT);
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_POOL);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, handlers);
            threadPoolExecutor.execute(clientHandler);
        }
    }

    void addHandler(String method, String path, Handler handler) {
        if (handlers.get(method) == null) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);
    }
}
