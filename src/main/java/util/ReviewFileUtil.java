package util;

import model.Review;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class ReviewFileUtil {
    private static final Path FILE =
            Paths.get(System.getProperty("user.home"), "bookstore-data", "reviews.txt");

    private static void ensureFile() throws IOException {
        Files.createDirectories(FILE.getParent());
        if (!Files.exists(FILE)) Files.createFile(FILE);
    }

    public static synchronized List<Review> getAll() throws IOException {
        ensureFile();
        List<Review> list = new ArrayList<>();
        for (String line : Files.readAllLines(FILE)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", 6);
            list.add(new Review(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[5]));
        }
        return list;
    }

    private static void saveAll(List<Review> list) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Review r : list) {
            lines.add(r.getReviewId() + "|" + r.getIsbn() + "|" + r.getUserId() + "|"
                    + r.getRating() + "|" + r.getComment().replace("|", " ") + "|" + r.getDate());
        }
        Files.write(FILE, lines);
    }

    public static synchronized void add(String isbn, String userId, int rating, String comment)
            throws IOException {
        List<Review> list = getAll();
        int max = 0;
        for (Review r : list) {
            max = Math.max(max, Integer.parseInt(r.getReviewId().substring(1)));
        }
        String id = String.format("R%03d", max + 1);
        list.add(new Review(id, isbn, userId, rating, comment, LocalDate.now().toString()));
        saveAll(list);
    }

    public static synchronized Review getById(String id) throws IOException {
        for (Review r : getAll()) if (r.getReviewId().equals(id)) return r;
        return null;
    }

    public static synchronized void update(String id, int rating, String comment)
            throws IOException {
        List<Review> list = getAll();
        for (int i = 0; i < list.size(); i++) {
            Review r = list.get(i);
            if (r.getReviewId().equals(id)) {
                list.set(i, new Review(id, r.getIsbn(), r.getUserId(), rating, comment, r.getDate()));
            }
        }
        saveAll(list);
    }

    public static synchronized void delete(String id) throws IOException {
        List<Review> list = getAll();
        list.removeIf(r -> r.getReviewId().equals(id));
        saveAll(list);
    }
}
