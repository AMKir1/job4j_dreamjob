package ru.job4j.dream.servlet;

import org.junit.Test;
import ru.job4j.dream.PsqlStore;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class PostServletTest {

    @Test
    public void whenAddPost() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn("1");
        when(req.getParameter("name")).thenReturn("Client");
        new PostServlet().doPost(req, resp);
        assertThat(PsqlStore.instOf().findPostById(1).getName(), is("Client"));
    }

    @Test
    public void whenGetPost() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher("posts.jsp")).thenReturn(dispatcher);
        new PostServlet().doGet(req, resp);
        verify(req).getRequestDispatcher("posts.jsp");
        verify(req, times(1)).getSession();
        verify(dispatcher).forward(req, resp);
    }

}
