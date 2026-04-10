package information_management_practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO student (id, firstname, lastname, course, year, gender) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, student.getId().trim());
            pstmt.setString(2, student.getFirstName().trim());
            pstmt.setString(3, student.getLastName().trim());
            pstmt.setString(4, student.getProgramCode().toUpperCase().trim());
            pstmt.setInt(5, student.getYear());
            pstmt.setString(6, student.getGender().trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("addStudent failed: " + e.getMessage());
            return false;
        }
    }

    // ── READ (single) ────────────────────────────────────────────────────────
    public Student getStudent(String id) {
        String sql = "SELECT id, firstname, lastname, course, year, gender FROM student WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id.trim());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getString("id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("course"),
                        rs.getInt("year"),
                        rs.getString("gender")
                );
            }
        } catch (Exception e) {
            System.err.println("getStudent failed: " + e.getMessage());
        }
        return null;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public boolean updateStudent(String oldId, Student updated) {
        String sql = "UPDATE student SET id = ?, firstname = ?, lastname = ?, course = ?, year = ?, gender = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, updated.getId().trim());
            pstmt.setString(2, updated.getFirstName().trim());
            pstmt.setString(3, updated.getLastName().trim());
            pstmt.setString(4, updated.getProgramCode().toUpperCase().trim());
            pstmt.setInt(5, updated.getYear());
            pstmt.setString(6, updated.getGender().trim());
            pstmt.setString(7, oldId.trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("updateStudent failed: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public boolean deleteStudent(String id) {
        String sql = "DELETE FROM student WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id.trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("deleteStudent failed: " + e.getMessage());
            return false;
        }
    }

    // ── LIST (all) ───────────────────────────────────────────────────────────
    public List<Student> getAllStudents() {
        return search("", "id", true, -1, -1);
    }

    // ── SEARCH + SORT + PAGINATION ───────────────────────────────────────────
    public List<Student> search(String keyword, String sortBy, boolean ascending, int limit, int offset) {
        List<Student> list = new ArrayList<>();

        // Whitelist sort columns
        if (!sortBy.equals("id") && !sortBy.equals("firstname")
                && !sortBy.equals("lastname") && !sortBy.equals("course")
                && !sortBy.equals("year") && !sortBy.equals("gender")) {
            sortBy = "id";
        }
        String direction = ascending ? "ASC" : "DESC";
        String filter = "%" + keyword.trim() + "%";

        String sql = "SELECT id, firstname, lastname, course, year, gender FROM student "
                + "WHERE id LIKE ? OR firstname LIKE ? OR lastname LIKE ? OR course LIKE ? "
                + "ORDER BY " + sortBy + " " + direction;

        if (limit > 0) {
            sql += " LIMIT ? OFFSET ?";
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, filter);
            pstmt.setString(2, filter);
            pstmt.setString(3, filter);
            pstmt.setString(4, filter);
            if (limit > 0) {
                pstmt.setInt(5, limit);
                pstmt.setInt(6, Math.max(offset, 0));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("course"),
                        rs.getInt("year"),
                        rs.getString("gender")
                ));
            }
        } catch (Exception e) {
            System.err.println("search students failed: " + e.getMessage());
        }
        return list;
    }

    public List<Student> searchAll(String id, String firstName, String lastName,
            String programCode, String yearLevel, String gender) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, firstname, lastname, course, year, gender FROM student WHERE 1=1");

        if (!id.isEmpty()) {
            sql.append(" AND id LIKE ?");
        }
        if (!firstName.isEmpty()) {
            sql.append(" AND firstname LIKE ?");
        }
        if (!lastName.isEmpty()) {
            sql.append(" AND lastname LIKE ?");
        }
        if (!programCode.isEmpty()) {
            sql.append(" AND course = ?");
        }
        if (!yearLevel.isEmpty()) {
            sql.append(" AND year = ?");
        }
        if (!gender.isEmpty()) {
            sql.append(" AND gender = ?");
        }

        sql.append(" ORDER BY id");

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());

            int i = 1;
            if (!id.isEmpty()) {
                pstmt.setString(i++, id + "%");           // starts with
            }
            if (!firstName.isEmpty()) {
                pstmt.setString(i++, firstName + "%");    // starts with
            }
            if (!lastName.isEmpty()) {
                pstmt.setString(i++, lastName + "%");     // starts with
            }
            if (!programCode.isEmpty()) {
                pstmt.setString(i++, programCode.toUpperCase().trim());
            }
            if (!yearLevel.isEmpty()) {
                pstmt.setInt(i++, Integer.parseInt(yearLevel));
            }
            if (!gender.isEmpty()) {
                pstmt.setString(i++, gender.trim());
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("course"),
                        rs.getInt("year"),
                        rs.getString("gender")
                ));
            }
        } catch (Exception e) {
            System.err.println("searchAll failed: " + e.getMessage());
        }
        return list;
    }

    // ── COUNT ────────────────────────────────────────────────────────────────
    public int count(String keyword) {
        String filter = "%" + keyword.trim() + "%";
        String sql = "SELECT COUNT(*) FROM student "
                + "WHERE id LIKE ? OR firstname LIKE ? OR lastname LIKE ? OR course LIKE ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, filter);
            pstmt.setString(2, filter);
            pstmt.setString(3, filter);
            pstmt.setString(4, filter);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("count students failed: " + e.getMessage());
        }
        return 0;
    }

    // ── COUNT BY PROGRAM (for delete guard) ──────────────────────────────────
    public int countByProgram(String programCode) {
        String sql = "SELECT COUNT(*) FROM student WHERE course = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, programCode.toUpperCase().trim());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("countByProgram failed: " + e.getMessage());
        }
        return 0;
    }

    // ── EXISTS ───────────────────────────────────────────────────────────────
    public boolean exists(String id) {
        return getStudent(id) != null;
    }
}
