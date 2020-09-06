package rudaks;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rudaks.serde.AppSerdes;
import rudaks.types.PaymentConfirmation;
import rudaks.types.PaymentRequest;
import rudaks.types.TransactionStatus;


public class KStreamJoinDemo {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, PaymentRequest> KS0 = streamsBuilder.stream(
            AppConfigs.paymentRequestTopicName,
            Consumed.with(AppSerdes.String(), AppSerdes.PaymentRequest())
                .withTimestampExtractor(AppTimestampExtractor.PaymentRequest())
        );

        KStream<String, PaymentConfirmation> KS1 = streamsBuilder.stream(
            AppConfigs.paymentConfirmationTopicName,
            Consumed.with(AppSerdes.String(), AppSerdes.PaymentConfirmation())
                .withTimestampExtractor(AppTimestampExtractor.PaymentConfirmation())
        );

        KS0.join(KS1, (v1, v2) ->
            new TransactionStatus()
                 .withTransactionID(v1.getTransactionID())
                 .withStatus((v1.getOTP().equals(v2.getOTP()) ? "Success" : "Failure")),
                 JoinWindows.of(Duration.ofMinutes(5)),
                 Joined.with(AppSerdes.String(), AppSerdes.PaymentRequest(), AppSerdes.PaymentConfirmation())
        ).print(Printed.toSysOut());


        logger.info("Starting Stream...");
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Streams...");
            streams.close();
        }));

    }
}
