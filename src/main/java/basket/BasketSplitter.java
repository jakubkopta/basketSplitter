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
        this.deliveryOptions = objectMapper.readValue(new FileReader(absolutePathToConfigFile), new TypeReference<>() {});
    }

    public List<String> readBasketFromJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new FileReader(filePath), new TypeReference<>() {});
    }

    public Map<String, List<String>> split(List<String> items) {

        Map<String, List<String>> deliveryOptionsForItems = new HashMap<>(); //New Map for delivery options for items in basket
        Map<String, List<String>> result = new HashMap<>(); //New Map for final result

        for (String item : items) { //fill deliveryOptionsForItems
            List<String> options = deliveryOptions.getOrDefault(item, Collections.emptyList());
            for (String option : options) {
                deliveryOptionsForItems.computeIfAbsent(option, key -> new ArrayList<>()).add(item);
            }
        }

        while (!deliveryOptionsForItems.isEmpty()) {

            List<String> biggestItemsList = new ArrayList<>(); //New List with the biggest number of items for delivery
            String biggestOption = null; //delivery option with the biggest number of items
            int maxSize = 0; //size of the biggest number of items

            for (Map.Entry<String, List<String>> entry : deliveryOptionsForItems.entrySet()) { //find biggestItemsList and biggestOption
                String option = entry.getKey();
                List<String> itemsList = entry.getValue();
                if (itemsList.size() > maxSize) {
                    maxSize = itemsList.size();
                    biggestItemsList = itemsList;
                    biggestOption = option;
                }
            }

            result.put(biggestOption, biggestItemsList); //add List with the biggest number of items and their delivery to result
            deliveryOptionsForItems.remove(biggestOption); //remove what added to result from deliveryOptionsForItems

            for (List<String> itemsList : deliveryOptionsForItems.values()) { //remove items in biggestItemsList from other lists
                itemsList.removeAll(biggestItemsList);
            }

            deliveryOptionsForItems.entrySet().removeIf(entry -> entry.getValue().isEmpty()); //remove deliveries with empty list of items
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        String absolutePathToConfigFile = "src/main/resources/config.json";
        BasketSplitter splitter = new BasketSplitter(absolutePathToConfigFile);

        String basketJsonFile = "src/main/resources/basket-1.json";
        List<String> items = splitter.readBasketFromJson(basketJsonFile);

        Map<String, List<String>> result = splitter.split(items); //execute split method
        for (Map.Entry<String, List<String>> entry : result.entrySet()) { //display result
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}


