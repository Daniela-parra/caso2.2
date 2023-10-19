import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Opcion1 {
    
    private int NF;
    private int NC1;
    private int NC2;
    private int NR;
    private int TP;
    


    //Un mapa el cual representa que pagina le fue a signada a la matriz A, B y C
    private HashMap <String, Integer> paginaAsignada;
    String MatrizL;
    
    public Opcion1(int TP, int NF,int NC1, int NC2){
        this.NF = NF;
        this.NC1 = NC1;
        this.NC2 = NC2;
        this.NR = (2*NC1+1)*NC2*NF;
        this.TP = TP;
        this.paginaAsignada = new HashMap<String, Integer>();
    }

    public void generarReferencias() throws IOException
    {
        FileWriter csvWriter = new FileWriter("referencias");
        csvWriter.append("NF:" + NF + "\n");
        csvWriter.append("NC1:" + NC1 + "\n");
        csvWriter.append("NC2:" + NC2 + "\n");
        csvWriter.append("TP:" + TP + "\n");
        csvWriter.append("NR:" + NR + "\n");
        csvWriter.append("NP:" + (int)Math.ceil((NF*NC1+NC1*NC2+NF*NC2)*4/TP) + "\n");


        int elementosPorPagina = TP/4;
        int numPaginas = NR/elementosPorPagina;

        int paginasPorMatriz = numPaginas/3;
        int desplazamiento=0, pagina=0, i=1, fila=0, columna=0;
        
        while(i<= NR){
            

                if(i%3 == 1)
                {
                    MatrizL="A";
                }
                else if(i%3==2)
                {
                    MatrizL="B";
                }
                else
                {
                    MatrizL="C";
                }

                if(i<=3)
                {
                    paginaAsignada.put(MatrizL, pagina);
                }
            
        
            csvWriter.append("[" + MatrizL + "-" + fila + "-"+ columna +"]" + "," + paginaAsignada.get(MatrizL) + ","+ desplazamiento +  "\n");
            
            pagina+=paginasPorMatriz;

            if(i % 3 == 0)
            {
                columna++;
                if(columna == NC1)
                {
                    columna=0;
                    fila++;
                }
                pagina=0;
                desplazamiento+= 4;
            }

            if(desplazamiento== TP)
            {
                desplazamiento=0;

                paginaAsignada.put("A",paginaAsignada.get("A")+1);

                paginaAsignada.put("B",paginaAsignada.get("B")+1);
                
                paginaAsignada.put("C",paginaAsignada.get("C")+1);

            }

            i++;
        }
        csvWriter.flush();
        csvWriter.close();
    }
}
