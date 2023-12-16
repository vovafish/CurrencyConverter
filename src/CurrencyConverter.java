import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverter {

    public static void CurrencyRender(Map currencyCodes) {
        Iterator it = currencyCodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " for " + pair.getValue());
        }
    }
    public CurrencyConverter() throws IOException {
        Boolean running = true;
        do {
            HashMap<Integer, String> currencyCodes = new HashMap<Integer, String>();

            // Add currency codes
            currencyCodes.put(1, "USD");
            currencyCodes.put(2, "CAD");
            currencyCodes.put(3, "EUR");
            currencyCodes.put(4, "HKD");
            currencyCodes.put(5, "INR");

            Integer from, to;
            String fromCode, toCode;
            double amount;

            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to the Currency Converter");
            System.out.println("Currency converting FROM?");
            CurrencyRender(currencyCodes);
            from = scanner.nextInt();
            while (from < 1 || from > currencyCodes.size()) {
                System.out.println("Please select a valid currency! (1-5)");
                CurrencyRender(currencyCodes);
                from = scanner.nextInt();
            }
            fromCode = currencyCodes.get(from);

            System.out.println("Currency converting TO?");
            CurrencyRender(currencyCodes);
            to = scanner.nextInt();
            while (to < 1 || to > currencyCodes.size()) {
                System.out.println("Please select a valid currency! (1-5)");
                CurrencyRender(currencyCodes);
                to = scanner.nextInt();
            }
            toCode = currencyCodes.get(to);

            System.out.println("Amount you wish to convert?");
            amount = scanner.nextFloat();
            sendHttpGETRequest(fromCode, toCode, amount);

            System.out.println("Use it again?");
            System.out.println("1 :for YES \t 2: for NO");
            if (scanner.nextInt() != 1) {
                running = false;
            }
        } while(running);
        System.out.println("Thanks for using converter :)");
    }

    private static void sendHttpGETRequest(String fromCode, String toCode, double amount) throws IOException {

        DecimalFormat f = new DecimalFormat("00.00");

        String GET_URL = "https://api.currencyapi.com/v3/latest?apikey=cur_live_Co8uwDYt0VuRPuTFm2FCRL6Hiz6mez9ay0drURSO&currencies=" + fromCode + "&base_currency=" + toCode;
        URL url = new URL(GET_URL);
        System.out.println(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        int responseCode = httpURLConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            } in.close();

            /*JSONObject obj = new JSONObject(response.toString());
            Double exchangeRate = obj.getJSONObject("data").getJSONObject(toCode).getDouble("value");*/
            JSONObject obj = new JSONObject(response.toString());
            JSONObject dataObj = obj.getJSONObject("data");

            try {
                Double exchangeRate = dataObj.getJSONObject(fromCode).getDouble("value");
                System.out.println("Result: " + f.format(amount) + " " + fromCode + " = " + f.format(amount / exchangeRate) + " " + toCode);
            } catch (JSONException e) {
                // Handle the exception (e.g., print an error message)
                System.out.println("Error: Unable to retrieve exchange rate from the response.");
                e.printStackTrace();  // Print the exception details (optional)
                // Proceed with the rest of your program or return, depending on your logic
            }
        } else {
            System.out.println("GET request failed");
        }

    }

}
