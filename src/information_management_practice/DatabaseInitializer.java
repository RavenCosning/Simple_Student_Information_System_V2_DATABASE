package information_management_practice;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        Connection conn = DBConnection.getConnection();

        try (Statement stmt = conn.createStatement()) {

            // ── COLLEGE ──────────────────────────────────────────────
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS college (" +
                "  code TEXT PRIMARY KEY, " +
                "  name TEXT NOT NULL UNIQUE" +
                ");"
            );
            System.out.println("College table ready.");

            // ── PROGRAM ──────────────────────────────────────────────
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS program (" +
                "  code    TEXT PRIMARY KEY, " +
                "  name    TEXT NOT NULL UNIQUE, " +
                "  college TEXT NOT NULL, " +
                "  FOREIGN KEY (college) REFERENCES college(code) " +
                "    ON UPDATE CASCADE ON DELETE RESTRICT" +
                ");"
            );
            System.out.println("Program table ready.");

            // ── STUDENT ──────────────────────────────────────────────
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS student (" +
                "  id        TEXT PRIMARY KEY, " +        // format: YYYY-NNNN
                "  firstname TEXT NOT NULL, " +
                "  lastname  TEXT NOT NULL, " +
                "  course    TEXT NOT NULL, " +           // refers to program.code
                "  year      INTEGER NOT NULL, " +        // 1 to 4
                "  gender    TEXT NOT NULL, " +           // Male / Female
                "  FOREIGN KEY (course) REFERENCES program(code) " +
                "    ON UPDATE CASCADE ON DELETE RESTRICT" +
                ");"
            );
            System.out.println("Student table ready.");

            System.out.println("All tables initialized successfully.");

        } catch (Exception e) {
            System.err.println("Table creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}