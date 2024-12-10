package AlgoritmoGenetico;

import java.io.Serializable;
import java.util.Random;

public class Individuo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double beta0;
    private double beta1;
    private double aptitud; 

    public Individuo(double beta0, double beta1) {
        this.beta0 = beta0;
        this.beta1 = beta1;
    }

    public double getBeta0() {
        return beta0;
    }

    public double getBeta1() {
        return beta1;
    }

    public double getAptitud() {
        return aptitud;
    }

    //Calculamos r^2
    public void evaluarAptitud(Data data) {
        double sumaY = 0;
        for (double valorY : data.y) {
            sumaY += valorY;
        }
        double meanY = sumaY / data.y.length;

        double ssTotal = 0, ssResidual = 0;
        for (int i = 0; i < data.x.length; i++) {
            double prediction = beta0 + beta1 * data.x[i];
            ssTotal += (data.y[i] - meanY) * (data.y[i] - meanY);
            ssResidual += (data.y[i] - prediction) * (data.y[i] - prediction);
        }

        if (ssTotal != 0) {
            this.aptitud = 1 - (ssResidual / ssTotal);
        } else {
            this.aptitud = 0; 
        }
    }
    
    
    public void mutar(Random rand) {
        beta0 += rand.nextGaussian();
        beta1 += rand.nextGaussian();
    }
}
