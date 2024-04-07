package basket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BasketSplitterTest {

    private BasketSplitter splitter;
    private List<String> testItems1;
    private Map<String, List<String>>  testItemsResult1;
    private List<String> testItems2;
    private Map<String, List<String>>  testItemsResult2;
    private List<String> testItems3;
    private Map<String, List<String>>  testItemsResult3;
    private List<String> sameItemsBasket;

    @BeforeEach
    public void setUp() throws IOException {
        String absolutePathToConfigFile = "src/main/resources/config.json"; // Update path accordingly
        splitter = new BasketSplitter(absolutePathToConfigFile);

        ObjectMapper objectMapper = new ObjectMapper();
        testItems1 = objectMapper.readValue(new File("src/main/resources/basket-1.json"), new TypeReference<>() {});
        testItemsResult1 = objectMapper.readValue(new File("src/main/resources/basket-1-result.json"), new TypeReference<>() {});
        testItems2 = objectMapper.readValue(new File("src/main/resources/basket-2.json"), new TypeReference<>() {});
        testItemsResult2 = objectMapper.readValue(new File("src/main/resources/basket-2-result.json"), new TypeReference<>() {});
        testItems3 = objectMapper.readValue(new File("src/main/resources/basket-3.json"), new TypeReference<>() {});
        testItemsResult3 = objectMapper.readValue(new File("src/main/resources/basket-3-result.json"), new TypeReference<>() {});
        sameItemsBasket = objectMapper.readValue(new File("src/main/resources/sameItemsBasket.json"), new TypeReference<>() {});

    }

    @Test
    public void testSplitWithEmptyInput() {
        List<String> emptyList = Collections.emptyList();
        Map<String, List<String>> result = splitter.split(emptyList);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSplitWithEmptyDeliveryOptions() throws IOException {
        splitter = new BasketSplitter("src/main/resources/emptyConfig.json");
        Map<String, List<String>> result = splitter.split(testItems1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSplitWithItemsNotFoundInDeliveryOptions() {
        List<String> items = List.of("Red", "Blue", "Green");
        Map<String, List<String>> result = splitter.split(items);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSplitSameItemsBasket() {
        Map<String, List<String>> result = splitter.split(sameItemsBasket);

        assertAll(
                () -> assertEquals(sameItemsBasket , result.get("Pick-up point"))
        );
    }

    @Test
    public void testSplitWithBasket1() {
        Map<String, List<String>> result = splitter.split(testItems1);

        assertAll(
                () -> assertEquals(testItemsResult1.get("Pick-up point"), result.get("Pick-up point")),
                () -> assertEquals(testItemsResult1.get("Courier"), result.get("Courier"))
        );
    }

    @Test
    public void testSplitWithBasket2() {
        Map<String, List<String>> result = splitter.split(testItems2);

        assertAll(
                () -> assertEquals(testItemsResult2.get("Same day delivery"), result.get("Same day delivery")),
                () -> assertEquals(testItemsResult2.get("Courier"), result.get("Courier")),
                () -> assertEquals(testItemsResult2.get("Express Collection"), result.get("Express Collection"))
        );
    }

    @Test
    public void testSplitWithBasket3() {
        Map<String, List<String>> result = splitter.split(testItems3);

        assertAll(
                () -> assertEquals(testItemsResult3.get("Express Delivery"), result.get("Express Delivery")),
                () -> assertEquals(testItemsResult3.get("Courier"), result.get("Courier"))
        );
    }
}