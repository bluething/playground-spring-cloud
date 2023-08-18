package io.github.bluething.playground.spring.cloud.aws_sqs_listener;

import io.awspring.cloud.autoconfigure.sqs.SqsProperties;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;

@SpringBootApplication
public class AwsSqsListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsSqsListenerApplication.class, args);
    }

    @SqsListener(value = "tempTest.fifo", maxConcurrentMessages = "10", maxMessagesPerPoll = "5", pollTimeoutSeconds = "10")
    public void listen(String message) {
        System.out.println("Receive " + message);
        //throw new IllegalArgumentException("Make the message back to the sqs");
    }

    @Autowired
    SqsAsyncClient sqsAsyncClient;

    @Bean
    ApplicationRunner runner() {

        return args -> {
            SqsTemplate template = SqsTemplate.newTemplate(sqsAsyncClient);
            for (int i = 0; i < 9; i++) {
                String payload = "myPayload" + i;
                String groupId = "myGroupId" + i;
                String deduplicationId = "myGroupDeduplicationId" + i;
                template.send(to -> to.queue("tempTest.fifo")
                        .payload(payload)
                        .messageGroupId(groupId)
                        .messageDeduplicationId(deduplicationId)
                );
                System.out.println("Send " + payload);
            }
        };
    }

    @Import(SqsBootstrapConfiguration.class)
    @Configuration
    public static class SQSConfiguration {

        @Bean
        public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
            return SqsMessageListenerContainerFactory
                    .builder()
                    .configure(options -> options
                            .acknowledgementMode(AcknowledgementMode.ON_SUCCESS)
                            .acknowledgementInterval(Duration.ofSeconds(1))
                            .acknowledgementThreshold(5)
                            .acknowledgementOrdering(AcknowledgementOrdering.ORDERED)
                    )
                    .sqsAsyncClient(sqsAsyncClient())
                    .build();
        }

        @Bean
        public SqsAsyncClient sqsAsyncClient() {
            return SqsAsyncClient.builder().build();
        }

        @Bean
        public SqsProperties.Listener listener() {
            return new SqsProperties.Listener();
        }

    }

}
