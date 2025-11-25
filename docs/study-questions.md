- JDBC의 표준 인터페이스 세 가지(java.sql.Connection, java.sql.Statement, java.sql.ResultSet)의 역할을 각각 한 문장으로 설명하라.
- SQL Mapper(예: JdbcTemplate, MyBatis)와 ORM(예: JPA)의 차이점과 각각을 선택해야 할 상황을 사례 중심으로 비교해라.
- close(Connection con, Statement stmt, ResultSet rs) 메서드에서 각 리소스를 닫는 순서를 말하시오.
- 커넥션 풀을 사용하는 이유와 장점을 설명하라.
- DataSource 인터페이스의 역할과 이를 사용하는 이유를 설명하라.
- 트랜잭션의 ACID 특성에 대해 설명하라.
- JDBC에서 자동 커밋 모드와 수동 커밋 모드의 차이점을 설명하라.
- 데이터베이스 락에 대한 다음의 설명 중 옳지 않은 것은?
  - 데이터베이스 락은 동시성 문제를 해결하기 위해 사용된다.
  - 일반적인 조회에도 락을 사용한다.
  - select for update 구문을 사용하여 락을 획득할 수 있다.
  - 트랜잭션이 커밋되면 락이 해제된다.
- 애플리케이션에서 트랜잭션을 어떤 계층에 걸어야 하는지 말하시오.
- 스프링이 제공하는 트랜잭션 매니저의 역할을 2가지 설명하시오.
- 트랜잭션을 시작하고, 비즈니스 로직을 실행하고, 성공하면 커밋하고, 예외가 발생해서 실패하면 롤백하는 패턴을 무엇이라고 하는가?
- 스프링의 트랜잭션 어노테이션(@Transactional)을 사용하는 이유를 설명하라.
- 스프링 부트에서 트랜잭션 매니저 빈이 자동으로 등록되는 조건을 설명하라.
- Exception, RuntimeException, Error 의 차이점을 설명하라.
- 체크 예외(Checked Exception)와 언체크 예외(Unchecked Exception)의 차이점을 설명하라.
- 체크 예외를 사용하는 상황과 언체크 예외를 사용하는 상황을 각각 설명하라.
- 다음과 같은 예외 변환 코드에서 수정할 부분을 찾아 고치시오.
```java
        catch (SQLException e) {
                throw new RuntimeSQLException();
        }
```
- 체크 예외와 인터페이스를 함께 사용할 때 발생하는 문제점과 이를 해결하는 방법을 설명하라.
- 스프링이 제공하는 데이터 접근 예외 추상화에서 DataAccessException의 역할을 설명하라.
- 스프링의 예외 변환기가 하는 역할을 설명하라.
- JDBC 사용 시 발생하는 반복 문제를 어떻게 해결해야 하는지 설명하라.