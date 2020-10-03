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

    /*
    *  test insert, update, delete
    *  1. mybatis 允许增删改直接定义接收以下类型的返回值：
    *       Integer, Long, Boolean
    *  2. 需要手动提交数据：
    *             SqlSession sqlSession = sessionFactory.openSession(); ==> won't auto commit, need to openSession.commit(), 手动提交
    *             SqlSession sqlSession = sessionFactory.openSession(true); ==>  auto commit
    * */
    @Test
    public void testGetDeptByIdStep() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);
        try{
            DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);
            Department deptByIdStep = mapper.getDeptByIdStep(1);
            System.out.println(deptByIdStep);
            System.out.println(deptByIdStep.getEmps());

        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void testGetEmpByIdSteps() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);
        try{
            EmployeeMapperPlus mapper = sqlSession.getMapper(EmployeeMapperPlus.class);
            Employee empById = mapper.getEmpByIdSteps(3);
            System.out.println(empById);
//            System.out.println(empById.getDepartment());
        }finally {
            sqlSession.close();
        }
    }

//    public Employee getEmpWithDeptById(Integer id);
    @Test
    public void testJoinedTableQuery() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);
        try{
            EmployeeMapperPlus mapper = sqlSession.getMapper(EmployeeMapperPlus.class);
            Employee empById = mapper.getEmpWithDeptById(3);
            System.out.println(empById);
            System.out.println(empById.getDepartment());
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void testResultMap() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);
        try{
            EmployeeMapperPlus mapper = sqlSession.getMapper(EmployeeMapperPlus.class);
            Employee empById = mapper.getEmpById(3);
            System.out.println(empById);
        }finally {
            sqlSession.close();
        }
    }


    @Test
    public void getEmpsByNameLikeReturnMap() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);
        try{
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Map<Integer, Employee> emp = mapper.getEmpsByNameLikeReturnMap("%o%");
            System.out.println(emp.size());
            Set<Map.Entry<Integer, Employee>> entries = emp.entrySet();
            Iterator<Map.Entry<Integer, Employee>> iterator = entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<Integer, Employee> next = iterator.next();
                System.out.println(next.getKey() + ": " + next.getValue());
            }
        }finally {
            sqlSession.close();
        }
    }
/*
*   1: Employee{id=1, lastName='Yao', gender=1, email='byao@gmail.com'}
    3: Employee{id=3, lastName='Boy', gender=1, email='boooo@hotmail.com'}
*
* */

    @Test
    public void testReturnsMap() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);
        try{
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Map<String, Object> emp = mapper.getEmpByIdReturnMap(3);
            System.out.println(emp.size());
            Set<Map.Entry<String, Object>> entries = emp.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                System.out.println(next.getKey() + ": " + next.getValue());
            }
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectLike() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);
        try{
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            List<Employee> emps = mapper.getEmpsByNameLike("%o%");
            for (Employee emp: emps){
                System.out.println(emp);
            }
        }finally {
            sqlSession.close();
        }
    }

    // test null insert Oracle exception
    @Test
    public void test06() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);

        try{

            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);

            // test add
            Employee emp = new Employee(null, "Ham", "0", null);
//             return an Integer as defined in interface method
            Integer integer = mapper.addEmp(emp);
            System.out.println("new emp added: " + integer);
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test05() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        // get sqlSession from sessionFactory and set to true to auto commit
        SqlSession sqlSession = sessionFactory.openSession(true);
        try {
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
//            Book bookById = mapper.getBookById(1);
//            System.out.println(bookById);
            Book book = new Book(null, "Book2", 3);
            Boolean aBoolean = mapper.addBook(book);
            System.out.println("new book added? " + aBoolean);
//
            System.out.println("new book id is: " + book.getBookId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error: ", e);
        }finally {
            sqlSession.close();
        }

    }


    @Test
    public void test04() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);

        try{
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", 4);
            map.put("lastName", "Ham");
            Employee ham = mapper.getEmpByMap(map);
            System.out.println(ham);
        }finally {
            sqlSession.close();
        }


    }

    @Test
    public void test03() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);

        try{
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee boy = mapper.getEmpByIdAndLastName(3, "Boy");
            System.out.println(boy);
        }finally {
            sqlSession.close();
        }


    }


    @Test
    public void test02() throws IOException {
        SqlSessionFactory sessionFactory = this.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(true);

        try{

            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);

            // test add
            Employee ham = new Employee(null, "Ham", "0", "ham@gmail.com");
//             return an Integer as defined in interface method
            Integer integer = mapper.addEmp(ham);
            System.out.println("new emp added: " + integer);
            // will get the primary key value
            System.out.println(ham.getId());

            // test udpate
//            Employee mari = new Employee(3, "mari", "0", "mari@hotmail.com");
//            Boolean aBoolean = mapper.updateEmp(mari);
//            System.out.println("emp No." + mari.getId() + " has been updated? " + aBoolean);

            // test delete
//            mapper.deleteEmpById(2);
//            System.out.println("deleted emp No.2");

//            sqlSession.commit();
        }finally {
            sqlSession.close();
        }


    }


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
