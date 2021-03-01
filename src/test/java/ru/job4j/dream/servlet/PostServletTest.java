package ru.job4j.dream.servlet;

import org.junit.Test;
import ru.job4j.dream.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
}
