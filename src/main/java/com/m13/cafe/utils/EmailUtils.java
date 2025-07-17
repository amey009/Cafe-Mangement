package com.m13.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;

//    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
//        SimpleMailMessage message= new SimpleMailMessage();
//        message.setFrom("jay474988@gmail.com");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        if (list !=null && list.size()>1 )
//            message.setCc(getCcArray(list));
//        emailSender.send(message);
//    }

    //fixes code

    public void sendSimpleMessage(String to, String subject, String text, List<String> ccList) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("jay474988@gmail.com");  // Change this to your actual mail sender address
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            if (ccList != null && !ccList.isEmpty()) {
                message.setCc(getCcArray(ccList));
            }

            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String[] getCcArray(List<String> ccList){
        String[] cc=new String[ccList.size()];
        for (int i = 0; i < ccList.size(); i++) {
            cc[i]=ccList.get(i);
        }
        return cc;
    }

    public void forgetMail(String to,String subject, String password) throws MessagingException {
        MimeMessage message=emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(message,true);
        mimeMessageHelper.setFrom("jay474988@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + to + " <br><b>Password: </b> " + password + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
        message.setContent(htmlMsg,"text/html");
        emailSender.send(message);



    }
}
