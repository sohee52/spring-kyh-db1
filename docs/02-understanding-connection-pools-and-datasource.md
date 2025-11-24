# 2. 커넥션풀과 데이터소스 이해
## 커넥션 풀 이해
### 데이터베이스 커넥션을 획득하는 과정
1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회한다.
2. DB 드라이버는 DB와 TCP/IP 커넥션을 연결한다. 물론 이 과정에서 3 way handshake 같은 TCP/IP 연결을 위한 네트워크 동작이 발생한다.
3. DB 드라이버는 TCP/IP 커넥션이 연결되면 ID, PW와 기타 부가정보를 DB에 전달한다.
4. DB는 ID, PW를 통해 내부 인증을 완료하고, 내부에 DB 세션을 생성한다.
5. DB는 커넥션 생성이 완료되었다는 응답을 보낸다.
6. DB 드라이버는 커넥션 객체를 생성해서 클라이언트에 반환한다.
### 커넥션 풀의 등장
- 커넥션을 새로 만드는 것은 과정도 복잡하고 시간도 많이 많이 소모되는 일이다.
- 이런 문제를 한번에 해결하는 아이디어가 바로 커넥션을 미리 생성해두고 사용하는 커넥션 풀이라는 방법이다.
- 기본값은 보통 10개이다.
- 커넥션 풀에 들어 있는 커넥션은 TCP/IP로 DB와 커넥션이 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL을 DB에 전달할 수 있다.
- 커넥션을 모두 사용하고 나면 이제는 커넥션을 종료하는 것이 아니라, 다음에 다시 사용할 수 있도록 해당 커넥션을 그대로 커넥션 풀에 반환하면 된다.
- 여기서 주의할 점은 커넥션을 종료하는 것이 아니라 커넥션이 살아있는 상태로 커넥션 풀에 반환해야 한다는 것이다.
- 성능과 사용의 편리함 측면에서 최근에는 hikariCP 를 커넥션 풀 오픈소스로 대부분 사용한다.
## Datasource 이해
- DriverManager 를 통해서 커넥션을 획득하다가, 커넥션 풀을 사용하는 방법으로 변경하려면 어떻게 해야할까?
- 예를 들어서 애플리케이션 로직에서 DriverManager 를 사용해서 커넥션을 획득하다가 HikariCP 같은 커넥션 풀을 사용하도록 변경하면 커넥션을 획득하는 애플리케이션 코드도 함께 변경해야 한다.
- 의존관계가 DriverManager 에서 HikariCP 로 변경되기 때문이다.
### 커넥션 획득 방법 추상화
- 이를 해결하기 위해서 나온 아이디어가 바로 커넥션 획득 방법을 추상화하는 것이다.
- 자바에서는 이를 위해서 DataSource 라는 인터페이스를 제공한다.
- 이 인터페이스의 핵심 기능은 커넥션 조회 하나이다.
## Datasource 예제1 - DriverManager
- 코드: src/test/java/hello/jdbc/connection/ConnectionTest.java
## Datasource 예제2 - 커넥션 풀
- 코드: src/test/java/hello/jdbc/connection/ConnectionTest.java
## Datasource 적용
- 코드: src/main/java/hello/jdbc/repository/MemberRepositoryV1.java
- 코드: src/test/java/hello/jdbc/repository/MemberRepositoryV1Test.java
### DI (Dependency Injection)
- DriverManagerDataSource HikariDataSource 로 변경해도 MemberRepositoryV1 의 코드는 전혀 변경하지 않아도 된다.
- MemberRepositoryV1 는 DataSource 인터페이스에만 의존하기 때문이다. 이것이 DataSource 를 사용하는 장점이다