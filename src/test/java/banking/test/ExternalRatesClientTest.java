package banking.test;

import banking.dto.RateDto;
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
                new InetSocketAddress("localhost", 0),
                0
        );

        server.createContext("/rate", exchange -> {

            String json = """
                    {
                        "currency": "USD",
                        "value": 92.5
                    }
                    """;

            byte[] body = json.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json"
            );

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

        baseUrl = "http://localhost:"
                + server.getAddress().getPort();

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

        String result = client.get(baseUrl + "/rate");

        assertTrue(result.contains("USD"));
        assertTrue(result.contains("92.5"));
    }

    @Test
    void shouldConvertJsonToRateDto()
            throws IOException, InterruptedException {

        RateDto rate = client.getRate(baseUrl + "/rate");

        assertNotNull(rate);

        assertEquals("USD", rate.currency());

        assertEquals(92.5, rate.value());
    }

    @Test
    void shouldThrowExceptionForNon2xxStatus() {

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> client.getRate(baseUrl + "/error")
        );

        assertEquals(
                "HTTP error: 404",
                exception.getMessage()
        );
    }
}
