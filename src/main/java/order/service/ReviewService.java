package order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import order.repository.ReviewDAOH2;

@Service
public class ReviewService {

    @Autowired
    @Qualifier("orderReviewDao")
    private ReviewDAOH2 reviewDao;

    public boolean addReview(int bookId, String memberId, int rating, String content) {
        int result = reviewDao.insertReview(bookId, memberId, rating, content);
        return result > 0;
    }
}