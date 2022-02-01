package Interface;

/**
 * This Class represents a abstract person,
 * which has a vorname and a nachname.
 *
 * @author Jannis Cui
 */
public abstract class Person_File_Impl {
    private String vorname;
    private String nachname;

    // Constructor
    public Person_File_Impl(String pName, String nName) {
        vorname = pName;
        nachname = nName;
    }

    public Person_File_Impl() {

    }


    //Getter and Setter
    public void setVorName(String pName) {
        this.vorname = pName;
    }

    public void setNachName(String nName) {
        this.nachname = nName;
    }

    public String getVorName() {
        return vorname;
    }

    public String getNachName() {
        return nachname;
    }


    @Override
    public String toString() {
        return vorname;
    }
}
