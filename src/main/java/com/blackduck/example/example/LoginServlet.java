package com.blackduck.example.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {}
    }

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean success = false;
        Connection conn = null;
        

        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/user", "root", "root");

            Statement stmt = null;
            PreparedStatement prepStmt = null;            
            ResultSet rs = null;

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            // UNSAFE 
            // String query = "select * from tbluser where username='" + username + "' and password = '" + password + "'";
            // stmt = conn.createStatement();
            // rs = stmt.executeQuery(query);

            // SAFE
            String safeQuery = "select * from tbluser where username=? and password = ?";
            prepStmt = conn.prepareStatement(safeQuery);
            prepStmt.setString(1,username);
            prepStmt.setString(2,password);
            rs = prepStmt.executeQuery();



            if (rs.next()) {
                // Login Successful if match is found
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //stmt.close();
                
                conn.close();
            } catch (Exception e) {}
        }
        if (success) {
            response.sendRedirect("home.html");
        } else {
            response.sendRedirect("login.html?error=1");
        }
    }
}




unsigned int get_terminal_columns(void)
{
  unsigned int width = 0;
  char *colp = curl_getenv("COLUMNS");
  if(colp) {
    curl_off_t num;
    const char *p = colp;
    if(!curlx_str_number(&p, &num, 10000) && (num > 20))
      width = (unsigned int)num;
    curl_free(colp);
  }

  if(!width) {
    int cols = 0;

#ifdef TIOCGSIZE
    struct ttysize ts;
    if(!ioctl(STDIN_FILENO, TIOCGSIZE, &ts))
      cols = ts.ts_cols;
#elif defined(TIOCGWINSZ)
    struct winsize ts;
    if(!ioctl(STDIN_FILENO, TIOCGWINSZ, &ts))
      cols = (int)ts.ws_col;
#elif defined(_WIN32) && !defined(CURL_WINDOWS_UWP)
    {
      HANDLE stderr_hnd = GetStdHandle(STD_ERROR_HANDLE);
      CONSOLE_SCREEN_BUFFER_INFO console_info;

      if((stderr_hnd != INVALID_HANDLE_VALUE) &&
         GetConsoleScreenBufferInfo(stderr_hnd, &console_info)) {
        /*
         * Do not use +1 to get the true screen-width since writing a
         * character at the right edge will cause a line wrap.
         */
        cols = (int)(console_info.srWindow.Right - console_info.srWindow.Left);
      }
    }
#endif /* TIOCGSIZE */
    if(cols >= 0 && cols < 10000)
      width = (unsigned int)cols;
  }
  if(!width)
    width = 79;
  return width; /* 79 for unknown, might also be tiny or enormous */
}