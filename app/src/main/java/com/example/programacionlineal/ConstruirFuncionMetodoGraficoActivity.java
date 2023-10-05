package com.example.programacionlineal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ConstruirFuncionMetodoGraficoActivity extends AppCompatActivity {

    RelativeLayout layout;

    short num_Rest;
    private static EditText x1FnObj, x2FnObj, x1R1, x2R2, restR1;
    ArrayList<String> signos;
    LinearLayout restricciones;
    private static ArrayList<View> restriccionesAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construir_funcion);
        init();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.añadirBtn:
                LayoutInflater layoutInflater = getLayoutInflater();
                View view1 = layoutInflater.inflate(R.layout.restriccion_rel_layout, null);

                LinearLayout ll = view1.findViewById(R.id.layoutConElSpinner);

                TextView textView = view1.findViewById(R.id.numRestriccion);
                textView.setText(textView.getText().toString() + " " + num_Rest);

                restricciones.addView(view1);
                restriccionesAL.add(view1);

                num_Rest++;
                break;

            case R.id.borrarBtn:
                if (!restriccionesAL.isEmpty()) {
                    restricciones.removeView(restriccionesAL.get(restriccionesAL.size() - 1));
                    restriccionesAL.remove(restriccionesAL.size() - 1);
                    num_Rest--;
                    break;
                }
            case R.id.resolverBtn:

                try {
                    getFnObj();
                    getX1();
                    getX2();
                    getRests();
                    startActivity(new Intent(this, MetodoGraficoActivity.class));
                } catch (NumberFormatException e){
                    Snackbar snackbar = Snackbar.make(layout, "Llena todos los campos", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } catch (Exception e){
                    Snackbar snackbar = Snackbar.make(layout, "Error inesperado", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }


                break;
            default:
                break;
        }
    }

    public static double[] getFnObj() {

        double[] arreglo = new double[2];
        double x1 = Double.parseDouble(x1FnObj.getText().toString());
        double x2 = Double.parseDouble(x2FnObj.getText().toString());

        arreglo[0] = x1;
        arreglo[1] = x2;

        return arreglo;
    }

    public static ArrayList<Double> getX1() {
        ArrayList<Double> x1s = new ArrayList<>();
        double x1Restriccion1 = Double.parseDouble(x1R1.getText().toString());
        x1s.add(x1Restriccion1);

        for (int i = 0; i < restriccionesAL.size(); i++) {
            EditText eX1 = restriccionesAL.get(i).findViewById(R.id.x1);
            double x1 = Double.parseDouble(eX1.getText().toString());
            x1s.add(x1);
        }
        return x1s;
    }

    public static ArrayList<Double> getX2() {
        ArrayList<Double> x2s = new ArrayList<>();
        double x2Restriccion1 = Double.parseDouble(x2R2.getText().toString());
        x2s.add(x2Restriccion1);

        for (int i = 0; i < restriccionesAL.size(); i++) {
            EditText eX2 = restriccionesAL.get(i).findViewById(R.id.x2);
            double x2 = Double.parseDouble(eX2.getText().toString());
            x2s.add(x2);
        }
        return x2s;
    }

    public static ArrayList<Double> getRests() {
        ArrayList<Double> rests = new ArrayList<>(); //los numeros a los que están igualadas o desigualadas las restricciones
        double rest1 = Double.parseDouble(restR1.getText().toString());
        rests.add(rest1);

        for (int i = 0; i < restriccionesAL.size(); i++) {
            EditText eRest = restriccionesAL.get(i).findViewById(R.id.rest);
            double rest = Double.parseDouble(eRest.getText().toString());
            rests.add(rest);
        }
        return rests;
    }

    private void init() {
        layout = findViewById(R.id.layout);
        x1FnObj = findViewById(R.id.x1FnObj);
        x2FnObj = findViewById(R.id.x2FnObj);
        x1R1 = findViewById(R.id.restriccion1X1);
        x2R2 = findViewById(R.id.restriccion1X2);
        restR1 = findViewById(R.id.rest1);
        restricciones = findViewById(R.id.variablesDeRestriccionLL);

        restriccionesAL = new ArrayList<View>();
        signos = new ArrayList<>();

        num_Rest = 2;
    }
}