package fis_comosalir;

import interfaz.ComoSalirUI;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
//import net.sourceforge.jFuzzyLogic.rule.Rule;
//import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FIS_ComoSalir {
    
    public static void main(String[] args) {

        ComoSalirUI p = new ComoSalirUI();
        p.setVisible(true);

    }

    public String calcularPropina(double clima, double hora, double flujo_vehicular) {
        // Carga el archivo de lenguaje de control difuso 'FCL'
        String fileName = "src/fis_comosalir/FCL_ComoSalir.fcl";
        FIS fis = FIS.load(fileName, true);
        
        // En caso de error
        if (fis == null) {
            System.err.println("No se puede cargar el archivo: '" + fileName + "'");
            return "";
        }
        
        // Establecer las entradas del sistema
        fis.setVariable("clima", clima);
        fis.setVariable("hora", hora);
        fis.setVariable("flujo_vehicular", flujo_vehicular);

        // Inicia el funcionamiento del sistema
        fis.evaluate();

        // Muestra los gráficos de las variables de entrada y salida
        JFuzzyChart.get().chart(fis.getFunctionBlock("prop"));
        
        /*
        // Muestra el conjunto difuso sobre el que se calcula el COG
        Variable tip = fis.getVariable("propina");
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
        */
             
        // Imprime el valor concreto de salida del sistema
        double salida = fis.getVariable("duracion_viaje").getLatestDefuzzifiedValue();
        
        // Muestra cuanto peso tiene la variable de salida en cada CD de salida
        double pertenenciaBaja = fis.getVariable("duracion_viaje").getMembership("poco");
        double pertenenciaMedia = fis.getVariable("duracion_viaje").getMembership("alto");
        double pertenenciaAlta = fis.getVariable("duracion_viaje").getMembership("muy_alto");
        
        String recomendacion = "";
        
        if (pertenenciaBaja >= pertenenciaMedia &&
                pertenenciaBaja >= pertenenciaAlta){
            
            recomendacion = "poco";
        } else if (pertenenciaMedia >= pertenenciaBaja &&
                pertenenciaMedia >= pertenenciaAlta){
            recomendacion = "alto";
        } else if (pertenenciaAlta >= pertenenciaBaja &&
                pertenenciaAlta >= pertenenciaMedia){
            recomendacion = "muy_alto";
        }
        
        // Muestra las reglas activadas y el valor de salida de cada una despues de aplicar las operaciones lógicas
        StringBuilder reglasUsadas = new StringBuilder();
        reglasUsadas.append("Reglas Usadas:\n");
        fis.getFunctionBlock("prop").getFuzzyRuleBlock("No1").getRules().stream().filter(r -> (r.getDegreeOfSupport() > 0)).forEachOrdered(r -> {
            reglasUsadas.append(r.toString()).append("\n");
        });
        
        return ("duracion del viaje: " + String.format("%.1f", salida) +
               "\n\n" + "la duracion del viaje estimada es de: " + recomendacion +
               "\n\n" + reglasUsadas.toString());
    }
    
}
