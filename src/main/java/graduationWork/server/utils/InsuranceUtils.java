package graduationWork.server.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsuranceUtils {

    public static Map<String, String> getCoverageNameAmount(String detail) {
        Map<String, String> result = new HashMap<>();

        if (detail == null || detail.isEmpty()) {
            return result; // Empty map
        }

        String[] parts = detail.split("-");
        if (parts.length > 1) {
            result.put("name",parts[0].stripTrailing());
            result.put("amount", parts[1].stripLeading());
        }

        return result;
    }
}
