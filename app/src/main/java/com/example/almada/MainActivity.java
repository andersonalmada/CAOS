package com.example.almada;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.config.CaosConfig;
import br.ufc.great.caos.api.config.Inject;
import br.ufc.great.caos.data.DataOffloading;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.eval.Expression;
import br.ufc.great.syssu.eval.var.NumberConstant;
import br.ufc.great.syssu.eval.var.NumberSensorVariable;
import br.ufc.great.syssu.eval.var.StringConstant;
import br.ufc.great.syssu.eval.var.StringSensorVariable;
import br.ufc.great.syssu.eval.var.StringVariable;

import static br.ufc.great.syssu.eval.Expression.and;
import static br.ufc.great.syssu.eval.Expression.eq;
import static br.ufc.great.syssu.eval.Expression.gteq;
import static br.ufc.great.syssu.eval.Expression.sensor;

@CaosConfig(primaryEndpoint = "10.0.2.2")
public class MainActivity extends AppCompatActivity {

    @DataOffloading
    Medical medical;

    @Inject(Calc.class)
    ICalc calc;

    EditText etName;
    EditText etCPF;
    EditText etDate;
    Button btOffloading;
    Button btGet;

    String ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btOffloading = (Button) findViewById(R.id.btOffloading);

        Caos.getInstance().start(this, this);

        btOffloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result = calc.soma(2, 3);
                        Log.i("Result: ", result + "");
                    }
                }).start();
            }
        });
/*

        btOffloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medical = new Medical();
                medical.name = etName.getText().toString();
                medical.cpf = etCPF.getText().toString();
                medical.date = etDate.getText().toString();

                //Sensor
                medical.sensorTemperature = new Sensor("smartwatch.temperature", BigDecimal.valueOf(new Random().nextDouble()*(42-35)+35)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue());

                ArrayList<Double> list = new ArrayList<>();
                list.add(BigDecimal.valueOf(new Random().nextDouble()*(3)+2)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue());
                list.add(BigDecimal.valueOf(new Random().nextDouble()*(10)+30)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue());

                medical.sensorLocation = new Sensor("smartwatch.location", list);

                medical.sensorHeart = new Sensor("smartwatch.heart", BigDecimal.valueOf(new Random().nextDouble()*(120)+40)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue());

                dataManager.makeData();
            }
        });

        btGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern pattern = (Pattern) new Pattern().addField("date", "qwe");
                Log.i("Tuples", dataManager.filter(pattern, filter).toString());
            }
        });


 */
}

    IFilter filter = new IFilter() {

        @Override
        public Expression remoteFilter() {
            StringVariable field = new StringVariable("date");
            Expression exp1 = eq(field, new StringConstant("asd"));

            StringSensorVariable field2 = new StringSensorVariable("type");
            Expression exp2 = eq(field2, new StringConstant("smartwatch.heart"));

            StringSensorVariable field3 = new StringSensorVariable("name2");
            Expression exp3 = eq(field3, new StringConstant("ok1"));

            NumberSensorVariable field4 = new NumberSensorVariable("value");
            Expression exp4 = gteq(field4, new NumberConstant(3));

            NumberSensorVariable field5 = new NumberSensorVariable("timestamp");
            Expression exp5 = gteq(field5, new NumberConstant(3));

            Expression finalExp = sensor(exp2, exp4, exp5);

            return finalExp;
        }

        @Override
        public Expression localFilter() {
            StringVariable field = new StringVariable("date");
            Expression exp1 = eq(field, new StringConstant("asd"));

            StringVariable field2 = new StringVariable("cpf");
            Expression exp2 = eq(field2, new StringConstant("123"));

            Expression finalExp = and(exp1, exp2);

            return exp2;
        }
    };
}
