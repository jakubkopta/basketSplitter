package basket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Getter
public class BasketSplitter {

    private final Map<String, List<String>> deliveryOptions;

    public BasketSplitter(String absolutePathToConfigFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.deliveryOptions = objectMapper.readValue(new FileReader(absolutePathToConfigFile), new TypeReference<>() {
        });
    }

    public List<String> readBasketFromJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new FileReader(filePath), new TypeReference<>() {
        });
    }

    public Map<String, List<String>> split(List<String> items) {

        Map<String, List<String>> deliveryOptionsForItems = new HashMap<>();
        Map<String, List<String>> result = new HashMap<>();

        for (String item : items) {
            List<String> options = deliveryOptions.getOrDefault(item, Collections.emptyList());
            for (String option : options) {
                deliveryOptionsForItems.computeIfAbsent(option, key -> new ArrayList<>()).add(item);
            }
        }

        while (!deliveryOptionsForItems.isEmpty()) {

            List<String> biggestItemsList = new ArrayList<>();
            String biggestOption = null;
            int maxSize = 0;

            for (Map.Entry<String, List<String>> entry : deliveryOptionsForItems.entrySet()) {
                String option = entry.getKey();
                List<String> itemsList = entry.getValue();
                if (itemsList.size() > maxSize) {
                    maxSize = itemsList.size();
                    biggestItemsList = itemsList;
                    biggestOption = option;
                }
            }

            result.put(biggestOption, biggestItemsList);
            deliveryOptionsForItems.remove(biggestOption);

            for (List<String> itemsList : deliveryOptionsForItems.values()) {
                itemsList.removeAll(biggestItemsList);
            }

            deliveryOptionsForItems.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        String absolutePathToConfigFile = "src/main/resources/config.json";
        BasketSplitter splitter = new BasketSplitter(absolutePathToConfigFile);

        String basketJsonFile = "src/main/resources/basket-1.json";
        List<String> items = splitter.readBasketFromJson(basketJsonFile);


        Map<String, List<String>> result = splitter.split(items);
        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}


