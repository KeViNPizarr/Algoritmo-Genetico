package AlgoritmoGenetico;

import java.io.Serializable;

public class Data implements Serializable {
    private static final long serialVersionUID = 1L;

    public double[] x;
    public double[] y;

    public Data() {
        this.x = new double[]{1,2,3,4,5,6,7,8,9};//Advertising
        this.y = new double[]{2,4,6,8,10,12,14,16,18};//Sales
    }
}
