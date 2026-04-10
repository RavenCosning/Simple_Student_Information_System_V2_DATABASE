package information_management_practice;

/**
 *
 * @author RavenCosning
 */
public class Student {
    private String id, firstName, lastName, programCode, gender;
    private int year;
    
    public Student(String id, String firstName, String lastName, String programCode, int year, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.programCode = programCode;
        this.year = year;
        this.gender = gender;
    }

    


    public void setId(String id) {
        this.id = id;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public String getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getProgramCode() {
        return programCode;
    }
    public String getGender() {
        return gender;
    }   
    public int getYear() {
        return year;
    }
    @Override
    public String toString() {
        return id + " - " + lastName + " , " + firstName;
    }
}
