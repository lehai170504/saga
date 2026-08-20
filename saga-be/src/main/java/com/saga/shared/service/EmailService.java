package com.saga.shared.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    
    @Value("${spring.mail.username:}")
    private String senderEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendCourseEnrollmentEmail(String toEmail, String className) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (senderEmail != null && !senderEmail.isEmpty()) {
                message.setFrom(senderEmail);
            }
            message.setTo(toEmail);
            message.setSubject("Saga - Bạn đã được thêm vào lớp " + className);
            message.setText("Chào bạn,\n\nBạn đã được giảng viên / admin thêm vào danh sách lớp " + className + 
                            ".\nVui lòng đăng nhập vào hệ thống Saga để kích hoạt tài khoản của bạn (Nếu chưa đăng nhập).\n\nTrân trọng,\nSaga System");
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail + ". Error: " + e.getMessage());
            // Log the error but don't crash the import process
        }
    }
}
