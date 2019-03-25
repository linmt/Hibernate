import demo.Customer;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Hibernate的工具类的测试
 * @author jt
 *
 */
//测试对象的三种状态
public class test1 {

    @Test
    //查看三种状态
    public void fun1(){
        //1 获得session
        Session session = HibernateUtils.openSession();
        //2 控制事务
        Transaction tx = session.beginTransaction();
        //3执行操作
        Customer c = new Customer(); // 没有id, 没有与session关联 => 瞬时状态
        c.setCust_name("联想"); // 瞬时状态

        session.save(c); // 持久化状态, 有id,有关联，被session管理

        //4提交事务.关闭资源
        tx.commit();
        session.close();// 游离|托管 状态, 有id , 没有关联
    }

    @Test
    //三种状态特点
    //save方法: 其实不能理解成保存.理解成将瞬时状态转换成持久状态的方法
    //主键自增 : 执行save方法时,为了将对象转换为持久化状态.必须生成id值.所以需要执行insert语句生成.
    //increment: 执行save方法,为了生成id.会执行查询id最大值的sql语句.
    public void fun2(){
        //1 获得session
        Session session = HibernateUtils.openSession();
        //2 控制事务
        Transaction tx = session.beginTransaction();
        //3执行操作
        Customer c = new Customer(); // 没有id, 没有与session关联 => 瞬时状态

        c.setCust_name("联想"); // 瞬时状态

        session.save(c); // 持久化状态, 有id,有关联

        //4提交事务.关闭资源
        tx.commit();
        session.close();// 游离|托管 状态, 有id , 没有关联


    }

    @Test
    //持久化状态对象的任何变化都会自动同步到数据库中.
    public void fun3(){
        Session session = HibernateUtils.openSession();
        Transaction tx = session.beginTransaction();

        //如果set的内容和数据库的一样，是不会更新的，如果不一样才会更新
        Customer c = session.get(Customer.class, 1l);//持久化状态对象

        c.setCust_name("微软公司");

        //4提交事务.关闭资源
        tx.commit();
        session.close();// 游离|托管 状态, 有id , 没有关联
    }

    @Test
    //测试一级缓存
    public void fun4(){
        Session session = HibernateUtils.openSession();
        Transaction tx = session.beginTransaction();

        Customer c1 = session.get(Customer.class, 1l);
        System.out.println(c1);

        // 没有发生SQL，从一级缓存中获取数据
        Customer c2 = session.get(Customer.class, 1l);
        System.out.println(c1 == c2);//true 一级缓存，存的是对象的地址

        tx.commit();
        session.close();
    }

    @Test
    public void snapshot(){
        Session session = HibernateUtils.openSession();
        Transaction tr = session.beginTransaction();

        Customer c = session.get(Customer.class, 1l);
        c.setCust_name("老王");

        tr.commit();
        session.close();
    }

    @Test
    public void CurrentSession(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tr = session.beginTransaction();

        Customer c = session.get(Customer.class, 1l);
        c.setCust_name("dawang");
        session.save(c);

        tr.commit();
//        session.close();
    }

    @Test
    //测试Query
    public void fun5(){
        Session session = HibernateUtils.openSession();
        Transaction tx = session.beginTransaction();

        // 1.查询所有记录
		Query query = session.createQuery("from Customer");
		List<Customer> list = query.list();
		System.out.println(list);

        // 2.条件查询:
        //不再支持该语法
//		Query query = session.createQuery("from Customer where cust_name = ?");
//		query.setString(0, "微软公司");
//		List<Customer> list = query.list();
//		System.out.println(list);

        // 3.条件查询:
//		Query query = session.createQuery("from Customer where cust_name = :aaa and cust_level = :bbb");
//		query.setString("aaa", "微软公司");
//		query.setInteger("bbb", 1);
//		List<Customer> list = query.list();
//		System.out.println(list);

        // 4.分页查询:
//        Query query = session.createQuery("from Customer");
//        query.setFirstResult(3);
//        query.setMaxResults(5);
//        List<Customer> list = query.list();
//        System.out.println(list);

        tx.commit();
        session.close();
    }

    @Test
    //测试Criteria
    public void fun6(){
        Session session = HibernateUtils.openSession();
        Transaction tx = session.beginTransaction();

        // 1.查询所有记录
//		Criteria criteria = session.createCriteria(Customer.class);
//		List<Customer> list = criteria.list();
//		System.out.println(list);

        // 2.条件查询
//		Criteria criteria = session.createCriteria(Customer.class);
//		criteria.add(Restrictions.eq("cust_name", "微软公司"));
//		List<Customer> list = criteria.list();
//		System.out.println(list);

        // 3.条件查询
		Criteria criteria = session.createCriteria(Customer.class);
//		criteria.add(Restrictions.eq("cust_name", "微软公司"));
		criteria.add(Restrictions.eq("cust_level", "1"));
//        criteria.add(Restrictions.like("cust_name", "微软公司"));
        criteria.add(Restrictions.like("cust_name", "微软公司", MatchMode.END));
		List<Customer> list = criteria.list();
		System.out.println(list);

        // 4.分页查询
//        Criteria criteria = session.createCriteria(Customer.class);
//        criteria.setFirstResult(3);
//        criteria.setMaxResults(3);
//        List<Customer> list = criteria.list();
//        System.out.println(list);

        tx.commit();
        session.close();
    }

    @Test
    //测试SQLQuery
    public void fun7(){
        Session session = HibernateUtils.openSession();
        Transaction tx = session.beginTransaction();

        //　基本查询
//		SQLQuery sqlQuery = session.createSQLQuery("select * from cst_customer");
//		List<Object[]> list = sqlQuery.list();
//
//		for (Object[] objects : list) {
//			System.out.println(Arrays.toString(objects));
//		}

//        SQLQuery sqlQuery = session.createSQLQuery("select * from cst_customer");
//        // 封装到对象中
//        sqlQuery.addEntity(Customer.class);
//        List<Customer> list = sqlQuery.list();
//
//        for(Customer customer:list){
//            System.out.println(customer);
//        }

//        String sql = "select * from cst_customer where cust_id = ? ";
//        SQLQuery query = session.createSQLQuery(sql);
//        query.setParameter(1, 1l);
//        query.addEntity(Customer.class);
//        List<Customer> list = query.list();
//        System.out.println(list);

        String sql = "select * from cst_customer  limit ?,? ";
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(1, 0);
        query.setParameter(2, 1);
        query.addEntity(Customer.class);
        List<Customer> list = query.list();
        System.out.println(list);

        tx.commit();
        session.close();
    }
}