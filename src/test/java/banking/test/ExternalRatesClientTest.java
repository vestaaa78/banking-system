package banking.test;

import banking.export.ExternalRatesClient;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ExternalRatesClientTest {

    private HttpServer server;
    private ExternalRatesClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(
                new InetSocketAddress("localhost", 0), 0
        );

        server.createContext("/success", exchange -> {
            byte[] body = "Rates loaded successfully"
                    .getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        server.createContext("/error", exchange -> {
            byte[] body = "Not found"
                    .getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(404, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        server.start();

        baseUrl = "http://localhost:" +
                server.getAddress().getPort();

        client = new ExternalRatesClient();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldReturnResponseBodyForSuccessfulRequest()
            throws IOException, InterruptedException {

        String result = client.get(baseUrl + "/success");

        assertEquals("Rates loaded successfully", result);
    }

    @Test
    void shouldThrowExceptionForNon2xxStatus() {

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> client.get(baseUrl + "/error")
        );

        assertEquals("HTTP error: 404", exception.getMessage());
    }
}
