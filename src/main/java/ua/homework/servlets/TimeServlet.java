package ua.homework.servlets;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(getServletContext());
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);

    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        Cookie[] cookies = req.getCookies();
        String query = req.getParameter("timezone");
        if (query != null) {
            query = query.replace(" ", "+");
        } else if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    query = cookie.getValue();
                }
            }
        } else {
            query = "UTC";
        }
        resp.addCookie(new Cookie("lastTimezone", query));


        ZoneId zoneId = ZoneId.of("UTC");
        if (query != null && !query.trim().isEmpty()) {
            if (query.startsWith("UTC+") || query.startsWith("UTC-")) {
                int offsetHours = Integer.parseInt(query.substring(3));
                zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(offsetHours));
            } else {
                zoneId = ZoneId.of(query);
            }
        }

        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'xxx");
        String formattedTime = zonedDateTime.format(formatter);


        Context context = new Context();
        context.setVariable("formattedTime", formattedTime);


        String htmlResponse = engine.process("time-template", context);

        resp.getWriter().println(htmlResponse);
    }


}
