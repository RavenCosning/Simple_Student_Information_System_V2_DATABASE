package information_management_practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProgramDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────
    public boolean addProgram(Program program) {
        String sql = "INSERT INTO program (code, name, college) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, program.getCode().toUpperCase().trim());
            pstmt.setString(2, program.getName().trim());
            pstmt.setString(3, program.getCollegeCode().toUpperCase().trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("addProgram failed: " + e.getMessage());
            return false;
        }
    }

    // ── READ (single) ────────────────────────────────────────────────────────
    public Program getProgram(String code) {
        String sql = "SELECT code, name, college FROM program WHERE code = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code.toUpperCase().trim());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Program(rs.getString("code"), rs.getString("name"), rs.getString("college"));
            }
        } catch (Exception e) {
            System.err.println("getProgram failed: " + e.getMessage());
        }
        return null;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public boolean updateProgram(String oldCode, Program updated) {
        String sql = "UPDATE program SET code = ?, name = ?, college = ? WHERE code = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, updated.getCode().toUpperCase().trim());
            pstmt.setString(2, updated.getName().trim());
            pstmt.setString(3, updated.getCollegeCode().toUpperCase().trim());
            pstmt.setString(4, oldCode.toUpperCase().trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("updateProgram failed: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public boolean deleteProgram(String code) {
        String sql = "DELETE FROM program WHERE code = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code.toUpperCase().trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("deleteProgram failed: " + e.getMessage());
            return false;
        }
    }

    // ── LIST (all) ───────────────────────────────────────────────────────────
    public List<Program> getAllPrograms() {
        return search("", "code", true, -1, -1);
    }

    // ── SEARCH + SORT + PAGINATION ───────────────────────────────────────────
    public List<Program> search(String keyword, String sortBy, boolean ascending, int limit, int offset) {
        List<Program> list = new ArrayList<>();

        if (!sortBy.equals("code") && !sortBy.equals("name") && !sortBy.equals("college")) {
            sortBy = "code";
        }
        String direction = ascending ? "ASC" : "DESC";
        String filter = "%" + keyword.trim() + "%";

        String sql = "SELECT code, name, college FROM program "
                + "WHERE code LIKE ? OR name LIKE ? OR college LIKE ? "
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
            if (limit > 0) {
                pstmt.setInt(4, limit);
                pstmt.setInt(5, Math.max(offset, 0));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Program(rs.getString("code"), rs.getString("name"), rs.getString("college")));
            }
        } catch (Exception e) {
            System.err.println("search programs failed: " + e.getMessage());
        }
        return list;
    }

    public List<Program> searchAll(String code, String name, String collegeCode) {
        List<Program> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT code, name, college FROM program WHERE 1=1");

        if (!code.isEmpty()) {
            sql.append(" AND code LIKE ?");
        }
        if (!name.isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (!collegeCode.isEmpty()) {
            sql.append(" AND college = ?");
        }

        sql.append(" ORDER BY code");

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());

            int i = 1;
            if (!code.isEmpty()) {
                pstmt.setString(i++, code + "%");
            }
            if (!name.isEmpty()) {
                pstmt.setString(i++, name + "%");
            }
            if (!collegeCode.isEmpty()) {
                pstmt.setString(i++, collegeCode.toUpperCase().trim());
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Program(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("college") // <-- was "collegecode", correct is "college"
                ));
            }
        } catch (Exception e) {
            System.err.println("searchAll programs failed: " + e.getMessage());
        }
        return list;
    }

    // ── COUNT ────────────────────────────────────────────────────────────────
    public int count(String keyword) {
        String filter = "%" + keyword.trim() + "%";
        String sql = "SELECT COUNT(*) FROM program WHERE code LIKE ? OR name LIKE ? OR college LIKE ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, filter);
            pstmt.setString(2, filter);
            pstmt.setString(3, filter);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("count programs failed: " + e.getMessage());
        }
        return 0;
    }

    // ── COUNT BY COLLEGE (for delete guard) ──────────────────────────────────
    public int countByCollege(String collegeCode) {
        String sql = "SELECT COUNT(*) FROM program WHERE college = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, collegeCode.toUpperCase().trim());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("countByCollege failed: " + e.getMessage());
        }
        return 0;
    }

    // ── EXISTS ───────────────────────────────────────────────────────────────
    public boolean exists(String code) {
        return getProgram(code) != null;
    }
}
