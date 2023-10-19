import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Main {
    
    static Scanner scanner = new Scanner(System.in);
    static Integer TP = 0;
    static Integer NF = 0;
    static Integer NC1 = 0;
    static Integer NC2 = 0;
    
    public static String input(String mensaje) throws IOException
	{
        System.out.print(mensaje + ": ");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			return reader.readLine();
		
    }

    public static void main(String[] args) throws Exception {
        

        System.out.println("Caso 2\n");
        
        System.out.println("1. Opcion 1");
        System.out.println("2. Opcion 2");

        int seleccionUsuario = Integer.parseInt(input("Por favor seleccione una opción"));

        if( seleccionUsuario == 1)
        {
            System.out.println("\n");
            System.out.println("Opcion 1\n");
            System.out.print("Ingrese el tamaño de pagina: ");
            TP = scanner.nextInt();
            System.out.print("Ingrese el numero de filas de la matriz1: ");
            NF = scanner.nextInt();
            System.out.print("Ingrese el numero de columnas de la matriz1: ");
            NC1 = scanner.nextInt();
            System.out.print("Ingrese el numero de columnas de la matriz2: ");
            NC2 = scanner.nextInt();
            Opcion1 opcion1 = new Opcion1(TP, NF, NC1, NC2);
            opcion1.generarReferencias();
            System.out.println("Referencias generadas.");
        }
         else if(seleccionUsuario == 2) {
            System.out.println("\n");
            System.out.println("Opción 2\n");
            System.out.print("Ingrese el nombre del archivo con las referencias (por ejemplo, referencias): ");
            String nombreArchivo = scanner.next();
            System.out.print("Ingrese el número de marcos de página: ");
            int numMarcos = scanner.nextInt();
            if (numMarcos <= 0) {
                System.out.println("El número de marcos de página debe ser un número entero positivo.");
                return;
                }
            PaginacionSimulacion.simularPaginacion(nombreArchivo, numMarcos);
            System.out.println("Simulación completada. Revise los resultados.");
            }
    }
}