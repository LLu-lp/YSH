package com.haowu.shop.service;

import com.haowu.shop.entity.OrderItem;
import com.haowu.shop.entity.Review;
import com.haowu.shop.entity.User;
import com.haowu.shop.mapper.OrderItemRepository;
import com.haowu.shop.mapper.ReviewRepository;
import com.haowu.shop.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    // 提交商品评价
    public Review submitReview(Long userId, Long orderId, Long productId, Integer rating, String content, String images) {
        // 检查订单商品是否存在
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        OrderItem orderItem = orderItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("订单商品不存在"));

        // 检查是否已经评价
        List<Review> existingReviews = reviewRepository.findByOrderId(orderId);
        boolean hasReviewed = existingReviews.stream()
                .anyMatch(review -> review.getProductId().equals(productId));
        if (hasReviewed) {
            throw new RuntimeException("该商品已经评价过");
        }

        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 创建评价
        Review review = new Review();
        review.setUserId(userId);
        review.setUserName(user.getUsername());
        review.setUserAvatar(user.getAvatar());
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);

        // 保存评价
        Review savedReview = reviewRepository.save(review);

        // 更新订单商品的评价状态
        orderItem.setReviewed(1);
        orderItemRepository.save(orderItem);

        return savedReview;
    }

    // 获取商品的评价列表
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    // 获取用户的评价列表
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    // 商家回复评价
    public Review replyReview(Long reviewId, String reply) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));
        
        review.setReply(reply);
        return reviewRepository.save(review);
    }
}
