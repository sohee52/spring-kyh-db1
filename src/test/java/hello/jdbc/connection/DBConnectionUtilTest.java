package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class DBConnectionUtilTest {

    @Test
    void connection() {
        // 주의! 실행전에 H2 데이터베이스 서버를 실행해두어야 한다. ( h2.sh , h2.bat )
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}
