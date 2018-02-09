package com.distributed.transaction.conf.db;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactorySecondary", basePackages = {
		"com.distributed.transaction.repository.distributed" }, transactionManagerRef = "transactionManagerSecondary")
public class SecondaryDSEntityManagerConfig {

	@Autowired
	@Qualifier("secondaryDataSource")
	private DataSource secondaryDS;

	@Autowired
	private JpaProperties jpaProperties;

	private Map<String, String> getVendorProperties(DataSource ds) {
		return jpaProperties.getHibernateProperties(ds);
	}

	@Bean(name = "entityManagerFactorySecondary")
	public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(secondaryDS).properties(getVendorProperties(secondaryDS))
				.packages("com.distributed.transaction.domain.distributed")
				.persistenceUnit("secondaryDSPersistenceUnit").build();
	}

	@Bean(name = "entityManagerSecondary")
	public EntityManager entityManger(EntityManagerFactoryBuilder builder) {
		return entityManagerFactorySecondary(builder).getObject().createEntityManager();
	}

	@Bean(name = "transactionManagerSecondary")
	public JpaTransactionManager transactionManagerSecondary(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(entityManagerFactorySecondary(builder).getObject());
	}
}
