package com.distributed.transaction.conf.db.naming.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class MysqlUpperCaseStrategy extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 1383021413247872469L;

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		return Identifier.toIdentifier(name.getText().toUpperCase());
	}

}
