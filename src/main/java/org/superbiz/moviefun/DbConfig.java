package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    @ConfigurationProperties("moviefun.datasources.movies")
    public DataSource moviesDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSource albumsDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean
    public DataSource moviesDataSourcePool(DataSource moviesDataSource) {
        HikariConfig config = new HikariConfig();
        config.setDataSource(moviesDataSource);
        return new HikariDataSource(config);
    }

    @Bean
    public DataSource albumsDataSourcePool(DataSource albumsDataSource) {
        HikariConfig config = new HikariConfig();
        config.setDataSource(albumsDataSource);
        return new HikariDataSource(config);
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(
            DataSource albumsDataSourcePool,
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter
    ) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(albumsDataSourcePool);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        entityManagerFactoryBean.setPersistenceUnitName("albums");
        return entityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean(
            DataSource moviesDataSourcePool,
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter
    ) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(moviesDataSourcePool);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        entityManagerFactoryBean.setPersistenceUnitName("movies");
        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager albumsTransactionManager(
            @Qualifier("albumsEntityManagerFactoryBean")
            LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(albumsEntityManagerFactoryBean.getObject());
        return transactionManager;
    }

    @Bean
    public PlatformTransactionManager moviesTransactionManager(
            @Qualifier("moviesEntityManagerFactoryBean")
            LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(moviesEntityManagerFactoryBean.getObject());
        return transactionManager;
    }
}
