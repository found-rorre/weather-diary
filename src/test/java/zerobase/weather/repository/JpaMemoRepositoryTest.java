package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        //given
        Memo memo = new Memo(1, "this is jpa memo");

        //when
        jpaMemoRepository.save(memo);

        //then
        Optional<Memo> result = jpaMemoRepository.findById(1);
        assertEquals(result.get().getText(), "this is jpa memo");


    }
}