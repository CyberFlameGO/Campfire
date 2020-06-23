package xyz.nkomarn.Campfire.task;

import com.earth2me.essentials.User;
import com.google.common.util.concurrent.AtomicDouble;
import io.prometheus.client.Gauge;
import xyz.nkomarn.Kerosene.Kerosene;

/**
 * A running task to export custom data to Prometheus for monitoring.
 */
public class DataExporter implements Runnable {
    /*private static final Gauge economy = Gauge.build()
            .name("economy_total")
            .help("Total money, in dollars, in the economy.")
            .register();*/

    @Override
    public void run() {
        /*AtomicDouble total = new AtomicDouble();
        Kerosene.getEssentials().getUserMap().getAllUniqueUsers().forEach(uuid -> {
            User user = Kerosene.getEssentials().getUser(uuid);
            if (user != null) {
                total.addAndGet(user.getMoney().doubleValue());
            }
        });
        economy.set(total.get());*/
    }
}
