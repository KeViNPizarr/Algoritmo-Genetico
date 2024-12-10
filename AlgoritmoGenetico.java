package AlgoritmoGenetico;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.util.ArrayList;
import java.util.Random;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.io.Serializable;
import java.io.IOException;

public class AlgoritmoGenetico extends Agent {

    private double tasaMutacion = 0.05; // Tasa de mutación
    private Random rand = new Random();
    private ArrayList<Individuo> poblacionInicial;

    @Override
    protected void setup() {
        doWait(2000);
        
        System.out.println( getLocalName() + " iniciado.");

        // Agregar comportamiento principal
        addBehaviour(new CalcularAlgoritmoGenetico());
    }


    private double[] resolverAlgoritmoGenetico(Data data, ArrayList<Individuo> poblacion) {
        int tamPoblacion = poblacion.size();
        int limiteGeneraciones = 4000;
        double tasaCruce = 0.95;

        int generacion = 0;

        // Bucle evolutivo
        while (generacion < limiteGeneraciones) {
            ArrayList<Individuo> nuevaPoblacion = new ArrayList<>();

            // Elitismo
            Individuo mejorIndividuo = obtenerMasApto(poblacion);
            nuevaPoblacion.add(mejorIndividuo);

            // Cruce y mutación
            while (nuevaPoblacion.size() < tamPoblacion) {
                Individuo padre1 = ruleta(poblacion);
                Individuo padre2 = ruleta(poblacion);
                ArrayList<Individuo> hijos = cruzar(padre1, padre2, tasaCruce);
                nuevaPoblacion.addAll(hijos);
            }
            mutarPoblacion(nuevaPoblacion);
            evaluarPoblacion(nuevaPoblacion, data);

            // Actualizar la población
            poblacion = nuevaPoblacion;
            generacion++;
        }

        // Obtener el mejor individuo
        Individuo mejor = obtenerMasApto(poblacion);

        double b0 = mejor.getBeta0();
        double b1 = mejor.getBeta1();
        double r2 = mejor.getAptitud();

        return new double[]{b0, b1, r2};
    }


    private void evaluarPoblacion(ArrayList<Individuo> nuevaPoblacion, Data data) {
        for (Individuo individuo : nuevaPoblacion) {
            individuo.evaluarAptitud(data);
        }
    }

    private Individuo ruleta(ArrayList<Individuo> poblacion) {
        double totalAptitud = 0.0;
        for (Individuo ind : poblacion) {
            totalAptitud += ind.getAptitud();
        }

        double umbral = rand.nextDouble() * totalAptitud;

        double acumulado = 0.0;
        for (Individuo individuo : poblacion) {
            acumulado += individuo.getAptitud();
            if (acumulado >= umbral) {
                return individuo;
            }
        }
        return poblacion.get(rand.nextInt(poblacion.size()));
    }

    private ArrayList<Individuo> cruzar(Individuo padre1, Individuo padre2, double tasaCruce) {
        ArrayList<Individuo> descendencia = new ArrayList<>();
        if (rand.nextDouble() < tasaCruce) {
            double beta0 = rand.nextBoolean() ? padre1.getBeta0() : padre2.getBeta0();
            double beta1 = rand.nextBoolean() ? padre1.getBeta1() : padre2.getBeta1();
            descendencia.add(new Individuo(beta0, beta1));
        } else {
            descendencia.add(new Individuo(padre1.getBeta0(), padre1.getBeta1()));
            descendencia.add(new Individuo(padre2.getBeta0(), padre2.getBeta1()));
        }
        return descendencia;
    }

    private void mutarPoblacion(ArrayList<Individuo> poblacion) {
        for (Individuo individuo : poblacion) {
            if (rand.nextDouble() < tasaMutacion) {
                individuo.mutar(rand);
            }
        }
    }

    private Individuo obtenerMasApto(ArrayList<Individuo> poblacion) {
        if (poblacion == null || poblacion.isEmpty()) {
            return null;
        }
        Individuo masApto = poblacion.get(0);
        for (int i = 1; i < poblacion.size(); i++) {
            Individuo actual = poblacion.get(i);
            if (actual.getAptitud() > masApto.getAptitud()) {
                masApto = actual;
            }
        }
        return masApto;
    }


    private class CalcularAlgoritmoGenetico extends OneShotBehaviour  {
        @Override
        public void action() {

            ACLMessage msg = receive();
            if (msg != null && "poblacion-inicial".equals(msg.getConversationId())) {
                try {
                    Serializable content = msg.getContentObject();
                    if (content instanceof ArrayList) {
                        poblacionInicial = (ArrayList<Individuo>) content;
                        System.out.println("Población inicial recibida y procesada en Algoritmo Genetico.");

                        Data data = new Data();

                        double[] resultado = resolverAlgoritmoGenetico(data, poblacionInicial);

                        // Enviar coeficientes (b0, b1, r2) a AgentePredicciones
                        ACLMessage responseMsg = new ACLMessage(ACLMessage.INFORM);
                        responseMsg.addReceiver(new AID("Predicciones", AID.ISLOCALNAME));
                        responseMsg.setConversationId("predicciones");
                        responseMsg.setContentObject(resultado);
                        send(responseMsg);
                        System.out.println("Coeficientes enviados al AgentePredicciones.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            } else if(msg != null) {
                System.out.println("Mensaje no reconocido. ID de conversación: " + msg.getConversationId());
            } else {
                block();
            }        
          myAgent.doDelete();
        }
    }

}
