package vn.hoidanit.jobhunter.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.repository.JobRepository;

@Service
public class EmailService {
      private final MailSender mailSender;
      private final JavaMailSender javaMailSender;
      private final SpringTemplateEngine templateEngine;
      private final JobRepository jobRepository;

      public EmailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine,
                  JobRepository jobRepository) {
            this.mailSender = mailSender;
            this.javaMailSender = javaMailSender;
            this.templateEngine = templateEngine;
            this.jobRepository = jobRepository;
      }

      // isMultipart dung de check gui kem file
      // isHtml dung de check gui noi dung html
      public void sendEmailSync(String to, String subject, String content, boolean isMultipart,
                  boolean isHtml) {
            // Prepare message using a Spring helper
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            try {
                  MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
                              isMultipart, StandardCharsets.UTF_8.name());
                  message.setTo(to);
                  message.setSubject(subject);
                  message.setText(content, isHtml);
                  this.javaMailSender.send(mimeMessage);
            } catch (MailException | MessagingException e) {
                  System.out.println("ERROR SEND EMAIL: " + e);
            }
      }

      @Async
      public void sendEmailFromTemplateSync(String to, String subject, String templateName, String userName,
                  Object value) {
            Context context = new Context();
            context.setVariable("name", userName);
            // khi them @Async thì sẽ phản hồi ngay lập tức nhưng về phía email cần thời
            // gian thực hiện (tối ưu hóa trải nghiệm người dùng)
            // khi thêm @Async thì sẽ gặp lỗi vì thread mới không chia sẻ data
            // lấy full data trước khi trả về cho template (convert to ResponseEmailJob)

            context.setVariable("jobs", value);
            String content = this.templateEngine.process(templateName, context);
            this.sendEmailSync(to, subject, content, false, true);
      }
}
