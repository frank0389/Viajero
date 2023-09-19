package ec.viajero.IdentityServer.util;

import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PostgresJsonTest {
    
    @Test
    void serializeAndDeserialize() throws IOException {
        String str = "{\"a\":\"aaaaa\",\"c\":\"ccccc\", \"list\":[1, 23.4, 54], \"e\":{\"list\":[{\"a\":\"aaaaa\",\"c\":\"ccccc\"},{\"a\":\"aaaaa\",\"c\":\"ccccc\"}]}}";
        Json pjson = Json.of(str);
        String encodeJson = pjson.asString();
        log.info(encodeJson);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(str);
        ((ObjectNode) node).put("a","111111");
        String newJson = mapper.writeValueAsString(node);
        log.info(newJson);
        assertThat(str).isEqualTo(encodeJson);
    }
}
