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

@WebServlet("/editReview")
public class EditReviewServlet extends HttpServlet {

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Review r = ReviewFileUtil.getById(request.getParameter("id"));
        if (r == null) {
            response.sendError(404, "Review not found");
            return;
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Edit Review</title><style>"
                + "body{font-family:Arial;margin:30px;background:#f4f6f8}"
                + "label{display:block;margin-top:12px}"
                + "input,textarea{padding:6px;width:300px}"
                + "button{margin-top:15px;padding:8px 16px;background:#2980b9;"
                + "color:#fff;border:none;border-radius:4px;cursor:pointer}"
                + "</style></head><body>");
        out.println("<h1>Edit Review " + esc(r.getReviewId()) + "</h1>");
        out.println("<p>Book: " + esc(r.getIsbn()) + " | User: " + esc(r.getUserId()) + "</p>");
        out.println("<form method='post' action='editReview'>");
        out.println("<input type='hidden' name='id' value='" + esc(r.getReviewId()) + "'>");
        out.println("<label>Rating (1-5):</label>"
                + "<input type='number' name='rating' min='1' max='5' required value='"
                + r.getRating() + "'>");
        out.println("<label>Comment:</label>"
                + "<textarea name='comment' rows='4' required>"
                + esc(r.getComment()) + "</textarea>");
        out.println("<button type='submit'>Save changes</button> "
                + "<a href='reviews'>Cancel</a>");
        out.println("</form></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = request.getParameter("id");
        String comment = request.getParameter("comment");
        try {
            int rating = Integer.parseInt(request.getParameter("rating"));
            if (id == null || comment == null || comment.trim().isEmpty()
                    || rating < 1 || rating > 5) {
                response.sendError(400, "Please enter valid review details.");
                return;
            }
            ReviewFileUtil.update(id, rating, comment);
            response.sendRedirect("reviews");
        } catch (NumberFormatException e) {
            response.sendError(400, "Rating must be a number.");
        }
    }
}
