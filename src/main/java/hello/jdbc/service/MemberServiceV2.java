package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection(); // 트랜잭션을 시작하려면 커넥션이 필요하다.
        try {
            con.setAutoCommit(false); // 트랜잭션 시작
            bizLogic(con, fromId, toId, money); // 트랜잭션이 시작된 커넥션을 전달하면서 비즈니스 로직을 수행한다.
            con.commit(); // 성공시 커밋
        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con); // finally {..} 를 사용해서 커넥션을 모두 사용하고 나면 안전하게 종료한다.
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                // 커넥션 풀을 사용하면 con.close() 를 호출 했을 때 커넥션이 종료되는 것이 아니라 풀에 반납된다.
                // 현재 수동 커밋 모드로 동작하기 때문에 풀에 돌려주기 전에 기본 값인 자동 커밋 모드로 변경하는 것이 안전하다.
                con.setAutoCommit(true); // 자동 커밋 모드로 변경
                con.close(); // 풀에 커넥션 반납
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
}
