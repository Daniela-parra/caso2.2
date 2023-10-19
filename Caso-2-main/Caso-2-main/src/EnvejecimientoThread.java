import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

class EnvejecimientoThread implements Runnable {
    private List<Pagina> memoriaReal;
    private List<ReentrantLock> locks;

    public EnvejecimientoThread(List<Pagina> memoriaReal, List<ReentrantLock> locks) {
        this.memoriaReal = memoriaReal;
        this.locks = locks;
    }

    @Override
    public void run() {
        while (true) {
            for (int i = 0; i < memoriaReal.size(); i++) {
                locks.get(i).lock();
                try {
                    if (memoriaReal.get(i) != null) {
                        memoriaReal.get(i).envejecer();
                    }
                } finally {
                    locks.get(i).unlock();
                }
            }

            try {
                Thread.sleep(1); // Dormir durante 1 milisegundo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
