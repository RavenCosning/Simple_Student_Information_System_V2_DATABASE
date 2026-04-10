package information_management_practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class DataSeeder {

    public static void seed() {
        if (alreadySeeded()) {
            System.out.println("Database already seeded, skipping.");
            return;
        }
        seedColleges();
        seedPrograms();
        seedStudents();
        System.out.println("Seeding complete.");
    }

    private static boolean alreadySeeded() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM student");
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) >= 5000;
        } catch (Exception e) { return false; }
    }

    private static void seedColleges() {
        String[][] colleges = {
            {"CASS", "College of Arts and Social Sciences"},
            {"CEBA", "College of Economics, Business, and Accountancy"},
            {"CCS",  "College of Computer Studies"},
            {"CED",  "College of Education"},
            {"COE",  "College of Engineering"},
            {"CHS",  "College of Health Sciences"},
            {"CSM",  "College of Science and Mathematics"}
        };
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO college (code, name) VALUES (?, ?)");
            for (String[] c : colleges) {
                pstmt.setString(1, c[0]);
                pstmt.setString(2, c[1]);
                pstmt.executeUpdate();
            }
            System.out.println("Colleges seeded.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void seedPrograms() {
        String[][] programs = {
            {"ABENG",  "Bachelor of Arts in English",                              "CASS"},
            {"ABFIL",  "Bachelor of Arts in Filipino",                             "CASS"},
            {"ABHIS",  "Bachelor of Arts in History",                              "CASS"},
            {"ABPOL",  "Bachelor of Arts in Political Science",                    "CASS"},
            {"ABPSY",  "Bachelor of Arts in Psychology",                           "CASS"},
            {"ABSOC",  "Bachelor of Arts in Sociology",                            "CASS"},
            {"ABPHIL", "Bachelor of Arts in Philosophy",                           "CASS"},
            {"BSAC",   "Bachelor of Science in Accountancy",                       "CEBA"},
            {"BSENTR", "Bachelor of Science in Entrepreneurship",                  "CEBA"},
            {"BSMM",   "Bachelor of Science in Marketing Management",              "CEBA"},
            {"BSECON", "Bachelor of Science in Economics",                         "CEBA"},
            {"BSBECON","Bachelor of Science in Business Economics",                "CEBA"},
            {"BSHM",   "Bachelor of Science in Hospitality Management",            "CEBA"},
            {"BSCS",   "Bachelor of Science in Computer Science",                  "CCS"},
            {"BSIT",   "Bachelor of Science in Information Technology",            "CCS"},
            {"BSCA",   "Bachelor of Science in Computer Applications",             "CCS"},
            {"BSHT",   "Bachelor of Science in Home Technology",                   "CED"},
            {"BSSME",  "Bachelor of Science in Science and Mathematics Education", "CED"},
            {"BSIED",  "Bachelor of Science in Industrial Education",              "CED"},
            {"BSPE",   "Bachelor of Science in Physical Education",                "CED"},
            {"BSPED",  "Bachelor of Science in Professional Education",            "CED"},
            {"BSCE",   "Bachelor of Science in Civil Engineering",                 "COE"},
            {"BSEE",   "Bachelor of Science in Electrical Engineering",            "COE"},
            {"BSME",   "Bachelor of Science in Mechanical Engineering",            "COE"},
            {"BSCHE",  "Bachelor of Science in Chemical Engineering",              "COE"},
            {"BSETEN", "Bachelor of Science in Engineering Technology",            "COE"},
            {"BSN",    "Bachelor of Science in Nursing",                           "CHS"},
            {"BSMID",  "Bachelor of Science in Midwifery",                         "CHS"},
            {"BSMT",   "Bachelor of Science in Medical Technology",                "CHS"},
            {"BSMATH", "Bachelor of Science in Mathematics",                       "CSM"},
            {"BSCHEM", "Bachelor of Science in Chemistry",                         "CSM"},
            {"BSPHY",  "Bachelor of Science in Physics",                           "CSM"},
            {"BSBIO",  "Bachelor of Science in Biology",                           "CSM"}
        };
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO program (code, name, college) VALUES (?, ?, ?)");
            for (String[] p : programs) {
                pstmt.setString(1, p[0]);
                pstmt.setString(2, p[1]);
                pstmt.setString(3, p[2]);
                pstmt.executeUpdate();
            }
            System.out.println("Programs seeded.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void seedStudents() {
        String[] firstNames = {
            "Juan", "Maria", "Jose", "Ana", "Carlos", "Rosa", "Miguel", "Elena",
            "Luis", "Sofia", "Marco", "Isabella", "Diego", "Camila", "Rafael",
            "Valentina", "Andre", "Gabriela", "Daniel", "Patricia", "James",
            "Angela", "Robert", "Christine", "Mark", "Jennifer", "Paul", "Karen",
            "Kevin", "Michelle", "Ryan", "Nicole", "Aaron", "Stephanie", "Brian"
        };
        String[] lastNames = {
            "Santos", "Reyes", "Cruz", "Bautista", "Ocampo", "Garcia", "Torres",
            "Flores", "Rivera", "Gomez", "Lopez", "Martinez", "Ramos", "Mendoza",
            "Dela Cruz", "Villanueva", "Fernandez", "Castillo", "Morales", "Aquino",
            "Navarro", "Santiago", "Domingo", "Salazar", "Pascual", "Aguilar",
            "Bernardo", "Cabrera", "Espiritu", "Hidalgo", "Ilustre", "Jacinto"
        };
        String[] programCodes = {
            "ABENG","ABFIL","ABHIS","ABPOL","ABPSY","ABSOC","ABPHIL",
            "BSAC","BSENTR","BSMM","BSECON","BSBECON","BSHM",
            "BSCS","BSIT","BSCA",
            "BSHT","BSSME","BSIED","BSPE","BSPED",
            "BSCE","BSEE","BSME","BSCHE","BSETEN",
            "BSN","BSMID","BSMT",
            "BSMATH","BSCHEM","BSPHY","BSBIO"
        };
        String[] genders = {"Male", "Female"};
        int[] years = {2020, 2021, 2022, 2023, 2024, 2025};

        Random rand = new Random();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO student (id, firstname, lastname, course, year, gender) " +
                "VALUES (?, ?, ?, ?, ?, ?)");

            int[] counters = new int[years.length];

            for (int i = 0; i < 5000; i++) {
                int yearIdx = rand.nextInt(years.length);
                counters[yearIdx]++;
                String studentId = years[yearIdx] + "-" + String.format("%04d", counters[yearIdx]);
                String firstName = firstNames[rand.nextInt(firstNames.length)];
                String lastName  = lastNames[rand.nextInt(lastNames.length)];
                String program   = programCodes[rand.nextInt(programCodes.length)];
                int yearLevel    = rand.nextInt(4) + 1;
                String gender    = genders[rand.nextInt(genders.length)];

                pstmt.setString(1, studentId);
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setString(4, program);
                pstmt.setInt(5, yearLevel);
                pstmt.setString(6, gender);
                pstmt.executeUpdate();
            }
            System.out.println("5000 students seeded.");
        } catch (Exception e) { e.printStackTrace(); }
    }
}