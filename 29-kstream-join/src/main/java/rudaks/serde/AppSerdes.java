package rudaks.serde;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import rudaks.types.PaymentConfirmation;
import rudaks.types.PaymentRequest;

public class AppSerdes extends Serdes {

    static final class PaymentRequestSerde extends WrapperSerde<PaymentRequest> {
        PaymentRequestSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<PaymentRequest> PaymentRequest() {
        PaymentRequestSerde serde = new PaymentRequestSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, PaymentRequest.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

    static final class PaymentConfirmationSerde extends WrapperSerde<PaymentConfirmation> {
        PaymentConfirmationSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<PaymentConfirmation> PaymentConfirmation() {
        PaymentConfirmationSerde serde = new PaymentConfirmationSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, PaymentConfirmation.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

}
