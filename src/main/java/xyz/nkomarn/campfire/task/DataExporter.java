package xyz.nkomarn.campfire.task;


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
