package br.ufc.great.caos.data;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings.Secure;

public class DataMonitor {

	Object objMain;

	public DataMonitor(Object objMain) {
		this.objMain = objMain;
	}

	public JSONObject makeData() {
		JSONObject jsonObject = null;

		try {
			Class c = objMain.getClass();

			for (Field field : c.getDeclaredFields()) {
				if (field.getAnnotation(DataOffloading.class) != null) {
					field.setAccessible(true);

					Object obj = field.get(objMain);

					jsonObject = new JSONObject();

					jsonObject.put("id_app", ((Context) objMain).getPackageName());
					jsonObject.put("id_class", obj.getClass().getCanonicalName());
					jsonObject.put("id_device",
							Secure.getString(((Context) objMain).getContentResolver(), Secure.ANDROID_ID));

					JSONArray jsonArray = new JSONArray();

					for (Field field2 : obj.getClass().getDeclaredFields()) {
						if (field2.getType() == File.class) {
							File file = (File) field2.get(obj);
							jsonObject.put("file " + field2.getName(), DataConverter.fileToString(file));
						} else if (field2.getType() == Sensor.class) {
							Sensor sensor = (Sensor) field2.get(obj);

							JSONObject objSensor = new JSONObject();

							for (Field field3 : sensor.getClass().getDeclaredFields()) {
								if (isCollection(field3.get(sensor))) {									
									JSONArray array = new JSONArray();
									List list = (List) field3.get(sensor);
									
								      for(int i = 0; i < list.size(); i++) {
								         array.put(list.get(i));
								      }
								      
									objSensor.put(field3.getName(), array);
								} else {
									objSensor.put(field3.getName(), field3.get(sensor));
								}
							}

							jsonArray.put(objSensor);
						} else {
							jsonObject.put(field2.getName(), field2.get(obj));
						}

						if (jsonArray.length() != 0) {
							jsonObject.put("sensors", jsonArray);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	private boolean isCollection(Object ob) {
		return ob instanceof Collection || ob instanceof Map;
	}
}
