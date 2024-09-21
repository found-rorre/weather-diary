package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest () {

        //given
        Memo NewMemo = new Memo(1, "new TestMemo");
        //when
        jdbcMemoRepository.save(NewMemo);

        //then
        Memo memo = jdbcMemoRepository.findById(1);
        assertEquals(memo.getText(), "new TestMemo");
    }

}