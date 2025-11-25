package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave() {
        service.create("myId");
        service.create("myId"); // 같은 ID 저장 시도
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0)); // 처음에 저장을 시도한다.
                log.info("saveId={}", memberId);
            } catch (MyDuplicateKeyException e) { // 만약 리포지토리에서 MyDuplicateKeyException 예외가 올라오면 이 예외를 잡는다. 잡아서
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId); // generateNewId(memberId) 로 새로운 ID 생성을 시도한다.
                log.info("retryId={}", retryId);
                repository.save(new Member(retryId, 0)); // 그리고 다시 저장한다.
            } catch (MyDbException e) { // 복구할 수 없는 예외( MyDbException )면 로그만 남기고 다시 예외를 던진다.
                log.info("데이터 접근 계층 예외", e);
                throw e;
            }
        }

//        실행해보면 다음과 같은 로그를 확인할 수 있다.
//
//        Service - saveId=myId
//        Service - 키 중복, 복구 시도
//        Service - retryId=myId492
//`
//        같은 ID를 저장했지만, 중간에 예외를 잡아서 복구한 것을 확인할 수 있다.

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }

    }

    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, money) values(?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                //h2 db
                if (e.getErrorCode() == 23505) { // 오류 코드가 키 중복 오류( 23505 )인 경우
                    throw new MyDuplicateKeyException(e); // MyDuplicateKeyException 을 새로 만들어서 서비스 계층에 던진다.
                }
                throw new MyDbException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }
}
