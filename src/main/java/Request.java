import java.io.*;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String methodRequest;
    private final String patchRequest;
    private final Map<String, String> headers;
    private final String bodyRequest;

    private Request(String methodRequest, String patchRequest,
                   Map<String, String> headers, String bodyRequest) {
        this.methodRequest = methodRequest;
        this.patchRequest = patchRequest;
        this.headers = headers;
        this.bodyRequest = bodyRequest;
    }

    public static Request getParseRequest(BufferedReader in) throws IOException {
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("некорректный запрос");
        }
        var methodRequest = parts[0];
        var patchRequest = parts[1];
        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = in.readLine()).equals("")) {
            //далее парсим хедеры
            int i = line.indexOf(":");
            var headerName = line.substring(0, i);// выбираем имя хедера - до ":"
            var headerValue = line.substring(i + 2);// выбираем значение хедера - после ":"
            headers.put(headerName, headerValue);//добавляем в мапу строку с хедером
        }
        if (methodRequest.equals("GET")) {//Если метод GET, то возвращаем спарсенный запрос без тела
            return new Request("GET",patchRequest,headers,null);
        }
        var contentLength = headers.get("Content-Length"); //обращаемся к хедеру с контентом
        final var length = contentLength.length(); //определяем размер тела запроса
        final var bodyBytes = in.read(CharBuffer.allocate(length)); //считываем размер в байтах
        String bodyRequest = String.valueOf(bodyBytes); //считыем тело запроса body
        return new Request(methodRequest,patchRequest,headers,bodyRequest); //возвращаем распарсенный запрос с телом
    }

    public String getMethodRequest() {
        return methodRequest;
    }

    public String getPathRequest() {
        return patchRequest;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBodyRequest() {
        return bodyRequest;
    }
}
