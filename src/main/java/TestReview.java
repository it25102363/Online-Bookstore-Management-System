import model.Review;
import util.ReviewFileUtil;

public class TestReview {
    public static void main(String[] args) throws Exception {
        ReviewFileUtil.add("978-1", "U001", 5, "Great book");
        ReviewFileUtil.add("978-2", "U002", 4, "Really good book");
        ReviewFileUtil.update("R001", 3, "Updated comment");
        ReviewFileUtil.delete("R002");

        for (Review r : ReviewFileUtil.getAll()) {
            System.out.println(r.getReviewId() + " " + r.getIsbn() + " "
                    + r.getRating() + " " + r.getComment());
        }
    }
}

