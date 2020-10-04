import com.alexpower.bean.Book;
import com.alexpower.bean.Department;
import com.alexpower.bean.Employee;
import com.alexpower.dao.BookMapper;
import com.alexpower.dao.DepartmentMapper;
import com.alexpower.dao.EmployeeMapper;
import com.alexpower.dao.EmployeeMapperPlus;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
    private static final Logger logger = LogManager.getLogger(MybatisTest.class);

    public SqlSessionFactory getSessionFactory() throws IOException {
        String resource = "config/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    /*
    *
    * There are two levels of Cache machanism in MyBatis
    cache mechanism:
    level 1 (local cache):
    inside sqlSession (default is on， sqlSession 级别的一个map)
    * 失效情况：
    * case1: 不同的sqlSession，查询条件相同，发送多次sql
    * case2: sqlSession 相同，查询条件不同， 发送新sql
    * case3: sqlSession 相同，两次之间进行了增删改操作
    * case4: sqlSession 相同，但是 sqlSession.clearCache(),进行缓存清空操作
    *
    level 2 (global cache):
    inside same namespace (mapper.xml) (default is off, need to set up manually)
    为了提高性能，mybatis 定义了缓存接口 Cache, 我们可以通过实现Cache接口来自定义二级缓存
    *
    *
    *
    *
    * */

    @Test
    public void testFirstLevelCache() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee empById = mapper.getEmpById(3);
            System.out.println(empById);

            // second time query
            Employee empById1 = mapper.getEmpById(3);
            System.out.println(empById1);
            // if it's same object of during two times
            System.out.println(empById == empById1); // true, cause first level cache (sqlSession cache) is on by default
        }finally {
            sqlSession.close();
        }
    }

    // 失效test
    @Test
    public void testFirstLevelCacheInValid() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession();;
        try {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee empById = mapper.getEmpById(3);
            System.out.println(empById);

            // case 1
//            SqlSession sqlSession1 = sessionFactory.openSession();
//            EmployeeMapper mapper1 = sqlSession1.getMapper(EmployeeMapper.class);
//            Employee empById1 = mapper1.getEmpById(3);
//            System.out.println(empById1);
//            System.out.println(empById == empById1); // false, different sqlSession

            // case 2 obvious it's different sql, so cache doesn't work
            // case 3
//            mapper.addEmp(new Employee(5,"CodePlayer", "1", "code@qq.com"));
//            Employee empById1 = mapper.getEmpById(3);
//            System.out.println(empById1);
//            System.out.println(empById == empById1); // false, insert/update/delete in between

            // case 4, clear cache
            sqlSession.clearCache();
            Employee empById1 = mapper.getEmpById(3);
            System.out.println(empById1);
            System.out.println(empById == empById1); // false, cache was cleared in between
        }finally {
            sqlSession.close();
        }
    }

   }
