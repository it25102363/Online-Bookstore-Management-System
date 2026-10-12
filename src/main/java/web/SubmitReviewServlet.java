package web;

import util.ReviewFileUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/submitReview")
public class SubmitReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String bookId = request.getParameter("bookId");
        String ratingText = request.getParameter("rating");
        String comment = request.getParameter("comment");

        try {
            int rating = Integer.parseInt(ratingText);

            if (bookId == null || bookId.trim().isEmpty()
                    || rating < 1 || rating > 5
                    || comment == null || comment.trim().isEmpty()) {
                response.sendError(400, "Please enter valid review details.");
                return;
            }

            ReviewFileUtil.add(
                    bookId.trim(),
                    "guest",
                    rating,
                    comment.trim()
            );

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println(
                    "<h2>Review submitted successfully!</h2>"
            );

        } catch (NumberFormatException e) {
            response.sendError(400, "Rating must be between 1 and 5.");
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
        }
    }
}