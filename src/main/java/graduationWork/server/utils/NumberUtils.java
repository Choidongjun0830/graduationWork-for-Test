package graduationWork.server.utils;

import java.text.DecimalFormat;

public class NumberUtils {

    private static final DecimalFormat df = new DecimalFormat("#,###");

    public static String formatCurrency(int amount){
        return df.format(amount) + "원";
    }
}
