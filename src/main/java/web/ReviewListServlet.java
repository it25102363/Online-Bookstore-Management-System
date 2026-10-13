package web;

import model.Review;
import util.ReviewFileUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/reviews")
public class ReviewListServlet extends HttpServlet {

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>All Reviews</title><style>"
                + "body{font-family:Arial;margin:30px;background:#f4f6f8}"
                + "table{border-collapse:collapse;width:100%;background:#fff}"
                + "th,td{border:1px solid #ccc;padding:8px;text-align:left}"
                + "th{background:#2c3e50;color:#fff}"
                + "a.btn,button{padding:5px 10px;border:none;border-radius:4px;"
                + "color:#fff;cursor:pointer;text-decoration:none;font-size:14px}"
                + "a.edit{background:#2980b9}button{background:#c0392b}"
                + "form{display:inline}"
                + "</style></head><body>");
        out.println("<h1>All Reviews</h1>");
        out.println("<p><a href='review.html'>+ Add new review</a></p>");
        out.println("<table><tr><th>ID</th><th>Book</th><th>User</th>"
                + "<th>Rating</th><th>Comment</th><th>Date</th><th>Actions</th></tr>");

        for (Review r : ReviewFileUtil.getAll()) {
            out.println("<tr><td>" + esc(r.getReviewId()) + "</td>"
                    + "<td>" + esc(r.getIsbn()) + "</td>"
                    + "<td>" + esc(r.getUserId()) + "</td>"
                    + "<td>" + r.getRating() + "</td>"
                    + "<td>" + esc(r.getComment()) + "</td>"
                    + "<td>" + esc(r.getDate()) + "</td>"
                    + "<td><a class='btn edit' href='editReview?id=" + esc(r.getReviewId())
                    + "'>Edit</a> "
                    + "<form method='post' action='deleteReview' "
                    + "onsubmit=\"return confirm('Delete this review?')\">"
                    + "<input type='hidden' name='id' value='" + esc(r.getReviewId()) + "'>"
                    + "<button type='submit'>Delete</button></form></td></tr>");
        }
        out.println("</table></body></html>");
    }
}