package com.javasearch.www.controller;

import com.javasearch.www.pojo.ResultVO;
import com.javasearch.www.util.JsonUtils;
import com.javasearch.www.util.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        action(req, resp);
    }

    protected void action(HttpServletRequest req, HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        String requestURL = req.getRequestURL().toString();
        String[] strs1 = requestURL.split("\\?");
        if (strs1.length == 0) {
            Logger.Info("no such method!");
            return;
        }
        String[] strs = strs1[0].split("/");
        String methodName = strs[strs.length - 1];
        PrintWriter writer = null;
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            String s = JsonUtils.toString(method.invoke(this, req, res));
            if (s == null) {
                s = JsonUtils.toString(new ResultVO<>(4000, "OK", null));
            }
            writer = res.getWriter();
            writer.print(s);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


}
