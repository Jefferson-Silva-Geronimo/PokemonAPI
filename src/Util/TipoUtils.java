package Util;

public class TipoUtils {

    public static double getMultiplicador(String tipoAtaque, String tipoDefensor) {

        tipoAtaque = tipoAtaque.toLowerCase();
        tipoDefensor = tipoDefensor.toLowerCase();

        // vantagens principais
        if (tipoAtaque.equals("water") && tipoDefensor.equals("fire")) return 2.0;
        if (tipoAtaque.equals("fire") && tipoDefensor.equals("grass")) return 2.0;
        if (tipoAtaque.equals("grass") && tipoDefensor.equals("water")) return 2.0;
        if (tipoAtaque.equals("electric") && tipoDefensor.equals("water")) return 2.0;
        if (tipoAtaque.equals("rock") && tipoDefensor.equals("flying")) return 2.0;
        if (tipoAtaque.equals("fighting") && tipoDefensor.equals("normal")) return 2.0;


        // desvantagens
        if (tipoAtaque.equals("fire") && tipoDefensor.equals("water")) return 0.5;
        if (tipoAtaque.equals("water") && tipoDefensor.equals("grass")) return 0.5;
        if (tipoAtaque.equals("grass") && tipoDefensor.equals("fire")) return 0.5;

        return 1.0;
    }
}