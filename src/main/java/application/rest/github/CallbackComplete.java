package application.rest.github;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@WebServlet("/callback/*")
public class CallbackComplete extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      String pathInfo = request.getPathInfo();
      String[] pathParts = pathInfo.split("/");
      if (pathParts.length > 1 && pathParts[1].equals("success")) {
        out.println("<h1>Authentication complete</h1>");
        out.println("Now copy this token into your ENV var...");
        out.println("<div>export AUTH_TOKEN="+pathParts[2]);
        out.println("</div>");
      } else {
        out.println("<h1>Authentication failed</h1>");
        out.println("Unlucky!");
      }
    }
}