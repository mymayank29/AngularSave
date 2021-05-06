package com.chevron.edap.gomica.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Autowired
    private ConfigurationProvider configurationProvider;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(configurationProvider.getJdbcUrl().replaceAll("\"", ""));
        dataSource.setDriverClassName(configurationProvider.getDriverClassName());
        dataSource.setUsername(configurationProvider.getDbUserName());
        dataSource.setPassword(configurationProvider.getDbPassword());
        dataSource.setInitialSize(configurationProvider.getDbPoolInitialSize());
        dataSource.setMaxTotal(configurationProvider.getDbPoolMaxTotal());
        dataSource.setMaxIdle(configurationProvider.getDbPoolMaxIdle());
        dataSource.setMinIdle(configurationProvider.getDbPoolMinIdle());

        return dataSource;
    }

}