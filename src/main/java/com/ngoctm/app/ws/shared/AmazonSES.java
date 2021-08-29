package com.ngoctm.app.ws.shared;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.ngoctm.app.ws.shared.dto.UserDto;

public class AmazonSES {

    final String accessKey = "AKIAQ6JFDBBMXJJY7V5L";

    final String secretKey = "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";

    final String FROM = "ngoccom1998@gmail.com";

    final String SUBJECT = "verify email";

    final String SUBJECT_RESET = "reset password";

    final String HTML_BODY = "<h1> Verify email </h1>" +
            "<a href=http://localhost:8082/users/email?token=$token> Click</a>";

    final String HTML_BODY_RESET = "<h1> Reset password: Token = </h1>" +
            "<p>$token></p>";

    public void sendVerifyEmail(UserDto userDto){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceAsyncClientBuilder
                .standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.US_EAST_1).build();

        String body = HTML_BODY.replace("$token", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(body)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);
        System.out.println("Send email");

    }

    public void sendResetPassword(String email, String token, String firstNme){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceAsyncClientBuilder
                .standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.US_EAST_1).build();

        String body = HTML_BODY_RESET.replace("$token", token);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(body)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT_RESET)))
                .withSource(FROM);

        client.sendEmail(request);
        System.out.println("Send email");

    }

}
