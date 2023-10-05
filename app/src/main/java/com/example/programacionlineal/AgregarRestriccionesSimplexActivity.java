package com.example.programacionlineal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AgregarRestriccionesSimplexActivity extends AppCompatActivity {

    ArrayList<View> restsViews; //Aqui van las views que se inflan, para luego poder quitarlas con el boton

    private RelativeLayout layout;
    private LinearLayout restriccion1LinLayout;
    private LinearLayout restriccionesLL;

    int numVariables;
    int numRestricciones;

    static ArrayList<Spinner> tipoRestriccion; //Si es mayor o igual, igual, o menor o igual

    //Aquí van los números de las restricciones, el número después del spinner, para no mezclarlas con los coeficientes de las variables
    static ArrayList<EditText> restNums;

    //Un array de arraylists donde en cada uno van las EditTexts de los coeficientes de cada variable
    static ArrayList<ArrayList<EditText>> restriccionesAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_restricciones_simplex);

        init();
    }

    private void init() {
        layout = findViewById(R.id.layout);
        tipoRestriccion = new ArrayList<>();
        restNums = new ArrayList<>();
        restriccionesAL = new ArrayList<>();
        restsViews = new ArrayList<>();

        numVariables = ConstruirFuncionSimplexActivity.numVariables;
        numRestricciones = 1;

        llenarRestriccionesAL();

        restriccion1LinLayout = findViewById(R.id.linLayout);
        restriccionesLL = findViewById(R.id.aquiVanLasRestriccionesLL);

        EditText rest1 = findViewById(R.id.rest);
        restNums.add(rest1);

        restriccionesAL.get(0).add(restriccion1LinLayout.findViewById(R.id.x1));
        restriccionesAL.get(1).add(restriccion1LinLayout.findViewById(R.id.x2));
        agregarVariables(numVariables, restriccion1LinLayout);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.añadirBtn:
                crearRestriccion();
                break;
            case R.id.borrarBtn:
                borrarRestriccion();
                break;
            case R.id.resolverBtn:
                try {
                    getCoeficientes();
                    startActivity(new Intent(this, MetodoSimplexActivity.class));
                }catch (NumberFormatException e){
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

    private void borrarRestriccion() {
        if (restsViews.size()!=0){
            restsViews.get(restsViews.size() - 1).setVisibility(View.GONE);
            restsViews.remove(restsViews.size() - 1);
            quitarCoefVariables();
            numRestricciones--;
        }

    }

    private void quitarCoefVariables() {
        for (int i = 0; i < restriccionesAL.size(); i++) {
            restriccionesAL.get(i).remove(restriccionesAL.get(i).size() - 1);
        }
        restNums.remove(restNums.size()-1);
    }

    //Añade a restriccionesAL n arraylists, dependiendo del número de variables
    void llenarRestriccionesAL() {
        for (int i = 0; i < numVariables; i++) {
            restriccionesAL.add(new ArrayList<EditText>());
        }
    }

    void agregarVariables(int numVars, ViewGroup viewGroup) {
        for (int i = 2; i < numVars; i++)
            restriccionesAL.get(i).add(agregarVariable(i + 1, viewGroup));
    }

    EditText agregarVariable(int n, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.new_variable_lin_layout, null);

        TextView tv = view.findViewById(R.id.numVariable);
        tv.setText(tv.getText().toString() + n);

        EditText coef = view.findViewById(R.id.editTxtCoeficiente);

        viewGroup.addView(view);

        return coef;
    }

    //como x1 y x2 vienen por defecto, tengo que hacerlo así
    void añadirCoeficienteX1X2(EditText coef1, EditText coef2) {
        restriccionesAL.get(0).add(coef1);
        restriccionesAL.get(1).add(coef2);
    }

    void crearRestriccion() {
        numRestricciones++;

        //Infla el layout de una restriccion
        LayoutInflater layoutInflater = getLayoutInflater();
        View nuevaRestriccion = layoutInflater.inflate(R.layout.restriccion_rel_layout_simplex, null);
        View restriccionView = nuevaRestriccion.findViewById(R.id.linLayout); //La view donde se va a añadir la restricción

        TextView textView = nuevaRestriccion.findViewById(R.id.numRestriccion);
        textView.setText(textView.getText().toString() + " " + numRestricciones);

        EditText coefX1 = nuevaRestriccion.findViewById(R.id.x1);
        EditText coefX2 = nuevaRestriccion.findViewById(R.id.x2);
        restriccionesAL.get(0).add(coefX1);
        restriccionesAL.get(1).add(coefX2);

        EditText rest = nuevaRestriccion.findViewById(R.id.rest);
        restNums.add(rest);

        agregarVariables(numVariables, (ViewGroup) restriccionView);

        restsViews.add(nuevaRestriccion);
        restriccionesLL.addView(nuevaRestriccion);
    }

    public static ArrayList<Float> getNumRests() {
        ArrayList<Float> numeros = new ArrayList<>();
        for (EditText e : restNums) numeros.add(Float.parseFloat(e.getText().toString()));

        return numeros;
    }

    public static ArrayList<ArrayList<Float>> getCoeficientes() {
        ArrayList<ArrayList<Float>> arrayLists = new ArrayList<>();

        for (int i = 0; i < restriccionesAL.get(0).size(); i++) {
            ArrayList<Float> coeficientes = new ArrayList<>();
            for (int j = 0; j < restriccionesAL.size(); j++) {
                float recipiente = Float.parseFloat(restriccionesAL.get(j).get(i).getText().toString());
                coeficientes.add(recipiente);
            }
            coeficientes.add(getNumRests().get(i));
            arrayLists.add(coeficientes);
        }
        return arrayLists;
    }
}