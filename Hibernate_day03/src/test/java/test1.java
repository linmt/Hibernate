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
        //可以保存赵洪
//         session.save(customer);
        //Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Column 'lkm_cust_id' cannot be null
        session.save(linkMan);

        tx.commit();
    }
}