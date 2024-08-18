package graduationWork.server.ether;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class UpbitApiClient {

    public double getTradePrice() {

        try {
            String url = "https://api.upbit.com/v1/ticker?markets=KRW-ETH";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //응답 읽기
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.toString());

                if(jsonResponse.isArray() && jsonResponse.size() > 0) {
                    JsonNode firstObject = jsonResponse.get(0);
                    JsonNode tradePrice = firstObject.get("trade_price");

                    if(tradePrice != null && tradePrice.isDouble()){
                        return tradePrice.doubleValue();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
