package AlgoritmoGenetico;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AgenteInicializarPoblacion extends Agent {
    @Override
    protected void setup() {
        
        System.out.println(getLocalName() + " iniciado. ");

        // Agregar comportamiento para inicializar la población
        addBehaviour(new IniciarPoblacion());
    }

    private class IniciarPoblacion extends OneShotBehaviour {
        @Override
        public void action() {
            int tamPoblacion = 200;
            ArrayList<Individuo> poblacion = inicializarPoblacion(tamPoblacion);

            // Enviar información de la población (simulado aquí)
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Algoritmo Genetico", AID.ISLOCALNAME)); // Nombre del agente receptor
            msg.setConversationId("poblacion-inicial"); 
            try {
                msg.setContentObject(poblacion);
                send(msg);
                System.out.println("Población inicial enviada al Agente AlgoritmoGenetico.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ArrayList<Individuo> inicializarPoblacion(int tam) {
            Random rand = new Random();
            ArrayList<Individuo> poblacionInicial = new ArrayList<>();
            for (int i = 0; i < tam; i++) {
                double beta0 = rand.nextDouble() * 200 - 100; // Valores entre -100 y 100
                double beta1 = rand.nextDouble() * 200 - 100;
                poblacionInicial.add(new Individuo(beta0, beta1));
            }
            return poblacionInicial;
        }
    }


    
}
