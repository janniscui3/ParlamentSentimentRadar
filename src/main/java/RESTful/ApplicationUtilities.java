package RESTful;

import Database.MongoDBConnectionHandler;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationUtilities {
    /*
    public Document getSentiment(String rednerid, MongoDBConnectionHandler connectionHandler) throws FileNotFoundException {
        // Create a Hashmap containing every redesentiment as key and increment its value by one every time we find it in the txt
        HashMap<String, ArrayList<Float>> redesentiment = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/Sentiments.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                String[] result = line.split(":");
                if(result.length == 2) {
                    String[] sentiments = result[1].split(" ");
                    ArrayList<Float> floatlist = new ArrayList<>();
                    for (String i: sentiments) {
                        floatlist.add(Float.parseFloat(i));
                    }
                    redesentiment.put(result[0], floatlist);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Iterate over every abgeordnete and get their reden, then find their sentiments in the redesentiment hashmap.
        for (String i: abgeordnetenliste.keySet()) {
            int possentimentcount = 0;
            int neusentimentcount = 0;
            int negsentimentcount = 0;
            for(String j: abgeordnetenliste.get(i).getRedeliste()) {
                if(redesentiment.containsKey(j)) {
                    for(Float k: redesentiment.get(j)) {
                        if (k > 0) {
                            possentimentcount++;
                        }
                        else if(k == 0) {
                            neusentimentcount++;
                        }
                        else if(k < 0) {
                            negsentimentcount++;
                        }
                    }
                }
            }
            int totalcount = possentimentcount + neusentimentcount + negsentimentcount;
            float percentpos = (possentimentcount * 100.0f)/totalcount;
            float percentneu = (neusentimentcount * 100.0f)/totalcount;
            float percentneg = (negsentimentcount * 100.0f)/totalcount;
            if(totalcount > 0) {
                System.out.println("Abgeordneter: " + abgeordnetenliste.get(i).getVorName() + " " + abgeordnetenliste.get(i).getNachName() + " Positiv: " + percentpos  + " Neutral: " + percentneu + " Negativ: " + percentneg);
            }
        }
    }

     */
}
