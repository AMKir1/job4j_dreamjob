package ru.job4j.dream.servlet;

import ru.job4j.dream.PsqlStore;
import ru.job4j.dream.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if(PsqlStore.instOf().existsUser(email)) {
            User currentUser = PsqlStore.instOf().findUserByEmail(email);
            if(currentUser.getPassword().equals(password)) {
                HttpSession sc = req.getSession();
                sc.setAttribute("user", currentUser);
                resp.sendRedirect(req.getContextPath() + "/posts.do");
            } else {
                req.setAttribute("error", "Не верный пароль");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("error", "Пользователя не существует");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}