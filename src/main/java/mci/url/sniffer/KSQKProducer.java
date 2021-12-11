package mci.url.sniffer;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.KsqlObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KSQKProducer {

    private static KSQKProducer ksqkProducer;

    private final ClientOptions options = ClientOptions.create()
            .setHost("167.71.107.99")
            .setPort(8088);

    private final Client client = Client.create(options);
    private final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    Map<String, Object> properties = Collections.singletonMap(
            "auto.offset.reset", "earliest"
    );

    private final LocalQueue localQueue = LocalQueue.getInstance();

    private static final Object LOCK = new Object();

    private KSQKProducer() {
        listenQueue();
    }

    public static KSQKProducer getInstance() {
        synchronized (LOCK) {
            if (ksqkProducer == null) ksqkProducer = new KSQKProducer();
        }
        return ksqkProducer;
    }

    private void listenQueue() {
        boolean running = true;
        List<String> domains = new ArrayList<>();
        while (running) {

            domains.add(localQueue.takeDomain());
            if (domains.size() == 64) {
                pushDomains(domains);
                domains.clear();
            }
//            if (domains.isEmpty()) running = false;//TODO fix it !!!
        }
    }

    private void pushDomains(List<String> domains) {
        List<KsqlObject> rows = new ArrayList<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String formatedTime = sdf1.format(timestamp);
        domains.forEach(domain -> {
            KsqlObject current = new KsqlObject().put("domain", domain).put("timestamp", formatedTime);
            rows.add(current);
        });


        CompletableFuture<Void> result = CompletableFuture.allOf(
                rows.stream()
                        .map(row -> client.insertInto("domainsCount", row))
                        .toArray(CompletableFuture[]::new)
        );
    }
}
