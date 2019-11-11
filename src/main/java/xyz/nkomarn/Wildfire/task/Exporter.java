package xyz.nkomarn.Wildfire.task;

import io.prometheus.client.Gauge;
import xyz.nkomarn.Wildfire.Wildfire;

import java.util.UUID;

public class Exporter implements Runnable {

    private Gauge econonmyTotal = Gauge.build().name("economy_total")
            .help("Total money in economy").create().register();

    @Override
    public void run() {

        // Calculate total money in the economy
        double total = 0;
        for (UUID player : Wildfire.essentials.getUserMap().getAllUniqueUsers()) {
            total += Wildfire.essentials.getUser(player).getMoney().doubleValue();
        }

        // Export total amount
        econonmyTotal.set(total);

    }

}
