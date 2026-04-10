package information_management_practice;

/**
 *
 * @author RavenCosning
 */
public class College {
    private String code, name;
    
    public College (String code, String name) {
        this.code = code;
        this.name = name;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return code + " - " + name;
    }
}
