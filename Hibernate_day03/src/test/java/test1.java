import demo.Customer;
import demo.LinkMan;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

//一对多关联映射
public class test1 {

    @Test
    // 向客户 和 联系人中同时保存数据：
    public void demo1(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 创建一个客户
        Customer customer = new Customer();
        customer.setCust_name("张总");

        // 创建两个联系人:
        LinkMan linkMan1 = new LinkMan();
        linkMan1.setLkm_name("秦助理");

        LinkMan linkMan2 = new LinkMan();
        linkMan2.setLkm_name("胡助理");

        // 建立关系:
        customer.getLinkMans().add(linkMan1);
        customer.getLinkMans().add(linkMan2);

        linkMan1.setCustomer(customer);
        linkMan2.setCustomer(customer);

        session.save(customer);
        session.save(linkMan1);
        session.save(linkMan2);

        tx.commit();
    }

    @Test
    // 一对多关系只保存一边是否可以
    public void demo2(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Customer customer = new Customer();
        customer.setCust_name("赵洪");

        LinkMan linkMan = new LinkMan();
        linkMan.setLkm_name("如花");
        customer.getLinkMans().add(linkMan);
        linkMan.setCustomer(customer);

        // 只保存一边是否可以：不可以，报一个瞬时对象异常：持久态对象关联了一个瞬时态对象。
        //org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: demo.Customer
        session.save(customer);
//        session.save(linkMan);

        tx.commit();
    }

    @Test
    // 保存客户级联联系人  只有Customer.hbm.xml配置cascade
    public void demo3(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Customer customer = new Customer();
        customer.setCust_name("赵洪");

        LinkMan linkMan = new LinkMan();
        linkMan.setLkm_name("如花");
        customer.getLinkMans().add(linkMan);
        linkMan.setCustomer(customer);

        session.save(customer);

        tx.commit();
    }

    @Test
    // 保存联系人级联客户  Customer.hbm.xml和LinkMan.hbm.xml都配置cascade
    public void demo4(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Customer customer = new Customer();
        customer.setCust_name("赵洪");

        LinkMan linkMan = new LinkMan();
        linkMan.setLkm_name("如花");
        customer.getLinkMans().add(linkMan);
        linkMan.setCustomer(customer);

        session.save(linkMan);

        tx.commit();
    }

    @Test
    // 测试对象导航  只有LinkMan.hbm.xml配置cascade
    public void demo5(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Customer customer = new Customer();
        customer.setCust_name("李兵");

        LinkMan linkMan1 = new LinkMan();
        linkMan1.setLkm_name("凤姐");
        LinkMan linkMan2 = new LinkMan();
        linkMan2.setLkm_name("如花");
        LinkMan linkMan3 = new LinkMan();
        linkMan3.setLkm_name("芙蓉");

        linkMan1.setCustomer(customer);
        customer.getLinkMans().add(linkMan2);
        customer.getLinkMans().add(linkMan3);

        // 双方都设置了cascade
		session.save(linkMan1); // 发送几条insert语句  4条     全都保存了，三个联系人外键lkm_cust_id字段有值
//		session.save(customer); // 发送几条insert语句  3条     只保存了customer，linkMan2，linkMan3
//        session.save(linkMan2); // 发送几条insert语句  1条   只保存了linkMan2

        tx.commit();
    }

    @Test
    /**
     * 级联删除：
     * * 删除客户级联删除联系人，删除的主体是客户，需要在Customer.hbm.xml中配置
     * * <set name="linkMans" cascade="delete">
     *
     *     执行前确保数据库中已经有demo5的数据，记得设置核心配置文件为update
     */
    public void demo6(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 没有设置级联删除，默认情况:修改了联系人的外键，删除客户
        //要去掉两个配置文件中的cascade="save-update"
        //update cst_linkman set lkm_cust_id=null where lkm_cust_id=?
//		Customer customer = session.get(Customer.class, 1l);
//		session.delete(customer);

        // 删除客户，同时删除联系人
        //删除了客户，和客户有主外键关联的联系人也被删除，没有和客户有主外键关联的联系人不会被删除
        Customer customer = session.get(Customer.class, 1l);
        session.delete(customer);

        tx.commit();
    }

    @Test
    /**
     * 级联删除：
     * * 删除联系人级联删除客户，删除的主体是联系人，需要在LinkMan.hbm.xml中配置
     * * <many-to-one name="customer" cascade="delete">
     * * 取消Customer.hbm.xml中cascade="delete"
     */
    public void demo7(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 删除客户，同时删除联系人
        LinkMan linkMan = session.get(LinkMan.class, 3l);
        session.delete(linkMan);

        tx.commit();
    }

    //创建数据：2个联系人，3个客户
    @Test
    public void createData(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Customer customer = new Customer();
        customer.setCust_name("一");
        Customer customer2 = new Customer();
        customer2.setCust_name("二");

        LinkMan linkMan1 = new LinkMan();
        linkMan1.setLkm_name("凤姐");
        LinkMan linkMan2 = new LinkMan();
        linkMan2.setLkm_name("如花");
        LinkMan linkMan3 = new LinkMan();
        linkMan3.setLkm_name("芙蓉");

        linkMan1.setCustomer(customer);
        customer.getLinkMans().add(linkMan2);
        customer2.getLinkMans().add(linkMan3);

        // 双方都设置了cascade
        session.save(linkMan1);
        session.save(customer2);

        tx.commit();
    }

    @Test
    /**
     * 将2号联系人原来归1号客户，现在改为2号客户
     */
    public void demo8(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        // 查询2号联系人
        LinkMan linkMan = session.get(LinkMan.class, 2l);
        // 查询2号客户
        Customer customer = session.get(Customer.class, 2l);
        // 双向的关联
        //两边都是cascade="save-update"
        //更新了两次cst_linkman的lkm_cust_id，这是双向维护关系产生多余的SQL
        //Customer.hbm.xml中配置<set name="linkMans" cascade="save-update,delete" inverse="true">解决多余SQL
        linkMan.setCustomer(customer);
        customer.getLinkMans().add(linkMan);

        tx.commit();
    }

    @Test
    /**
     * 区分cascade和inverse的区别
     */
    public void demo9(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Customer customer = new Customer();
        customer.setCust_name("李兵");

        LinkMan linkMan = new LinkMan();
        linkMan.setLkm_name("凤姐");

        customer.getLinkMans().add(linkMan);

        // 条件在Customer.hbm.xml上的set中配置了cascade="save-update" inverse="true"
        session.save(customer); // 客户会插入到数据库，联系人也会插入到数据库，但是外键为null

        tx.commit();
    }
}