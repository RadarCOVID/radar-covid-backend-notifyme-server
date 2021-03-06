package ch.ubique.notifyme.sdk.backend.data.config;

import ch.ubique.notifyme.sdk.backend.data.DiaryEntryDataService;
import ch.ubique.notifyme.sdk.backend.data.JdbcDiaryEntryDataServiceImpl;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Profile("test-config")
@Configuration
public class TestConfig {
    @Autowired DataSource dataSource;

    @Bean
    public PlatformTransactionManager testTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public DiaryEntryDataService diaryEntryDataService() {
        return new JdbcDiaryEntryDataServiceImpl(dataSource);
    }
}
