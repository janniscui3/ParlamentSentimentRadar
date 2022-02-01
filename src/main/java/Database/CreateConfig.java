package Database;

import java.io.*;
import java.util.Properties;

/**
 * @author Jannis Cui
 * Has a Properties class that can be read by readProperty
 */
public class CreateConfig {
    String filepath;

    public CreateConfig() {
        filepath = "src/main/resources/config.properties";
    }


    /**
     * @author Jannis Cui
     * Will initialize the database config and put its values into the filepath
     * Not really important.
     */
    public void makeinitialConfig() {
        try (OutputStream output = new FileOutputStream(this.filepath)) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("remote_host", "prg2021.texttechnologylab.org");
            prop.setProperty("remote_user", "PRG_WiSe21_Gruppe_2_4");
            prop.setProperty("remote_password", "II2dksbA");
            prop.setProperty("remote_port", "27020");
            prop.setProperty("remote_collection", "protocol");
            prop.setProperty("remote_database", "PRG_WiSe21_Gruppe_2_4");
            // save properties to project root folder
            prop.store(output, null);

            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    /**
     * @author Jannis Cui
     * Will add a Property to the file.
     * Not really important.
     */
    public void addProperty(String key, String value) {
        try (InputStream input = new FileInputStream(this.filepath)) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            // set given property
            prop.setProperty(key, value);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Reads a propertys value
     * @author Jannis Cui
     * @param property The property to be read
     * @return Value associated with said property
     */
    public String readProperty(String property) {
        String output = "null";
        try (InputStream input = new FileInputStream(this.filepath)) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            output = prop.getProperty(property);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return output;
    }
}