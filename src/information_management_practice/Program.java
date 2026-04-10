package information_management_practice;

/**
 *
 * @author RavenCosning
 */
public class Program {
    private  String code, name, collegeCode;
    
    public Program(String code, String name, String collegeCode) {
        this.code = code;
        this.name = name;
        this.collegeCode = collegeCode;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public String getCollegeCode() {
        return collegeCode;
    }
    @Override
    public String toString() {
        return code + " - " + name;
        }
}
