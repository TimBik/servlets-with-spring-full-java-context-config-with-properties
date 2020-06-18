package ru.itis.servlets.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

// говорим, что этот класс
// используется для создания бинов
@Configuration

// указываем, где находится файлы properties
@PropertySource("classpath:application.properties")

// говорим, где искать классы,
// которые так же будут создавать бины,
// например классы с аннотациями
// component, repository
@ComponentScan(basePackages = "ru.itis.servlets")

public class ApplicationContextConfig {

    //используем, для получения
    // доступа к properties
    @Autowired
    private Environment environment;


    // кладем этот объект в контейнер
    // тоесть при анотации
    // @Autowired JdbcTemplate ...
    // будет подтягиваться
    // этот singleton объект
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(hikariDataSource());
    }

    // говорим как создавать DataSource
    // используем встроенную в спринг
    // реаизацию
    @Bean
    public DataSource driverManagerDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        // по данным из
        // файла application.properties
        // настраиваем объект dataSource
        dataSource.setDriverClassName(environment.getProperty("db.driver"));
        dataSource.setUrl(environment.getProperty("db.url"));
        dataSource.setUsername(environment.getProperty("db.user"));
        dataSource.setPassword(environment.getProperty("db.password"));
        return dataSource;
    }

    // говорим как инициализируется объект HikariConfig,
    // после кладем в контейнер бинов
    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();

        // по данным из
        // файла application.properties
        // настраиваем объект config
        config.setJdbcUrl(environment.getProperty("db.url"));
        config.setUsername(environment.getProperty("db.user"));
        config.setPassword(environment.getProperty("db.password"));
        config.setDriverClassName(environment.getProperty("db.driver"));
        return config;
    }

    // реализация DataSource через HikariCP
    // какая и реализаций будет использована
    // зависит от аннотации @Qualifier
    @Bean
    public DataSource hikariDataSource() {
        return new HikariDataSource(hikariConfig());
    }
}
