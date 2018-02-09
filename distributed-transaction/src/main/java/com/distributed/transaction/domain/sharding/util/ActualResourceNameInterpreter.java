package com.distributed.transaction.domain.sharding.util;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Table;

public class ActualResourceNameInterpreter {

	public static String getTableName(Class<?> clazz) {
		return clazz.getDeclaredAnnotation(Table.class).name();
	}

	public static String getColumnName(Class<?> clazz, int colIndex) {
		Field target = null;
		int index = 0;
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getDeclaredAnnotation(Column.class) != null) {
				if (index == colIndex) {
					target = field;
					break;
				}
				index++;
			}
		}
		return target.getDeclaredAnnotation(Column.class).name();
	}

}
