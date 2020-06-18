package ru.itis.servlets.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.itis.servlets.models.Course;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
public class CoursesRepositoryJdbcTemplateImpl implements CoursesRepository {

    //language=`SQL`
    private static final String SQL_SELECT_BY_ID = "select * from course where id = ?";
    //language=SQL
    private static final String SQL_SELECT_ALL = "select * from course";
    //language=SQL
    private static final String SQL_INSERT = "insert into course(title) values (?)";

    //предоставляет доступ к JDBC API
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //говорим, как строить Course по row
    private RowMapper<Course> courseRowMapper = (row, rowNumber) ->
            Course.builder()
                    .id(row.getLong("id"))
                    .title(row.getString("title"))
                    .build();


    //поиск Course по id.
    //jdbcTemplate  по sql запросу, пришедшему id и courseRowMapper
    //создает нужный Course
    @Override
    public Course find(Long id) {
        return jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, new Object[]{id}, courseRowMapper);
    }

    //поиск всех Course в бд
    //по sql запросу и courseRowMapper
    public List<Course> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, courseRowMapper);
    }

    //сохраняем в бд новый Course
    public void save(Course entity) {
        //генератор ключей из HikariCP, используем для создания id
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // через PreparedStatement в sql запрос
        // вставляем данные из entity,
        // чтобы не было sql инъекции
        // и сохраняем в бд со сгенерированным ключем
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection
                    .prepareStatement(SQL_INSERT);
            statement.setString(1, entity.getTitle());
            return statement;
        }, keyHolder);

        //вставляем в пришедшию модель сгенерированный id
        entity.setId((Long) keyHolder.getKey());
    }

    public void delete(Long id) {

    }
}
