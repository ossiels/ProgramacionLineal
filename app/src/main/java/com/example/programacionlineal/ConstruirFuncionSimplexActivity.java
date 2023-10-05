package com.example.programacionlineal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ConstruirFuncionSimplexActivity extends AppCompatActivity {

    RelativeLayout layout;

    LinearLayout fnObjLL, restriccionesLL, todo;
    ArrayList<View> viewFnAL; //Aqui van las view con los EditTexts de la funcion objetivo
    static ArrayList<EditText> coefVariablesFn; //Aqui van los EditTexts con los coeficientes de la funcion objetivo

    ArrayList<View> viewRestriccion; //Aqui van las view con los EditTexts de la funcion objetivo
    static ArrayList<EditText> coefVariablesRestriccion; //Aqui van los coeficientes de las variables de una restriccion
    ArrayList<ArrayList> restricciones; //Aqui va cada una de las restricciones

    EditText x1Fn, x2Fn;

    static int numVariables;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construir_funcion_simplex);

        init();
    }

    private void init(){
        layout = findViewById(R.id.layout);
        numVariables = 2;

        fnObjLL = findViewById(R.id.linearLayoutFnObj);
        restriccionesLL = findViewById(R.id.variablesDeRestriccionLL);
        todo = findViewById(R.id.todo);

        viewFnAL = new ArrayList<>();
        viewRestriccion = new ArrayList<>();

        x1Fn = findViewById(R.id.x1FnObj);
        x2Fn = findViewById(R.id.x2FnObj);

        coefVariablesFn = new ArrayList<>();
        coefVariablesFn.add(x1Fn);
        coefVariablesFn.add(x2Fn);

        ArrayList<EditText> x1s = new ArrayList<>();
        ArrayList<EditText> x2s = new ArrayList<>();
        ArrayList<EditText> rests = new ArrayList<>();

        EditText restriccion1x1 = findViewById(R.id.restriccion1X1);
        EditText restriccion1x2 = findViewById(R.id.restriccion1X2);
        EditText rest = findViewById(R.id.rest);

        x1s.add(restriccion1x1);
        x2s.add(restriccion1x2);
        rests.add(rest);

        coefVariablesRestriccion = new ArrayList<>();
        restricciones = new ArrayList<>();
        restricciones.add(x1s);
        restricciones.add(x2s);
        restricciones.add(rests);
    }

    public void onClick(View view){

        switch (view.getId()){
            case R.id.añadirVarBtn:
                agregarVariable();
                break;
            case R.id.borrarVarBtn:
                borrarVariable();
                break;
            case R.id.añadirRestriccionesBtn:

                try{
                    getCoeficientesFnObj();
                    Intent i = new Intent(this, AgregarRestriccionesSimplexActivity.class);
                    startActivity(i);
                } catch (NumberFormatException e){
                    Snackbar snackbar = Snackbar.make(layout, "Llena todos los campos", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }


                break;
            default:
                break;
        }

    }

    private void agregarVariable(){
        numVariables++;

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.new_variable_lin_layout,null);

        TextView tv = view.findViewById(R.id.numVariable);
        tv.setText(tv.getText().toString() + numVariables);

        EditText editText = view.findViewById(R.id.editTxtCoeficiente);


        viewFnAL.add(view);
        coefVariablesFn.add(editText);
        fnObjLL.addView(view);


        //Crea un nuevo arreglo donde se guardaran todas las EditTexts con los coeficientes xₙ (en realidad creo que queda mejor X sub i
        // que x sub n porque n sería el número total de variables x, e i sería cada una de ellas, pero no importa)
        ArrayList<EditText> xn = new ArrayList<>();
        restricciones.add(xn);
    }

    private void borrarVariable(){
        if(viewFnAL.size()!=0){
            viewFnAL.get(viewFnAL.size()-1).setVisibility(View.GONE);
            viewFnAL.remove(viewFnAL.size()-1);
            coefVariablesFn.remove(coefVariablesFn.size()-1);

            numVariables--;
        }
    }

    public static ArrayList<Float> getCoeficientesFnObj(){
        ArrayList<Float> al = new ArrayList<>();
        for (EditText e: coefVariablesFn)
            al.add(Float.parseFloat(e.getText().toString()));

        return al;
    }

}