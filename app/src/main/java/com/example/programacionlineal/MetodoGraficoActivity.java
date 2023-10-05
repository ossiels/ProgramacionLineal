package com.example.programacionlineal;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

//TODO que grafique si solo hay limitlines
//TODO prolongar las lineas

public class MetodoGraficoActivity extends AppCompatActivity {

    private LineChart chart1;
    private int[] colores;
    private byte contadorColor;
    private int colorPuntosDarkMode;
    private int colorPuntosLightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_grafico);

        init();
        metodo();
    }

    private void init() {
        chart1 = findViewById(R.id.chart1);
        colores = new int[]{Color.rgb(213, 123, 123), Color.BLUE, Color.rgb(186, 85, 211), Color.rgb(255, 140, 0), Color.rgb(80, 200, 120), Color.rgb(255, 215, 0)};
        contadorColor = 0;
        colorPuntosDarkMode = Color.rgb(173, 216, 230);
        colorPuntosLightMode = Color.rgb(9, 20, 60);
    }

    private void metodo() {
        ArrayList<Double> x1s = ConstruirFuncionMetodoGraficoActivity.getX1();
        ArrayList<Double> x2s = ConstruirFuncionMetodoGraficoActivity.getX2();
        ArrayList<Double> rests = ConstruirFuncionMetodoGraficoActivity.getRests();

        for (int i = 0; i < x1s.size(); i++) {

            if (x1s.get(i) == 0 || x2s.get(i) == 0) {
                if (x1s.get(i) == 0) {
                    graficarLimitLine(true, rests.get(i), x2s.get(i), i, chart1);
                } else {
                    graficarLimitLine(false, rests.get(i), x1s.get(i), i, chart1);
                }
            } else {
                //convierte los arrays a "puntos XY", o sea, las coordenadas en (x1,0) y (0,x2) para trazar una línea de cada restricción
                x2s.set(i, haciendoX1_0(x2s.get(i), rests.get(i)));
                x1s.set(i, haciendoX2_0(x1s.get(i), rests.get(i)));
            }
        }

        double[][] paresX1X2 = new double[2][x1s.size()];
        for (int i = 0; i < paresX1X2[0].length; i++) {
            paresX1X2[0][i] = x1s.get(i);
        }
        for (int i = 0; i < paresX1X2[1].length; i++) {
            paresX1X2[1][i] = x2s.get(i);
        }
        ArrayList<ILineDataSet> dataSets = graficarLineas(paresX1X2);

        for (int i = 0; i < dibujarIntersecciones().size(); i++) {
            dataSets.add(dibujarIntersecciones().get(i));
        }

        double[] recipiente = encontrarPuntoOptimo();
        List<Entry> puntoOptimoList = new ArrayList<>();
        puntoOptimoList.add(new Entry((float) recipiente[0], (float) recipiente[1]));

        LineDataSet puntoOptimo = new LineDataSet(puntoOptimoList, "Punto Óptimo");
        puntoOptimo.setCircleColor(Color.RED);
        puntoOptimo.setDrawCircleHole(false);
        puntoOptimo.setColor(Color.RED);
        puntoOptimo.setDrawValues(false);
        dataSets.add(puntoOptimo);

        LineData data = new LineData(dataSets);

        chart1.setData(data);
        formatearGrafico(chart1);

        mostrarSolucion(recipiente);
    }

    private void mostrarSolucion(double[] solucion) {
        TextView txtView = findViewById(R.id.textViewSolucion);

        String[] valores = new String[3];

        //Si el número es entero, le quita el .0
        for (int i = 0; i < solucion.length; i++) {
            if ((int) solucion[i] - solucion[i] == 0) {
                valores[i] = String.valueOf((int) solucion[i]);
            } else {
                valores[i] = String.valueOf(solucion[i]);
            }
        }

        txtView.setText("X1 = " + valores[0] +
                        "\nX2 = " + valores[1] +
                        "\nMax Z = " + valores[2]);
    }

    private void graficarLimitLine(boolean flag, double valor, double division, int numRestriccion, LineChart chart) {

        LimitLine ll;
        String restriccion = "R" + (numRestriccion + 1);
        ll = new LimitLine((float) (valor / division), restriccion);
        ll.setLineColor(colores[contadorColor]);

        if (flag) {
            chart.getAxisLeft().addLimitLine(ll);
        } else {
            chart.getXAxis().addLimitLine(ll);
        }

        ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
            ll.setTextColor(Color.WHITE);

        chart.invalidate();
        contadorColor++;

        if (contadorColor >= colores.length) contadorColor = 0;
    }

    ArrayList<ILineDataSet> graficarLineas(double[][] puntos) {
        ArrayList<ILineDataSet> dataSets = añadirLineDatas(arrayDeListas(puntos));
        return dataSets;
    }

    ArrayList<ILineDataSet> añadirLineDatas(List[] listas) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        int contador = 1;

        for (List<Entry> l : listas) {
            String numRestriccion = "R" + contador;

            LineDataSet dataSet = new LineDataSet(l, numRestriccion);
            dataSet.setColor(colores[contadorColor]);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircleHole(false);

            if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                dataSet.setCircleColor(colorPuntosDarkMode);
            else
                dataSet.setCircleColor(colorPuntosLightMode);

            dataSets.add(dataSet);
            contador++;

            contadorColor++;
            if (contadorColor >= colores.length) contadorColor = 0;
        }

        return dataSets;
    }

    private List[] arrayDeListas(double[][] puntos) {
        double[] puntosX1 = puntos[0];
        double[] puntosX2 = puntos[1];

        List[] listas = new List[puntosX1.length];

        //hace un array de listas
        for (int i = 0; i < puntosX1.length; i++) {
            if (puntosX1[i] != 0 && puntosX2[i] != 0)
                listas[i] = crearListas((float) puntosX1[i], (float) puntosX2[i]);
        }
        return listas;
    }

    private List<Entry> crearListas(float x1, float x2) {
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(x1, 0));
        entries.add(new Entry(0, x2));

        return entries;
    }

    private ArrayList<ILineDataSet> dibujarIntersecciones() {
        return añadirPuntos(arrayDePuntos());
    }

    private ArrayList<ILineDataSet> añadirPuntos(List[] listas) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        for (List<Entry> l : listas) {
            LineDataSet dataSet = new LineDataSet(l, null);

            if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                dataSet.setCircleColor(colorPuntosDarkMode);
            else
                dataSet.setCircleColor(colorPuntosLightMode);

            dataSet.setDrawValues(false);
            dataSet.setColor(Color.TRANSPARENT);
            dataSet.setDrawCircleHole(false);
            dataSets.add(dataSet);
        }
        return dataSets;
    }

    private List[] arrayDePuntos() {

        ArrayList<Double> x1s = ConstruirFuncionMetodoGraficoActivity.getX1();
        ArrayList<Double> x2s = ConstruirFuncionMetodoGraficoActivity.getX2();
        ArrayList<Double> rests = ConstruirFuncionMetodoGraficoActivity.getRests();
        int contador = 0;
        List[] arreglo;
        for (int i = 0; i < ConstruirFuncionMetodoGraficoActivity.getX1().size(); i++) {
            for (int j = i; j < ConstruirFuncionMetodoGraficoActivity.getX1().size(); j++) {
                if (!sonParalelas(x1s.get(i), x2s.get(i), rests.get(i), x1s.get(j), x2s.get(j), rests.get(j)))
                    contador++;
            }
        }

        arreglo = new List[contador];
        contador = 0;

        for (int i = 0; i < ConstruirFuncionMetodoGraficoActivity.getX1().size(); i++) {
            for (int j = i; j < ConstruirFuncionMetodoGraficoActivity.getX1().size(); j++) {
                if (!sonParalelas(x1s.get(i), x2s.get(i), rests.get(i), x1s.get(j), x2s.get(j), rests.get(j))) {
                    arreglo[contador] = encontrarInterseccion(x1s.get(i), x2s.get(i), rests.get(i), x1s.get(j), x2s.get(j), rests.get(j));
                    contador++;
                }
            }
        }

        return arreglo;
    }

    private List<Entry> encontrarInterseccion(double x1, double x2, double rest, double x1_2, double x2_2, double rest_2) {

        List<Entry> entries = new ArrayList<>();

        //ya no supe que nombres ponerles
        double x, y, recipiente;

        recipiente = x1 / x1_2;
        y = (rest - rest_2 * recipiente) / (x2 - x2_2 * recipiente);
        x = (rest - y * x2) / x1;
        entries.add(new Entry((float) x, (float) y));

        return entries;
    }

    private boolean sonParalelas(double x1, double x2, double rest, double x1_2, double x2_2, double rest_2) {
        return (x1 / x1_2 == x2 / x2_2) && (x2 / x2_2 == rest / rest_2);
    }

    private void formatearGrafico(LineChart chart) {
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setDragEnabled(true);
        chart.getXAxis().setAxisMinimum(-0.01f); //LMAO
        chart.setDescription(null);
        igualarEscala(chart);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            chart.getAxisLeft().setTextColor(Color.WHITE);
            chart.getXAxis().setTextColor(Color.WHITE);
            chart.getLegend().setTextColor(Color.WHITE);
        } else {
            chart.getAxisLeft().setTextColor(Color.BLACK);
            chart.getXAxis().setTextColor(Color.BLACK);
            chart.getLegend().setTextColor(Color.BLACK);
        }
        chart.invalidate();
    }

    //para que la escala de los dos ejes coincida
    private void igualarEscala(LineChart chart) {
        if (chart.getYMax() >= chart.getXChartMax()) {
            chart.getAxisLeft().setAxisMaximum(chart.getAxisLeft().getAxisMaximum() + 1);
            chart.getXAxis().setAxisMaximum(chart.getAxisLeft().getAxisMaximum());
        } else {
            chart.getXAxis().setAxisMaximum(chart.getXAxis().getAxisMaximum() + 1);
            chart.getAxisLeft().setAxisMaximum(chart.getXAxis().getAxisMaximum());
        }
    }

    private double haciendoX1_0(double x2, double rest) {
        x2 = rest / x2;
        return x2;
    }

    private double haciendoX2_0(double x1, double rest) {
        x1 = rest / x1;
        return x1;
    }

    //Esta madre lo que hace es que encuentra el punto optimo resolviendo con simplex, porque al chile no se me ocurrió otra forma y no me estan
    //pagando para echarle ganas a pensar en esto y al momento que lo estoy haciendo faltan como 3 días para entregar esta madre. Ayuda
    private static double[] encontrarPuntoOptimo() {

        double[] recipienteFnObj = ConstruirFuncionMetodoGraficoActivity.getFnObj();// :'(
        ArrayList<Double> x1s = ConstruirFuncionMetodoGraficoActivity.getX1();
        ArrayList<Double> x2s = ConstruirFuncionMetodoGraficoActivity.getX2();
        ArrayList<Double> rests = ConstruirFuncionMetodoGraficoActivity.getRests();

        ArrayList<Double> fnObj = new ArrayList<>();
        for (double d : recipienteFnObj) fnObj.add(-d);

        for (int i = 0; i <= x1s.size(); i++) fnObj.add(0.0);

        ArrayList<ArrayList<Double>> recipienteArregloTotal = new ArrayList<>();

        recipienteArregloTotal.add(x1s);
        recipienteArregloTotal.add(x2s);
        recipienteArregloTotal.add(rests);

        recipienteArregloTotal = ordenarCoeficientesPorRestriccion(recipienteArregloTotal);
        recipienteArregloTotal.add(0, fnObj);

        return operarTabla(recipienteArregloTotal);
    }

    private static ArrayList<ArrayList<Double>> ordenarCoeficientesPorRestriccion(ArrayList<ArrayList<Double>> coeficientesOPV) {
        ArrayList<ArrayList<Double>> ordenado = new ArrayList<>();
        ArrayList<ArrayList<Double>> normal = coeficientesOPV;

        for (int i = 0; i < coeficientesOPV.get(0).size(); i++) {
            ArrayList<Double> restriccion = new ArrayList<>();

            for (int j = 0; j < coeficientesOPV.size(); j++)
                restriccion.add(coeficientesOPV.get(j).get(i));

            ordenado.add(restriccion);
        }

        int xd = normal.get(0).size();
        for (int i = 0; i < ordenado.size(); i++) {
            for (int j = 0; j < xd; j++) {
                ordenado.get(i).add(i == j ? 1.0 : 0.0);
            }
        }
        //Pone el número de la restricción hasta el último
        for (int i = 0; i < ordenado.size(); i++) {
            ordenado.get(i).add(ordenado.get(i).get(2));
            ordenado.get(i).remove(2);
        }
        return ordenado;
    }

    private static double[] operarTabla(ArrayList<ArrayList<Double>> al) {
        double[][] tabla = new double[al.size()][al.get(0).size()];

        for (int i = 0; i < tabla.length; i++) {
            for (int j = 0; j < tabla[0].length; j++) {
                tabla[i][j] = al.get(i).get(j);
            }
        }

        ArrayList<Point> puntosPivote = new ArrayList<>();

        while (funcionObjetivoSeaNegativa(tabla[0])) {
            //Encuentra el más negativo de la primera fila
            double min = tabla[0][0];
            int colAux = 0; //Índice de la columna donde se encuentra el número más negativo

            for (int i = 0; i < tabla[0].length; i++) {
                if (tabla[0][i] < min) {
                    min = tabla[0][i];
                    colAux = i;
                }
            }

            Double[] divisiones = new Double[tabla.length];

            //Realiza las divisiones entre el último y el colAux-ésimo número (columna seleccionada) de cada fila
            for (int i = 0; i < tabla.length; i++)
                divisiones[i] = tabla[i][tabla[0].length - 1] / tabla[i][colAux];

            //Encuetra el mayor para asignarlo a la variable menor
            double mayor = divisiones[1];
            int filaAux = 1;
            for (int i = 1; i < divisiones.length; i++) {
                if (divisiones[i] > mayor) {
                    mayor = divisiones[i];
                    filaAux = i;
                }
            }

            //Encuentra el menor cociente
            double menor = mayor;
            for (int i = 1; i < divisiones.length; i++) {
                if (divisiones[i] < menor && divisiones[i] >= 0) {
                    menor = divisiones[i];
                    filaAux = i;
                }
            }

            double puntoPivote = tabla[filaAux][colAux];
            puntosPivote.add(new Point(filaAux, colAux));

            for (int i = 0; i < tabla.length; i++) {
                double recipiente = -(tabla[i][colAux] / tabla[filaAux][colAux]); //Esta madre saca la division entre el punto pivote y el numero en la misma columna que el punto pivote

                for (int j = 0; j < tabla[0].length; j++) {
                    if (i != filaAux) tabla[i][j] = tabla[filaAux][j] * recipiente + tabla[i][j];
                }
            }
            for (int i = 0; i < tabla[0].length; i++)
                tabla[filaAux][i] = tabla[filaAux][i] / puntoPivote;
        }

        double x1 = 0;
        double x2 = 0;
        double z;

        //determinar los valores x1, x2
        for (int i = 0; i < puntosPivote.size(); i++) {
            if (puntosPivote.get(i).y == 0)
                x1 = tabla[puntosPivote.get(i).x][tabla[0].length - 1];
            else
                x2 = tabla[puntosPivote.get(i).x][tabla[0].length - 1];
        }

        //obtener el valor de z
        z = tabla[0][tabla[0].length - 1];
        double[] arraySolucion = {x1, x2, z};

        return arraySolucion;
    }

    private static boolean funcionObjetivoSeaNegativa(double[] fnObj) {
        for (int i = 0; i < fnObj.length; i++) {
            //float numeroxd = (float) fnObj[i];
            if (fnObj[i] < 0) return true;
        }
        return false;
    }

}