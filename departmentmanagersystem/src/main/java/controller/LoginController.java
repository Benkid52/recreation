package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import service.SlipService;
import service.UserService;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ylr
 */
@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
    @Autowired
    private SlipService slipService;

    @Autowired
    private UserService userService;

    //获取滑块信息
    @RequestMapping(value = "/getSlip", method = RequestMethod.GET)
    public void getSlip() throws IOException {
        PrintWriter out = this.getResponse().getWriter();
        out.write(slipService.getSplit(this.getRequset(), this.getResponse()));
        out.close();
    }

    //验证滑块
    @RequestMapping(value = "/confirmSlip", method = RequestMethod.POST)
    public void confirmSlip(int distance) throws IOException {
        PrintWriter out = this.getResponse().getWriter();
        out.write(slipService.confirm(this.getRequset(), distance) + "");
        out.close();
    }
}
