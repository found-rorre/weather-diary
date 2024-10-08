package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class JdbcMemoRepository {
    private  final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMemoRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo) {
        String sql = "insert into memo values(?, ?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());

        return memo;
    }

    private RowMapper<Memo> memoRowMapper() {

        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }

    public List<Memo> findAll() {
        String sql ="select * from memo";

        return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Memo findById(int id) {
        String sql = "select * from memo where id = ?";

        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst().get();
    }
}
