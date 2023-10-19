
import java.util.*;
import java.util.concurrent.locks.*;

class Pagina {
    int id;
    int age;

    public Pagina(int id) {
        this.id = id;
        this.age = 0;
    }

    public void envejecer() {
        this.age >>= 1; // envejece la página
    }
}

class ProcesoThread implements Runnable {
    private int numMarcos;
    private int pageSize;
    private List<Integer> referencias;
    private List<Pagina> memoriaReal;
    private List<ReentrantLock> locks;

    public ProcesoThread(int numMarcos, int pageSize, List<Integer> referencias, 
                         List<Pagina> memoriaReal, List<ReentrantLock> locks) {
        this.numMarcos = numMarcos;
        this.pageSize = pageSize;
        this.referencias = referencias;
        this.memoriaReal = memoriaReal;
        this.locks = locks;
    }

    @Override
    public void run() {
        int numFallasPagina = 0;

        for (int referencia : referencias) {
            int paginaId = referencia / pageSize;

            boolean paginaEnMemoria = false;
            int marcoLibre = -1;
            for (int i = 0; i < numMarcos; i++) {
                locks.get(i).lock();
                try {
                    if (memoriaReal.get(i) != null && memoriaReal.get(i).id == paginaId) {
                        paginaEnMemoria = true;
                        memoriaReal.get(i).age |= 0x80000000; // Marcar la página como accedida recientemente 
                        break;
                    } else if (memoriaReal.get(i) == null && marcoLibre == -1) {
                        marcoLibre = i; // Encuentrar el primer marco libre
                    }
                } finally {
                    locks.get(i).unlock();
                }
            }

            if (!paginaEnMemoria) {
                numFallasPagina++;
                if (marcoLibre != -1) {// Usar marco  libre (si hay)
                    locks.get(marcoLibre).lock();
                    try {
                        memoriaReal.set(marcoLibre, new Pagina(paginaId));
                    } finally {
                        locks.get(marcoLibre).unlock();
                    }
                } else {//lógica para reemplazar una página existente basada en el algoritmo de envejecimiento
                    int marcoAReemplazar = -1;
                    int minAge = Integer.MAX_VALUE;
                    for (int i = 0; i < numMarcos; i++) {
                        locks.get(i).lock();
                        try {
                            if (memoriaReal.get(i).age < minAge) {
                                minAge = memoriaReal.get(i).age;
                                marcoAReemplazar = i;
                            }
                        } finally {
                            locks.get(i).unlock();
                        }
                    }

                    locks.get(marcoAReemplazar).lock();
                    try {
                        memoriaReal.set(marcoAReemplazar, new Pagina(paginaId));
                    } finally {
                        locks.get(marcoAReemplazar).unlock();
                    }
                }
            }

            try {
                Thread.sleep(2); // Dormir 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Número de fallas de página: " + numFallasPagina);
    }
}
public class PaginacionSimulacion {
    public static void main(String[] args) {
        int numMarcos = 4; 
        int pageSize = 256; 

        List<Integer> referencias = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            referencias.add(random.nextInt(100));
        }

        List<Pagina> memoriaReal = new ArrayList<>(numMarcos);
        for (int i = 0; i < numMarcos; i++) {
            memoriaReal.add(null);
        }

        List<ReentrantLock> locks = new ArrayList<>();
        for (int i = 0; i < numMarcos; i++) {
            locks.add(new ReentrantLock());
        }

        Thread procesoThread = new Thread(new ProcesoThread(numMarcos, pageSize, referencias, memoriaReal, locks));
        Thread envejecimientoThread = new Thread(new EnvejecimientoThread(memoriaReal, locks));

        procesoThread.start();
        envejecimientoThread.start();
    }
}