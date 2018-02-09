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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.distributed.transaction.domain.BankATimeTable;
import com.distributed.transaction.domain.BankBTimeTable;
import com.distributed.transaction.domain.ShardingTableRoute;
import com.distributed.transaction.repository.BankATimeTableRepo;
import com.distributed.transaction.repository.BankBTimeTableRepo;
import com.distributed.transaction.repository.ShardingTableRouteRepo;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPrimary", transactionManagerRef = "transactionManagerPrimary", basePackageClasses = {
		BankATimeTableRepo.class, BankBTimeTableRepo.class, ShardingTableRouteRepo.class })
public class PrimaryDSEntityManagerConfig {

	@Autowired
	@Qualifier("primaryDataSource")
	private DataSource primaryDS;

	@Autowired
	private JpaProperties jpaProperties;

	private Map<String, String> getVendorProperties(DataSource ds) {
		return jpaProperties.getHibernateProperties(ds);
	}

	@Primary
	@Bean(name = "entityManagerFactoryPrimary")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(primaryDS).properties(getVendorProperties(primaryDS))
				.packages(BankATimeTable.class, BankBTimeTable.class, ShardingTableRoute.class)
				.persistenceUnit("primaryDSPersistenceUnit").build();
	}

	@Primary
	@Bean(name = "entityManagerPrimary")
	public EntityManager entityManger(EntityManagerFactoryBuilder builder) {
		return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
	}

	@Primary
	@Bean(name = "transactionManagerPrimary")
	public JpaTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
	}

}
