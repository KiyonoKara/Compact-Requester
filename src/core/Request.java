package core;

// Data streaming
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.IOException;

// Networking and HTTP/HTTPS
import java.net.HttpURLConnection;
import java.net.URL;

// Charsets
import java.nio.charset.StandardCharsets;

// Utilities
import java.util.zip.GZIPInputStream;

// Constants
import core.constants.Constants;

public class Request {
    public static void main(String[] args) {
        Request request1 = new Request("https://docs.scala-lang.org/", "GET", null);
        Request request2 = new Request("https://reqres.in/api/users", "POST", "{\"name\": \"Li Xi\", \"job\": \"Java POST\"}");
        System.out.println(request1.output);
        System.out.println(request2.output);
    }
    public String method;
    public String url;
    public String output;

    public Request(String url) {
        this.url = url;
        this.method = Constants.GET;

        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod(this.method);

            connection.setConnectTimeout(Constants.STANDARD_TIMEOUT);
            connection.setReadTimeout(Constants.STANDARD_TIMEOUT);

            this.output = read(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request(String url, String method, String data) {
        this.url = url;
        this.method = method.toUpperCase();

        if (data == null) data = "{}";

        HttpURLConnection connection;

        if (this.method.equals(Constants.POST) || this.method.equals(Constants.DELETE) || this.method.equals(Constants.PUT) || this.method.equals(Constants.PATCH)) {
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();

                connection.setRequestMethod(this.method);

                connection.setConnectTimeout(Constants.STANDARD_TIMEOUT);
                connection.setReadTimeout(Constants.STANDARD_TIMEOUT);

                connection.setDoOutput(true);

                try {
                    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
                    int length = bytes.length;
                    connection.setFixedLengthStreamingMode(length);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(bytes, 0, length);
                    outputStream.flush();
                    outputStream.close();
                } finally {
                    this.output = read(connection);
                    connection.getInputStream().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Request readOnly = new Request(url);
            this.output = readOnly.output;
        }
    }

    public static String read(HttpURLConnection connection) {
        InputStream connectionInputStream = null;
        try {
            connectionInputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Reader reader = null;
        if (connection.getContentEncoding() != null) {
            try {
                assert connectionInputStream != null;
                reader = new InputStreamReader(new GZIPInputStream(connectionInputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                reader = new InputStreamReader(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Empty char value
        int ch;

        // String Builder to add to the final string
        StringBuilder stringBuilder = new StringBuilder();

        // Appending the data to a String Builder
        while (true) {
            try {
                assert reader != null;
                ch = reader.read();
                if (ch == -1) {
                    return stringBuilder.toString();
                }

                stringBuilder.append((char) ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
