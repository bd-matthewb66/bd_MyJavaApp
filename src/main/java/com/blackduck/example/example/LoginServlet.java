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
