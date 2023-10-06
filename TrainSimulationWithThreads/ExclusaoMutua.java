public class ExclusaoMutua {
    public volatile int lock = 0;

    public void entrarRC() {
        while (true) {
            // Tenta adquirir o bloqueio
            while (lock != 0); // Loop até que o bloqueio esteja liberado

            // Adquire o bloqueio
            lock = 1;

            // Região crítica
            criticalRegion();

            // Libera o bloqueio
            lock = 0;

            // Região não crítica
            nonCriticalRegion();
        }
    }

    private void criticalRegion() {
        System.out.println(Thread.currentThread().getName() + " Entrando na região crítica");

        // Simule algum processamento na região crítica
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void nonCriticalRegion() {
        // Código da região não crítica
        System.out.println(Thread.currentThread().getName() + " Saindo da região crítica");
        // Simule algum processamento na região não crítica
    }
}