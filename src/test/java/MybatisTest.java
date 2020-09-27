import com.alexpower.bean.Employee;
import com.alexpower.dao.EmployeeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/*
* 1.
* 2. sqlSession 代表和数据库的一次对话，用完必须关闭
* 3. sqlSession 和 connection 一样是线程不安全的， 所以每次使用都应该重新获取新的对象
* 4. mapper 接口无实现类， 但是 mybatis 会 通过 getMapper() 实现动态代理，为接口动态地创建一个代理实现类
* 5. 两个重要配置文件：
*           mybatis 全局配置文件中， 包含 数据库信息， 事务管理器信息等， 系统运行环境信息
*           sql 映射文件中，保存所有 SQL 语句的映射信息：
*                         将SQL 提取出来
* */

public class MybatisTest {
    public SqlSessionFactory getSessionFactory() throws IOException {
        String resource = "config/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    /*
    Steps:
    * 1,according to global config file (mybatis-config.xml) to generate a sqlSessionFactory instance
    * 2,sql mapped xml: configured each sql and encapsulate rule
    * 3, save sql mapping xml into global config file (mybatis-config.xml)
    * 4, coding:
    *       a. get sqlSessionFactory from config file
    *       b. get sqlsessin from sqlSessinFactory and use it to operate CRUD, one sqlsession represents one session to db,
    *           close when finish
    *       c. tell Mybatis which sql to use by the mapping id (the unique identification). sqls are all restored in mapping file
    *
    * */

    @Test
    public void test() throws IOException {

        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession();

        /*
         * @param statement Unique identifier matching the statement to use.
         * @param parameter A parameter object to pass to the statement.
         *
         * */

        Object employee = sqlSession.selectOne("com.alexpower.dao.EmployeeMapper.getEmpById", 1);
        System.out.println("method 1 \nuse sqlSession.selectOne(“xxx”, object) : \n" +employee);

        sqlSession.close();
    }

    @Test
    public void test01() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        // get sqlSession from sessionFactory
        SqlSession sqlSession = sessionFactory.openSession();

        try{

            // get Proxy (代理对象) of interface EmployeeMapper
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            System.out.println(mapper + " / " + mapper.getClass()); // com.sun.proxy.$Proxy3 代理对象
            // call interface method getEmpById()
            Employee employee = mapper.getEmpById(1);

            System.out.println("method 2 \nuse getMapper to get instance of the Mapper Interface : \n" + employee);
        }finally {
            sqlSession.close();
        }


    }
}
