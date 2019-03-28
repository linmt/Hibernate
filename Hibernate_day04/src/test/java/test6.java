import demo.Customer;
import demo.LinkMan;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import java.util.List;

/**
 * 批量抓取
 */
public class test6 {
    @SuppressWarnings("unchecked")
    @Test
    /**
     * 获取客户的时候，批量抓取联系人
     * 在Customer.hbm.xml中set上配置batch-size
     */
    public void demo1(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        //默认情况：先查所有客户，然后遍历的时候根据客户id查询该id所有联系人
        //加了batch-size之后：发送查询客户的语句之后再发送查询3个联系人的语句
        List<Customer> list = session.createQuery("from Customer").list();
        for (Customer customer : list) {
            System.out.println(customer.getCust_name());
            for (LinkMan linkMan : customer.getLinkMans()) {
                System.out.println(linkMan.getLkm_name());
            }
        }
        tx.commit();
    }

    @SuppressWarnings("unchecked")
    @Test
    /**
     * 获取联系人的时候，批量抓取客户
     * * 在Customer.hbm.xml中<class>上配置batch-size="3"
     */
    public void demo2(){
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();

        //默认情况：发送一条查询所有联系人的语句 + 两条查询客户的语句
        List<LinkMan> list = session.createQuery("from LinkMan").list();
        for (LinkMan linkMan : list) {
            System.out.println(linkMan.getLkm_name());
            System.out.println(linkMan.getCustomer().getCust_name());
        }
        tx.commit();
    }
}
