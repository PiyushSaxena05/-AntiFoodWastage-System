package pack;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

@WebServlet("/Serv")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class FoodLeft extends HttpServlet {

    protected void doGet(HttpServletRequest rq, HttpServletResponse res) throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        try {
            Part p = rq.getPart("t4");
            String filename = p.getSubmittedFileName();

            String filePath = getServletContext().getRealPath("") + "\\" + "SaveData" + "\\" + rq.getParameter("t1");

            File f = new File(filePath);
            if (!f.exists()) {
                f.mkdirs(); 
            }

            String path = filePath + "\\" + filename;
            p.write(path);

            out.println("file uploaded<br>");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/food", "root", "");
            PreparedStatement ps = con.prepareStatement(
                "insert into wastage(name,notice,mobno,img) values(?,?,?,?)"
            );
            
        
            String dbPhotoPath = "SaveData/" + rq.getParameter("t1") + "/" + filename;

            

            ps.setString(1, rq.getParameter("t1"));
            ps.setString(2, rq.getParameter("t2"));
            ps.setLong(3,Long.parseLong(rq.getParameter("t3")));
            ps.setString(4, dbPhotoPath);
           

            int x = ps.executeUpdate();
            if (x > 0) {
                out.println("Data saved in DB");
            } else {
                out.println("Data not inserted");
            }

            con.close();

        } catch (Exception e) {
            out.println("<br>Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    
   
}


