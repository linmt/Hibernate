import demo.Customer;
import demo.LinkMan;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

//一对多关联映射
public class test1 {

    @Test
    /**
     * 初始化数据
     */
    public void demo1() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 创建一个客户
        Customer customer = new Customer();
        customer.setCust_name("张三");

        for (int i = 1; i <= 10; i++) {
            LinkMan linkMan = new LinkMan();
            linkMan.setLkm_name("张三客户" + i);
            linkMan.setCustomer(customer);
            customer.getLinkMans().add(linkMan);
            session.save(linkMan);
        }
        session.save(customer);

        tx.commit();
    }

    @Test
    /**
     * HQL的简单查询
     */
    public void demo2() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        // 简单的查询
        //包名可省略
        Query query = session.createQuery("from Customer");
        List<Customer> list = query.list();

        // sql中支持*号的写法：select * from cst_customer; 但是在HQL中不支持*号的写法。
		/*
		 * Query query = session.createQuery("select * from Customer");// 报错
		 * List<Customer> list = query.list();
		 */

        for (Customer customer : list) {
            //打印的时候Customer中有LinkMan的集合，不能打印该集合，因为LinkMan中有Customer变量，会造成死循环
            System.out.println(customer);
        }
        tx.commit();
    }

    @Test
    /**
     * 别名查询
     */
    public void demo3() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        // 别名的查询
		/*
		 * Query query = session.createQuery("from Customer c"); List<Customer>
		 * list = query.list();
		 */

        Query query = session.createQuery("select c from Customer c");
        List<Customer> list = query.list();

        for (Customer customer : list) {
            System.out.println(customer);
        }
        tx.commit();
    }

    @Test
    /**
     * 排序查询
     */
    public void demo4() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        // 排序的查询
        // 默认升序1  2
        // List<Customer> list = session.createQuery("from Customer order by cust_id").list();
        // 设置降序排序 升序使用asc 降序使用desc
        List<Customer> list = session.createQuery("from Customer order by cust_id desc").list();

        for (Customer customer : list) {
            System.out.println(customer);
        }
        tx.commit();
    }

    @Test
    /**
     * 条件查询
     */
    public void demo5() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        // 条件的查询
        // 一、按位置绑定：根据参数的位置进行绑定。
        // 一个条件

//        Query query = session.createQuery("from Customer where cust_name = ?" );
//        query.setParameter(0, "张三");
//        List<Customer> list = query.list();


        // 多个条件
//        Query query = session.createQuery("from Customer where cust_source = ? and cust_name like ?");
//        query.setParameter(0, "小广告");
//        query.setParameter(1, "张%");
//        List<Customer> list = query.list();


        // 二、按名称绑定
        Query query = session.createQuery("from Customer where cust_source = :aaa and cust_name like :bbb");
        // 设置参数:
        query.setParameter("aaa", "小广告");
        query.setParameter("bbb", "张%");
        List<Customer> list = query.list();

        for (Customer customer : list) {
            System.out.println(customer);
        }
        tx.commit();
    }

    @Test
    /**
     * 投影查询
     */
    public void demo6() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 投影查询
        // 单个属性
//        List<Object> list = session.createQuery( "select c.cust_name from Customer c").list();
//        for (Object object : list) {
//            System.out.println(object);
//        }

        // 多个属性:
//        List<Object[]> list = session.createQuery("select c.cust_name,c.cust_source from Customer c").list();
//        for(Object[] objects : list) {
//            System.out.println(Arrays.toString(objects));
//        }

        // 查询多个属性，但是我想封装到对象中。
        //要有无参构造方法和带cust_name,cust_source变量的构造方法
        List<Customer> list = session.createQuery("select new Customer(cust_name,cust_source) from Customer").list();
        for (Customer customer : list) {
            System.out.println(customer);
        }
        tx.commit();
    }

    @Test
    /**
     * 分页查询
     */
    public void demo7() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 分页查询
        Query query = session.createQuery("from LinkMan");
        query.setFirstResult(5);
        query.setMaxResults(15);
        List<LinkMan> list = query.list();

        for (LinkMan linkMan : list) {
            System.out.println(linkMan);
        }
        tx.commit();
    }

    @Test
    /**
     * 分组统计查询
     */
    public void demo8() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 聚合函数的使用：count(),max(),min(),avg(),sum()
        Object object = session.createQuery("select count(*) from Customer").uniqueResult();
        System.out.println(object);
        // 分组统计：
        List<Object[]> list = session.createQuery("select cust_source,count(*) from Customer group by cust_source").list();
        for (Object[] objects : list) {
            System.out.println(Arrays.toString(objects));
        }
        tx.commit();
    }

    @Test
    /**
     * HQL的多表查询
     */
    public void demo9() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        // SQL:SELECT * FROM cst_customer c INNER JOIN cst_linkman l ON
        // c.cust_id = l.lkm_cust_id;
        // HQL:内连接 from Customer c inner join c.linkMans
//        List<Object[]> list = session.createQuery("from Customer c inner join c.linkMans").list();
//        for (Object[] objects : list) {
//            System.out.println(Arrays.toString(objects));
//        }


        // HQL:迫切内连接 其实就在普通的内连接inner join后添加一个关键字fetch.
        // from Customer c inner join fetch c.linkMans
        // 通知hibernate，将另一个对象的数据封装到该对象中
        //Customer的toString中要有Set<LinkMan>
        /*
           内连接和迫切内连接的区别:
         * 内连接:发送就是内连接的语句,封装的时候将每条记录封装到一个Object[]数组中,最后得到一个List<Object[]>.
         * 迫切内连接：发送的也是内连接的语句,在join后添加一个fetch关键字,Hibernate会将每条数据封装到对象中,最后List<Customer>. 需要去掉重复值.
         */
        List<Customer> list = session.createQuery("select distinct c from Customer c inner join fetch c.linkMans").list();
        for (Customer customer : list) {
            System.out.println(customer);
        }
        tx.commit();
    }
}