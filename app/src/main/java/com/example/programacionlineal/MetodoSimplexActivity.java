package com.example.programacionlineal;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MetodoSimplexActivity extends AppCompatActivity {

    LinearLayout.LayoutParams params;
    ConstraintLayout layout;

    private float[][] tabla;
    private ArrayList<ArrayList<Float>> recipiente;
    private ArrayList<Point> puntosPivote;
    LinearLayout tablaLL;
    int contadorTabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_simplex);


        init();

        try {


            igualarACero();
            printTabla(-1, -1); //para que no haya punto pivote
            resolver();


        } catch (Exception e){
            Snackbar snackbar = Snackbar.make(layout, "Error inesperado", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

    }

    //Muestra la función objetivo igualada a cero en una TextView
    private void igualarACero() {
        TextView tv = findViewById(R.id.igualarACero);
        int numVariablesDecision = tabla[0].length - tabla.length;

        tv.setText("Z");
        for (int i = 0; i < numVariablesDecision; i++) {
            tv.setText(tv.getText().toString() + " - " + -tabla[0][i] + "X" + (i + 1));
        }
        tv.setText(tv.getText().toString() + " = 0");
    }

    private void resolver() {
        while (fnObjTieneNegativos()) {
            actualizarTabla();
        }
        findSolution();
    }

    private void findSolution() {
        TableLayout tablaFinal = (TableLayout) tablaLL.getChildAt(tablaLL.getChildCount() - 1);
        TableRow[] filas = new TableRow[tablaFinal.getChildCount() - 1];

        //guarda cada TableRow de la útlima tabla en el array filas
        for (int i = 1; i < tablaFinal.getChildCount(); i++) {
            TableRow tr = (TableRow) tablaFinal.getChildAt(i);
            filas[i - 1] = tr;
        }

        ArrayList<String> soluciones = new ArrayList<>();

        for (TableRow tr : filas) {
            TextView variable = (TextView) tr.getChildAt(0); //la primera TextView de la fila, donde van los nombres de las variables
            TextView valor = (TextView) tr.getChildAt(tr.getChildCount() - 1); //la última TextView, donde van los valores de las variables

            String solucion = variable.getText().toString() + " = " + valor.getText().toString();
            soluciones.add(solucion);
        }

        soluciones.set(0, soluciones.get(0).replace("j-Cj", "*"));

        imprimirSolucion(soluciones);
    }

    private void imprimirSolucion(ArrayList<String> arg) {
        TextView txtViewSolucion = new TextView(this);
        txtViewSolucion.setLayoutParams(params);
        txtViewSolucion.setText("Solución");
        txtViewSolucion.setTextSize(30);

        TextView txtViewValoresVariables = new TextView(this);
        txtViewValoresVariables.setTextSize(20);

        //Separa las variables Xs y las hs
        ArrayList<String> xs = new ArrayList<>();//Lista de variables de decision X
        ArrayList<String> hs = new ArrayList<>();
        for (String s : arg) {
            if (s.contains("X")) xs.add(s);
            if (s.contains("h")) hs.add(s);
        }

        int numVarsDecision = tabla[0].length - tabla.length;
        int numVarsHolgura = tabla.length - 1;

        agregarVariablesFaltantes(xs, numVarsDecision, "X"); //Agrega las variables xs que faltan porque su valor es cero
        agregarVariablesFaltantes(hs, numVarsHolgura, "h"); //Agrega las variables hs que faltan porque su valor es cero

        xs = ordenarVariables(xs, "X");
        hs = ordenarVariables(hs, "h");

        String[][] valoresSolucion = {
                xs.toArray(new String[xs.size()]),
                hs.toArray(new String[hs.size()])
        };

        //Crea el texto final a mostrar
        String solucion = arg.get(0) + "\n";
        for (String[] a : valoresSolucion) {
            for (String s : a)
                solucion = solucion.concat(s + "\n"); //concatena al texto final el valor de cada variable
        }

        txtViewValoresVariables.setText(solucion);

        tablaLL.addView(txtViewSolucion);
        tablaLL.addView(txtViewValoresVariables);
    }

    //Ordena las variables por subíndice, por ejemplo X1, X2, X3... Xn
    private ArrayList<String> ordenarVariables(ArrayList<String> variables, String var) {
        ArrayList<String> ordenado = new ArrayList<>();

        for (int i = 0; i < variables.size(); i++) {
            String variableIesima = var.concat(String.valueOf(i + 1));
            for (int j = 0; j < variables.size(); j++) {
                if (variables.get(j).substring(0,2).contentEquals(variableIesima)) { //checa si la variable es Xi, por ejemplo
                    ordenado.add(variables.get(j));
                    break;
                }
            }
        }
        return ordenado;
    }

    private void agregarVariablesFaltantes(ArrayList<String> variables, int numVars, String letra) {
        for (int i = 1; i <= numVars; i++) {
            boolean existe = false;

            //Revisa si la lista contiene la variable i-ésima
            for (int j = 0; j < variables.size(); j++) {
                if (variables.get(j).contains(letra + i)) {
                    existe = true;
                    break;
                }
            }
            if (!existe) variables.add(letra + i + " = 0");
        }
    }

    private void actualizarTabla() {
        int columnaPivote = columnaPivote();
        int filaPivote = filaPivote(columnaPivote);
        puntosPivote.add(new Point(filaPivote, columnaPivote));

        float puntoPivote = tabla[filaPivote][columnaPivote];

        //Divide toda la fila pivote entre el valor del punto pivote
        for (int i = 0; i < tabla[filaPivote].length; i++) {
            tabla[filaPivote][i] = tabla[filaPivote][i] / puntoPivote;
        }

        //Hace cero los numeros de la columna pivote
        for (int i = 0; i < tabla.length; i++) {
            float numAMultiplicar = -tabla[i][columnaPivote];

            for (int j = 0; j < tabla[0].length; j++)
                if (i != filaPivote)
                    tabla[i][j] = tabla[filaPivote][j] * numAMultiplicar + tabla[i][j];
        }
        printTabla(filaPivote, columnaPivote);
    }

    //Encuentra el número más negativo en la función objetivo
    private int columnaPivote() {
        float menor = tabla[0][0];
        int indice = 0;

        for (int i = 0; i < tabla[0].length; i++)
            if (tabla[0][i] < menor) {
                menor = tabla[0][i];
                indice = i;
            }
        return indice;
    }

    //Encuentra la fila pivote
    private int filaPivote(int columnaPivote) {
        int XBi = tabla[0].length - 1; //última columna
        float[] divisiones = new float[tabla.length - 1];

        //Realiza las divisiones entre los números de la última columna y los de la columna pivote
        for (int i = 1; i < tabla.length; i++) {
            for (int j = 0; j < tabla[0].length; j++)
                divisiones[i - 1] = tabla[i][XBi] / tabla[i][columnaPivote];
        }

        //TODO intentar que truene con esto con puros negativos, probablemente tenga que hacer otro arreglo seleccionando solo los numeros postivos
        //Encuentra el mayor, es importante que primero encuentre el mayor y después el menor, porque este menor debe ser positivo
        int indice = 0;
        float mayor = 0;
        for (int i = 0; i < divisiones.length; i++)
            if (divisiones[i] > mayor) {
                mayor = divisiones[i];
                indice = i;
            }

        float menor = mayor;
        for (int i = 0; i < divisiones.length; i++)
            if (divisiones[i] < menor && divisiones[i] >= 0) {
                menor = divisiones[i];
                indice = i;
            }

        //retorna indice + 1 porque no se tomó en cuenta la primera fila
        return indice + 1;
    }

    private boolean fnObjTieneNegativos() {
        boolean flag = false;
        for (int i = 0; i < tabla[0].length; i++) {
            if (tabla[0][i] < 0) flag = true;
        }
        return flag;
    }


    private void printTabla(int filaPivote, int columnaPivote) {

        printNumTabla();

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setStretchAllColumns(true);

        int filas = tabla.length;
        int columnas = tabla[0].length;
        int numVariables = columnas - filas;
        int contador = 1; //el número de variable para hs

        //Llena la primera fila con los nombres de las variables
        TableRow primeraFila = new TableRow(this);
        for (int i = 0; i <= columnas; i++) {
            TextView tv = new TextView(this);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setBackgroundResource(R.drawable.outer_border);

            if (i != 0) { //si es la primera columna, la celda estará vacía
                if (i <= numVariables)
                    tv.setText("X" + i);
                else {
                    if (i == columnas) {
                        tv.setText("XBi"); //Si es la última columna, la celda dirá XBi
                    } else {
                        tv.setText("h" + contador);
                        contador++;
                    }
                }
            }
            primeraFila.addView(tv);
        }

        tableLayout.addView(primeraFila);

        for (int i = 0; i < filas; i++) {

            TableRow row = new TableRow(this);


            for (int j = 0; j <= columnas; j++) {
                TextView tv = new TextView(this);

                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setBackgroundResource(R.drawable.border);

                if (j == 0) {
                    tv.setBackgroundResource(R.drawable.outer_border);
                    if (i == 0) tv.setText("Zj-Cj"); //Si es la primera columna se pone Zj-Cj
                    else
                        tv.setText(i == filaPivote ? "X" + (columnaPivote + 1) : "h" + i); //Hace cambio de variable si es la fila pivote
                } else {
                    String texto = String.valueOf(tabla[i][j - 1]);
                    tv.setText(texto);
                }


                row.addView(tv);
            }
            tableLayout.addView(row);
        }
        tablaLL.addView(tableLayout);
    }

    private void printNumTabla() {
        TextView numTabla = new TextView(this);
        numTabla.setLayoutParams(params);
        numTabla.setText(contadorTabla == 0 ? "Tabla inicial" : "Actualización de tabla #" + contadorTabla);
        numTabla.setTextSize(30);

        tablaLL.addView(numTabla);
        contadorTabla++;
    }

    private void init() {
        layout = findViewById(R.id.layout);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 100, 0, 0);

        recipiente = AgregarRestriccionesSimplexActivity.getCoeficientes();
        recipienteInit();
        tabla = new float[recipiente.size()][recipiente.get(0).size()];
        tablaInit();
        tablaLL = findViewById(R.id.tablaLL);
        contadorTabla = 0;
        puntosPivote = new ArrayList<>();
    }

    private void recipienteInit() {

        ArrayList<ArrayList<Float>> aux = new ArrayList<>();

        //guarda el arraylist con las columnas y filas intercambiadas en aux
        for (int i = 0; i < recipiente.size(); i++) {
            ArrayList<Float> auxAL = new ArrayList<>();
            for (int j = 0; j < recipiente.get(0).size(); j++) {
                auxAL.add(recipiente.get(i).get(j));
            }
            aux.add(auxAL);
        }

        //añade una matriz identidad
        for (int i = 0; i < aux.size(); i++)
            for (int j = 0; j < aux.size(); j++)
                aux.get(i).add(i == j ? 1f : 0f);

        //agrega la funcion objetivo a aux
        aux.add(0, new ArrayList<>());
        ArrayList<Float> fnObj = ConstruirFuncionSimplexActivity.getCoeficientesFnObj();

        for (int i = 0; i < fnObj.size(); i++)
            aux.get(0).add(-fnObj.get(i));

        //rellena de 0s la primera fila, según el número de restricciones (o sea, de variables de holgura) + 1 (columna Xbi)
        for (int i = 0; i < aux.size(); i++)
            aux.get(0).add(0f);

        //pone el numero que debería ir en XBi hasta el último y lo borra de donde estaba
        for (int i = 0; i < aux.size(); i++) {
            int indice = aux.get(i).size() - aux.size();//se le resta 2, por el índice y por la fila de la funcion objetivo
            aux.get(i).add(aux.get(i).get(indice));
            aux.get(i).remove(indice);
        }
        recipiente = aux;
    }

    //Hace el Arraylist una matriz, pues es más fácil manipular los datos para las operaciones así
    private void tablaInit() {
        for (int i = 0; i < recipiente.size(); i++)
            for (int j = 0; j < recipiente.get(0).size(); j++)
                tabla[i][j] = recipiente.get(i).get(j);
    }
}