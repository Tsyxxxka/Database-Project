package org.sang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Override
    public String toUserEmailWithCode(String email) throws MessagingException, javax.mail.MessagingException{
        String code = (Math.random()+"").substring(3,9);
        MimeMessage mimemessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimemessage,true,"UTF-8");
        helper.setFrom(adminEmail);
        helper.setTo(email);
        helper.setSubject("[欢迎使用]实验室论文管理系统");
        helper.setText("您好！欢迎加入实验室论文系统！您的验证码是：【"+code+"】");
        mailSender.send(mimemessage);
        return code;
    }


}
