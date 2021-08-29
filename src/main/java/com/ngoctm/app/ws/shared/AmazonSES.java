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

    final String HTML_BODY = "<h1> Verify email </h1>" +
            "<a href=http://localhost:8082/users/email.html?token=$token> Click</a>";

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

}
