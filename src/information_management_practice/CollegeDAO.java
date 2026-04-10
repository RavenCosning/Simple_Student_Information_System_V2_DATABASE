package information_management_practice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CollegeDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────
    public boolean addCollege(College college) {
        String sql = "INSERT INTO college (code, name) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, college.getCode().toUpperCase().trim());
            pstmt.setString(2, college.getName().trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("addCollege failed: " + e.getMessage());
            return false;
        }
    }

    // ── READ (single) ────────────────────────────────────────────────────────
    public College getCollege(String code) {
        String sql = "SELECT code, name FROM college WHERE code = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code.toUpperCase().trim());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new College(rs.getString("code"), rs.getString("name"));
            }
        } catch (Exception e) {
            System.err.println("getCollege failed: " + e.getMessage());
        }
        return null; // not found
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public boolean updateCollege(String oldCode, College updated) {
        // Update code and name; CASCADE will fix linked programs automatically
        String sql = "UPDATE college SET code = ?, name = ? WHERE code = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, updated.getCode().toUpperCase().trim());
            pstmt.setString(2, updated.getName().trim());
            pstmt.setString(3, oldCode.toUpperCase().trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("updateCollege failed: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public boolean deleteCollege(String code) {
        // Will fail if programs still reference this college (RESTRICT)
        String sql = "DELETE FROM college WHERE code = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code.toUpperCase().trim());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("deleteCollege failed: " + e.getMessage());
            return false;
        }
    }

    // ── LIST (all, no filter) ────────────────────────────────────────────────
    public List<College> getAllColleges() {
        return search("", "code", true, -1, -1);
    }

    // ── SEARCH + SORT + PAGINATION ───────────────────────────────────────────
    /**
     * @param keyword filters by code or name (empty = no filter)
     * @param sortBy column to sort: "code" or "name"
     * @param ascending true = ASC, false = DESC
     * @param limit rows per page (-1 = no limit)
     * @param offset starting row (-1 = no offset)
     */
    public List<College> search(String keyword, String sortBy, boolean ascending, int limit, int offset) {
        List<College> list = new ArrayList<>();

        // Whitelist sort columns to prevent SQL injection
        if (!sortBy.equals("code") && !sortBy.equals("name")) {
            sortBy = "code";
        }
        String direction = ascending ? "ASC" : "DESC";
        String filter = "%" + keyword.trim() + "%";

        String sql = "SELECT code, name FROM college "
                + "WHERE code LIKE ? OR name LIKE ? "
                + "ORDER BY " + sortBy + " " + direction;

        // Only add LIMIT/OFFSET if pagination is requested
        if (limit > 0) {
            sql += " LIMIT ? OFFSET ?";
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, filter);
            pstmt.setString(2, filter);
            if (limit > 0) {
                pstmt.setInt(3, limit);
                pstmt.setInt(4, Math.max(offset, 0));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new College(rs.getString("code"), rs.getString("name")));
            }
        } catch (Exception e) {
            System.err.println("search failed: " + e.getMessage());
        }
        return list;
    }

    // ── COUNT (for pagination) ───────────────────────────────────────────────
    public int count(String keyword) {
        String filter = "%" + keyword.trim() + "%";
        String sql = "SELECT COUNT(*) FROM college WHERE code LIKE ? OR name LIKE ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, filter);
            pstmt.setString(2, filter);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("count failed: " + e.getMessage());
        }
        return 0;
    }

    // ── EXISTS CHECK ─────────────────────────────────────────────────────────
    public boolean exists(String code) {
        return getCollege(code) != null;
    }

    public List<College> searchAll(String code, String name) {
        List<College> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT code, name FROM college WHERE 1=1");

        if (!code.isEmpty()) {
            sql.append(" AND code LIKE ?");
        }
        if (!name.isEmpty()) {
            sql.append(" AND name LIKE ?");
        }

        sql.append(" ORDER BY code");

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());

            int i = 1;
            if (!code.isEmpty()) {
                pstmt.setString(i++, code + "%");  // starts with
            }
            if (!name.isEmpty()) {
                pstmt.setString(i++, name + "%");  // starts with
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new College(
                        rs.getString("code"),
                        rs.getString("name")
                ));
            }
        } catch (Exception e) {
            System.err.println("searchAll colleges failed: " + e.getMessage());
        }
        return list;
    }
}
