package hello.jdbc.repository;

import hello.jdbc.domain.Member;

// 특정 기술에 종속되지 않는 순수한 인터페이스이다.
// 이 인터페이스를 기반으로 특정 기술을 사용하는 구현체를 만들면 된다.
public interface MemberRepository {
    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);

}
