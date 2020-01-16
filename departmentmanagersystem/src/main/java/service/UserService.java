package service;

import com.alibaba.fastjson.JSONObject;
import dao.UserDao;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.User;
import util.MD5;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author ylr
 */
@Service
public class UserService {
    private static Log log = LogFactory.getLog(UserService.class.getName());

    @Autowired
    UserDao userDao;

    /**
     * 登录，增加账号信息到数据库，判断教务系统信息是否正确
     */
    public String enter(HttpServletRequest request, User user) {
        //学校教务系统
        String address = request.getParameter("address");
        //学校是否有效,目前只有广金有效
        if(address.equals("广东金融学院")) address = "http://jwxt.gduf.edu.cn/app.do";
        //无效学校
        else return "Address";

        //连接学校教务系统
        boolean isWorse = false;      //学校教务系统是否崩了
        InputStream is = null;
        InputStreamReader ir = null;
        BufferedReader buff = null;
        JSONObject json = null;
        try {
            address = address + "?method=" + request.getParameter("method") + "&xh=" + request.getParameter("xh")
                    + "&pwd=" + request.getParameter("pwd");
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            is = connection.getInputStream();
            ir = new InputStreamReader(is);
            buff = new BufferedReader(ir);
            StringBuffer sb = new StringBuffer();
            String temp = null;
            while((temp = buff.readLine()) != null) sb.append(temp);
            json = JSONObject.parseObject(sb.toString());
        } catch(Exception e) {
            isWorse = true;
        } finally {
            try {
                buff.close();
                ir.close();
                is.close();
            } catch(Exception e) {}
        }

        //学校崩了,通过数据库查找以前信息
        if(isWorse) {
            Integer id = userDao.getId(user.getAccount());
            if(id == null) return "NoAccountAndSystemBoom";
            User userMsg = userDao.selectUser(id);
            if(userMsg.getPassword().equals(MD5.getMD5(user.getPassword()))) return "Success";
            else return "PasswordError";
        } else {
            if(json.getString("flag").equals("1")) {
                Integer id = userDao.getId(user.getAccount());
                if(id == null) {
                    user.setAdmin(false);
                    user.setPassword(MD5.getMD5(user.getPassword()));
                    userDao.insertUser(user);
                } else {
                    User userMsg = userDao.selectUser(id);
                    if(!userMsg.getPassword().equals(MD5.getMD5(user.getPassword()))) {
                        userMsg.setPassword(MD5.getMD5(user.getPassword()));
                        userDao.updateUser(userMsg);
                    }
                }
                return "Success";
            } else return "PasswordError";
        }
    }
}
