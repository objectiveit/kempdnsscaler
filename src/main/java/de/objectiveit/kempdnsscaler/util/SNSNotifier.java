package de.objectiveit.kempdnsscaler.util;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import de.objectiveit.kempdnsscaler.ApplicationException;

public class SNSNotifier {

    private final String topicArn;

    public SNSNotifier(String topicArn) {
        this.topicArn = topicArn;
    }

    /**
     * Publishes message to the topic defined by {@code topicArn}.
     *
     * @param message message to publish
     * @param subject the subject
     * @return the resulted message ID
     * @throws com.amazonaws.services.sns.model.AmazonSNSException in case of SNS related exception
     * @throws de.objectiveit.kempdnsscaler.ApplicationException in case of wrong  format of topic ARN
     */
    public String publishNotification(String message, String subject) {
        Regions region = getRegionFromArn();
        if (region == null) {
            throw new ApplicationException("Wrong format of topic ARN: " + topicArn);
        }
        AmazonSNS snsClient = AmazonSNSClient.builder()
                .withRegion(region)
                .withCredentials(new EnvironmentVariableCredentialsProvider()) // will use Lambda function IAM Role credentials from env
                .build();

        PublishRequest publishRequest = new PublishRequest(this.topicArn, message, subject);
        PublishResult publishResult = snsClient.publish(publishRequest);
        return publishResult.getMessageId();
    }

    private Regions getRegionFromArn() {
        try {
            String[] parts = this.topicArn.split(":");
            String region = parts[3];
            return Regions.fromName(region);
        } catch (Throwable t) { //incorrect format of the topic ARN
            return null;
        }
    }

}
