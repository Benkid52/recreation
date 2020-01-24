package dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojo.Belong;

/**
 * @author ylr
 */
@Repository
public class BelongDao extends SqlSessionDaoSupport {
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public Belong getBelong(int id) {
        SqlSession session = this.getSqlSession();
        Belong res = session.selectOne("belong.selectById", id);
        session.close();
        return res;
    }

    public void updateBelong(Belong belong) {
        SqlSession session = this.getSqlSession();
        session.update("belong.updateBelong", belong);
        session.close();
    }
}
