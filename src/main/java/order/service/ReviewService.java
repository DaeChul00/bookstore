package order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import order.repository.ReviewDAOH2;

@Service // 스프링이 서비스 빈으로 등록하도록 어노테이션 추가
public class ReviewService {

    @Autowired
    @Qualifier("orderReviewDao")
    private ReviewDAOH2 reviewDao; // DAO 주입

    public boolean addReview(int bookId, String memberId, int rating, String content) {
        // DB에 데이터 정적 삽입 후 성공 여부(1 이상인지)를 반환
        int result = reviewDao.insertReview(bookId, memberId, rating, content);
        return result > 0;
    }
}