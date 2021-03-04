package ru.job4j.dream.servlet;

import ru.job4j.dream.PsqlStore;
import ru.job4j.dream.model.Candidate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CandidateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        PsqlStore.instOf().saveCandidate(new Candidate(Integer.parseInt(req.getParameter("id")), req.getParameter("name"),Long.parseLong(req.getParameter("photo_id")), Long.parseLong(req.getParameter("city_id"))));
        resp.sendRedirect(req.getContextPath() + "/candidates.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", PsqlStore.instOf().findPhotoForAllCandidate(PsqlStore.instOf().findAllCandidates()));
        req.setAttribute("user", req.getSession().getAttribute("user"));
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }
}
