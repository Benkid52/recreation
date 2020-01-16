package service;

import com.alibaba.fastjson.JSONObject;
import factory.SlipFactory;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.stereotype.Service;
import pojo.Slip;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author ylr
 */
@Service
public class SlipService {
    private static Log log = LogFactory.getLog(SlipService.class.getName());
    private Slip slip;

    /**
     * 获取滑块验证信息
     */
    public String getSplit(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject json = new JSONObject();
        slip = SlipFactory.getInstance();

        json.put("posY", slip.getRandY());      //y轴坐标
        json.put("big", slip.getBigBase64());       //大图base64
        json.put("small", slip.getSmallBase64());       //小图base64

        //保存在session中
        HttpSession session = request.getSession();
        session.setAttribute("slip", slip);
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setMaxAge(-1);       //关闭浏览器时删除
        response.addCookie(cookie);

        return json.toJSONString();
    }

    /**
     * 滑块验证
     */
    public boolean confirm(HttpServletRequest request, int distance) {
        boolean res = true;
        slip = (Slip)request.getSession().getAttribute("slip");
        if(slip == null) res = false;
        else {
            int randX = slip.getRandX();
            int good = slip.getGood();
            if(distance >= randX - good && distance <= randX + good) res = true;
            else res = false;
        }
        return res;
    }
}
