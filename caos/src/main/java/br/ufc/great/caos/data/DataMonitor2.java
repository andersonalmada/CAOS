package br.ufc.great.caos.data;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.main.SysSUManager;

public class DataMonitor2 {

	Object objMain;
	SysSUManager syssu;

	public DataMonitor2(Object objMain, SysSUManager syssu) {
		this.objMain = objMain;
		this.syssu = syssu;
	}

	public JSONObject makeData() {
		Tuple tuple = null;
		List<Integer> list = new ArrayList<>();

		try {
			Class c = objMain.getClass();

			for (Field field : c.getDeclaredFields()) {
				if (field.getAnnotation(DataOffloading.class) != null) {
					field.setAccessible(true);

					Object obj = field.get(objMain);

					tuple = (Tuple) new Tuple();

					tuple.addField("id_app", ((Context) objMain).getPackageName());
					tuple.addField("id_class", obj.getClass().getCanonicalName());
					tuple.addField("id_device",
							Secure.getString(((Context) objMain).getContentResolver(), Secure.ANDROID_ID));

					int i = 3;

					for (Field field2 : obj.getClass().getDeclaredFields()) {
						if (field2.getType() == File.class) {
							File file = (File) field2.get(obj);
							tuple.addField("file_" + field2.getName(), DataConverter.fileToString(file));
						} else if (field2.getType() == Sensor.class) {
							Sensor sensor = (Sensor) field2.get(obj);

							Tuple tuple2 = (Tuple) new Tuple();

							for (Field field3 : sensor.getClass().getDeclaredFields()) {
								tuple2.addField(field3.getName(), field3.get(sensor));
							}

							tuple.addField("sensor", tuple2);
						} else {
							tuple.addField(field2.getName(), field2.get(obj));
						}

						if (field2.getAnnotation(NotOffloading.class) != null) {
							list.add(i);
						}

						i++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("Tuple", tuple.toString());
		syssu.put(tuple);

		Tuple tuple3 = (Tuple) new Tuple();

		for (int i = 0; i < tuple.size(); i++) {
			if (list.contains(i) == false) {
				tuple3.addField(tuple.getField(i).getName(), tuple.getField(i).getValue());
			}
		}

		return convertToJSON(tuple3);
	}

	private JSONObject convertToJSON(Tuple tuple) {
		JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		try {

			for (int i = 0; i < tuple.size(); i++) {
				if (tuple.getField(i).getName().equals("sensor")) {
					JSONObject obj2 = new JSONObject();

					Tuple tuple2 = (Tuple) tuple.getField(i).getValue();

					obj2.put(tuple2.getField(0).getName(), tuple2.getField(0).getValue());
					obj2.put(tuple2.getField(1).getName(), tuple2.getField(1).getValue());
					obj2.put(tuple2.getField(2).getName(), tuple2.getField(2).getValue());

					jsonArray.put(obj2);
				} else {
					obj.put(tuple.getField(i).getName(), tuple.getField(i).getValue());
				}
			}

			obj.put("sensors", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}
}
