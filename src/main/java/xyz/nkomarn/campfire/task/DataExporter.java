package xyz.nkomarn.campfire.task;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import xyz.nkomarn.kerosene.Kerosene;

import java.util.Objects;

/**
 * A running task to export custom data to Prometheus for monitoring.
 */
public class DataExporter implements Runnable {

    private final Gauge economyTotal;

    public DataExporter() {
        this.economyTotal = Gauge.build()
                .name("economy_total")
                .help("Total money, in dollars, in the economy.")
                .register();
    }

    public void shutdown() {
        CollectorRegistry.defaultRegistry.unregister(economyTotal);
    }

    @Override
    public void run() {
        economyTotal.set(Kerosene.getEssentials().getUserMap().getAllUniqueUsers().parallelStream()
                .map(uuid -> Kerosene.getEssentials().getUser(uuid))
                .filter(Objects::nonNull)
                .mapToDouble(user -> user.getMoney().doubleValue())
                .sum());
    }
}
