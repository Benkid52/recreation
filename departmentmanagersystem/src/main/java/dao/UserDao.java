package dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pojo.User;

/**
 * @author ylr
 */
@Repository
public class UserDao extends SqlSessionDaoSupport {
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public Integer getId(String account) {
        SqlSession session = this.getSqlSession();
        Integer id = session.selectOne("user.getId", account);
        return id;
    }

    public User selectUser(int id) {
        SqlSession session = this.getSqlSession();
        User user = session.selectOne("user.selectUser", id);
        return user;
    }

    public void insertUser(User user) {
        SqlSession session = this.getSqlSession();
        session.insert("user.insertUser", user);
    }

    public void updateUser(User user) {
        SqlSession session = this.getSqlSession();
        session.update("user.updateUser", user);
    }
}
