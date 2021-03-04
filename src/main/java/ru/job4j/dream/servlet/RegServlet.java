package ru.job4j.dream.servlet;

import ru.job4j.dream.PsqlStore;
import ru.job4j.dream.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if(!PsqlStore.instOf().existsUser(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setName(" ");
            PsqlStore.instOf().saveUser(user);
            HttpSession sc = req.getSession();
            sc.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/posts.do");
        } else {
            req.setAttribute("error", "Пользователь существует");
            req.getRequestDispatcher("reg.jsp").forward(req, resp);
        }
    }
}
