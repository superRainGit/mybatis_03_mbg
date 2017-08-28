package cn.zhang.test;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import cn.zhang.beans.Employee;
import cn.zhang.beans.EmployeeExample;
import cn.zhang.beans.EmployeeExample.Criteria;
import cn.zhang.dao.EmployeeMapper;

public class TestMbg {
	
	private static final Logger logger = LogManager.getLogger(TestMbg.class.getName());

	public SqlSessionFactory getSqlSessionFactory() throws IOException {
		String resource = "mybatis-config.xml";
		InputStream stream = Resources.getResourceAsStream(resource);
		return new SqlSessionFactoryBuilder().build(stream);
	}

	/**
	 * 测试使用简单版的生成规则生成出的mapper文件
	 * @throws IOException
	 */
	@Test
	public void testMybatis3Simple() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			Employee emp = mapper.selectByPrimaryKey(1);
			logger.info(emp);
		} finally {
			session.close();
		}
	}
	
	@Test
	public void testMybatis3() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			// 当参数不传递的时候默认就是没有任何的查询或者其他CRUD操作的条件
//			List<Employee> emps = mapper.selectByExample(null);
			EmployeeExample example = new EmployeeExample();
			// 一个Criteria表示一个查询条件   当需要使用和的关系的时候  使用一个查询条件就可以了
			// 如果使用的是或的关系   那就需要使用example创建一个新的Criteria  然后使用example的or方法进行链接
			Criteria criteria = example.createCriteria();
			criteria.andLastNameLike("%e%");
			criteria.andGenderEqualTo("1");
			Criteria criteria2 = example.createCriteria();
			criteria2.andEmailLike("%t%");
			example.or(criteria2);
			// 经过上述查询条件的封装之后   查询的SQL语句就会变成
			// select id, last_name, gender, email, d_id from employee 
			// WHERE ( last_name like ? and gender = ? ) or( email like ? )
			List<Employee> emps = mapper.selectByExample(example);
			for(Employee emp : emps) {
				logger.info(emp);
			}
		} finally {
			session.close();
		}
	}
	
}
