package horvath.app;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class, which controls current locale, changes language
 * @author Marcel Horváth
 */
public class GymPrompt {
    private String language = "en";
    private String country = "US";
    private Locale currentLocale;
    private ResourceBundle languages;
    private static GymPrompt gymPrompt;

    public GymPrompt(){
        currentLocale = new Locale(language, country);
        languages = ResourceBundle.getBundle("text", currentLocale);
    }

    public static GymPrompt getInstance() {
        if(gymPrompt == null){
            gymPrompt = new GymPrompt();
        }
        return gymPrompt;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public ResourceBundle getLanguages() {
        return languages;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    public void setLanguages(ResourceBundle languages) {
        this.languages = languages;
    }

    public void changeLanguage() {
        if(language.equals("cz")){
            setLanguage("en");
            setCountry("US");
        }
        else if(getLanguage().equals("en")){
            setLanguage("cz");
            setCountry("CZ");
        }
        currentLocale = new Locale(language, country);
        languages = ResourceBundle.getBundle("text", currentLocale);
    }
}
