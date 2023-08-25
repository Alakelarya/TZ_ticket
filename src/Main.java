import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Main {
    public static void main(String[] args) {

        Object obj = null;
        try {
            obj = new JSONParser().parse(new BufferedReader(new InputStreamReader(new FileInputStream("src/tickets.json"), "UTF-8")));
        } catch (IOException | ParseException e) {
            System.out.println("Error! ");
        }

        JSONObject jo = (JSONObject) obj;

        JSONArray priceArr = (JSONArray) jo.get("tickets");
        Iterator priceItr = priceArr.iterator();
        System.out.println("Result time:");
        String pattern = "dd.mm.yy HH:mm";
        long temp = 1000000000;
        ArrayList<String> listCarrier = new ArrayList<>();
        ArrayList<String> listTime = new ArrayList<>();
        ArrayList<Long> listPrice = new ArrayList<>();


        while (priceItr.hasNext()) {
            JSONObject test = (JSONObject) priceItr.next();

            if (test.get("origin_name").equals("Владивосток") && test.get("destination_name").equals("Тель-Авив")) {
                String timeStart = test.get("departure_date") + " " + test.get("departure_time");
                String timeStop = test.get("arrival_date") + " " +  test.get("arrival_time");


                listCarrier.add((String) test.get("carrier"));
                listPrice.add((Long) test.get("price"));

                SimpleDateFormat format = new SimpleDateFormat(pattern);

                Date d1 = null;
                Date d2 = null;

                try {
                    d1 = format.parse(timeStart);
                    d2 = format.parse(timeStop);

                    long diff = d2.getTime() - d1.getTime();


                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    listTime.add(diffHours + "ч " + diffMinutes + "мин |" + test.get("carrier"));


                } catch (java.text.ParseException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        minFliegth(listTime);
        System.out.println("Result price:");
        Collections.sort(listPrice);
        double average = 0;

        for (int i = 0; i < listPrice.size(); i++) {
            double test = Double.parseDouble(String.valueOf(listPrice.get(i)));
            average += test;
        }
        average = average / listPrice.size();
        System.out.println("Average - " + average);

        double median = 0;
        if (listPrice.size() % 2 == 0) {
            int tempMedian = (listPrice.size()) / 2;
            median = (listPrice.get(tempMedian) + listPrice.get(tempMedian - 1)) / 2;

        } else median = listPrice.get((listPrice.size()) / 2);

        System.out.println("Median - " + median);
        System.out.println("Diff - " + (average - median));


    }

    public static void minFliegth(ArrayList<String> listTime) {
        Map<String, Integer> carrierToMinTime = new HashMap<>();

        for (String line : listTime) {
            String[] parts = line.split("\\|");
            String carrier = parts[1].trim();
            String timeStr = parts[0].trim();

            int hours = Integer.parseInt(timeStr.substring(0, timeStr.indexOf("ч")));
            int minutes = Integer.parseInt(timeStr.substring(timeStr.indexOf("ч") + 2, timeStr.indexOf("мин")));

            int totalTime = hours * 60 + minutes;

            if (!carrierToMinTime.containsKey(carrier) || totalTime < carrierToMinTime.get(carrier)) {
                carrierToMinTime.put(carrier, totalTime);
            }
        }

        for (Map.Entry<String, Integer> entry : carrierToMinTime.entrySet()) {
            String carrier = entry.getKey();
            int totalTime = entry.getValue();
            int hours = totalTime / 60;
            int minutes = totalTime % 60;

            System.out.println(carrier + ": " + hours + "ч" + minutes + "мин");
        }
    }

}


