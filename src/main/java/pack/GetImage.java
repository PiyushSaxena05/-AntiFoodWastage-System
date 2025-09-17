package pack;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.*;

@WebServlet("/GetImage")
public class GetImage extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("name"); 

        if (name == null || name.trim().isEmpty()) {
            res.setContentType("text/html");
            res.getWriter().println("Name parameter is missing!");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/food", "root", "PIYUSH@111WORD016");

            PreparedStatement ps = con.prepareStatement("SELECT img FROM wastage WHERE name=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String imgPath = rs.getString("img");
                File imgFile;

                if (new File(imgPath).isAbsolute()) {
                    imgFile = new File(imgPath);  
                } else {
                    imgFile = new File(getServletContext().getRealPath("/") + imgPath); 
                }

                if (imgFile.exists()) {
                    res.setContentType("image/jpeg"); 
                    try (FileInputStream fis = new FileInputStream(imgFile);
                         OutputStream os = res.getOutputStream()) {

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                } else {
                    res.setContentType("text/html");
                    res.getWriter().println("Image file not found on server. Path: " + imgFile.getAbsolutePath());
                }
            } else {
                res.setContentType("text/html");
                res.getWriter().println("No image found for employee: " + name);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            res.setContentType("text/html");
            res.getWriter().println("Error: " + e.getMessage());
        }
    }
}
