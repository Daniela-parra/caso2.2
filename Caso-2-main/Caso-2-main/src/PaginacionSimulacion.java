import java.util.*;
import java.util.concurrent.locks.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
                        memoriaReal.get(i).age |= 0x80000000;
                        break;
                    } else if (memoriaReal.get(i) == null && marcoLibre == -1) {
                        marcoLibre = i;
                    }
                } finally {
                    locks.get(i).unlock();
                }
            }

            if (!paginaEnMemoria) {
                numFallasPagina++;
                

                if (marcoLibre != -1) {
                    locks.get(marcoLibre).lock();
                    try {
                        memoriaReal.set(marcoLibre, new Pagina(paginaId));
                    } finally {
                        locks.get(marcoLibre).unlock();
                    }
                } else {
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
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Número total de fallas de página: " + numFallasPagina);
    }
}

public class PaginacionSimulacion {

    public static void simularPaginacion(String nombreArchivo, int numMarcos) {
        List<Integer> referencias = cargarReferenciasDeArchivo(nombreArchivo);

        if (referencias == null || referencias.isEmpty()) {
            System.err.println("No se pudieron cargar las referencias del archivo.");
            return;
        }

        int pageSize = 4; // Puede ser dinámico o configurado según sea necesario

        List<Pagina> memoriaReal = new ArrayList<>(Collections.nCopies(numMarcos, null));
        List<ReentrantLock> locks = new ArrayList<>();
        for (int i = 0; i < numMarcos; i++) {
            locks.add(new ReentrantLock());
        }

        Thread procesoThread = new Thread(new ProcesoThread(numMarcos, pageSize, referencias, memoriaReal, locks));
        procesoThread.start();
    }

    private static List<Integer> cargarReferenciasDeArchivo(String nombreArchivo) {
        List<Integer> referencias = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.contains(",")) continue; // Ignorar líneas sin referencias de página
                String[] partes = linea.split(",");
                referencias.add(Integer.parseInt(partes[2].trim())); 
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return null;
        }
        return referencias;
    }

    public static void main(String[] args) {
        // Aquí deberías manejar los argumentos y errores potenciales
        String nombreArchivo = args[0];
        int numMarcos = Integer.parseInt(args[1]);
        simularPaginacion(nombreArchivo, numMarcos);
    }
}