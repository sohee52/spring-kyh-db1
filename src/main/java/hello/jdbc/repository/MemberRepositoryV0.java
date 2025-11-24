package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    // 회원 등록
    public Member save(Member member) throws SQLException {
        // 데이터베이스에 전달할 SQL 정의
        String sql = "insert into member(member_id, money) values (?, ?)";
        
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection(); // 데이터베이스 커넥션 획득
            pstmt = con.prepareStatement(sql); // 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비한다.
            // SQL의 첫번째 ? 에 값을 지정한다
            pstmt.setString(1, member.getMemberId());
            // : SQL의 두번째 ? 에 값을 지정한다.
            pstmt.setInt(2, member.getMoney());
            // 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다.
            // 참고로 executeUpdate() 은 int 를 반환하는데 영향받은 DB row 수를 반환한다.
            // 여기서는 하나의 row를 등록했으므로 1을 반환한다.
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // 리소스를 정리할 때는 항상 역순으로 해야한다.
            // Connection 을 먼저 획득하고 Connection 을 통해 PreparedStatement 를 만들었기 때문에
            // 리소스를 반환할 때는 PreparedStatement 를 먼저 종료하고, 그 다음에 Connection 을 종료하면 된다.
            close(con, pstmt, null);
        }

    }

    // 회원 조회
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            // 데이터를 변경할 때는 executeUpdate() 를 사용하지만,
            // 데이터를 조회할 때는 executeQuery() 를 사용한다.
            // executeQuery() 는 결과를 ResultSet 에 담아서 반환한다.
            rs = pstmt.executeQuery();
            // findById() 에서는 회원 하나를 조회하는 것이 목적이다. 따라서 조회 결과가 항상 1건이므로 while 대신에 if를 사용한다.
            if (rs.next()) { // 이것을 호출하면 커서가 다음으로 이동한다.
                // 참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 rs.next() 를 최초 한번은 호출해야 데이터를 조회할 수 있다.
                // true 를 반환하면 데이터가 존재한다는 뜻이고, false 를 반환하면 데이터가 없는 것이다.
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }

    // 회원 수정
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); // 영향받은 row 수 반환
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    // 회원 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

    }


    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }


}
