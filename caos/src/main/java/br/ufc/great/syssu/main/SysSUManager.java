package br.ufc.great.syssu.main;

import static br.ufc.great.syssu.eval.Expression.and;
import static br.ufc.great.syssu.eval.Expression.eq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.TupleSpaceSecurityException;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.base.interfaces.ILocalDomain;
import br.ufc.great.syssu.eval.Expression;
import br.ufc.great.syssu.eval.var.StringConstant;
import br.ufc.great.syssu.eval.var.StringVariable;
import br.ufc.great.syssu.ubibroker.LocalUbiBroker;

public class SysSUManager {

	private LocalUbiBroker mLocalUbiBroker;
	private static ILocalDomain mDomain;
	private ILocalDomain mDomainDump;
	private static String endpoint;

	public SysSUManager(String endpoint) {
		try {
			mLocalUbiBroker = LocalUbiBroker.createUbibroker();
			mDomain = (ILocalDomain) mLocalUbiBroker.getDomain("caos");
			mDomainDump = (ILocalDomain) mLocalUbiBroker.getDomain("dump");
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.endpoint = endpoint;
	}

	public void put(Tuple tuple) {
		try {
			mDomain.put(tuple, null);
			mDomainDump.put(tuple, null);
		} catch (TupleSpaceException | TupleSpaceSecurityException e) {
			e.printStackTrace();
		}
	}

	public static List<Tuple> read(Pattern pattern, IFilter filter, Context context) {
		List<Tuple> list = null;
		String localCaos = "";

		if (context == null) {
			localCaos = "off-ms";
		} else if (Util.hasConnection(context)) {
			localCaos = "off-server";
		}

		if (!localCaos.equals("")) {
			if (pattern.size() > 0) {
				StringVariable field = new StringVariable(pattern.getField(0).getName());
				Expression auxExp = eq(field, new StringConstant(pattern.getField(0).getValue().toString()));
				Expression finalExp = auxExp;

				for (int i = 1; i < pattern.size(); i++) {
					field = new StringVariable(pattern.getField(i).getName());
					auxExp = eq(field, new StringConstant(pattern.getField(i).getValue().toString()));
					finalExp = and(finalExp, auxExp);
				}

				if (filter != null) {
					if (filter.remoteFilter() != null) {
						finalExp = and(finalExp, filter.remoteFilter());
					}
				}

				return convertJsonToTuples(filter(clearCaracters(finalExp), localCaos));
			}
		} else {
			try {
				list = mDomain.read(pattern, filter, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	public List<Tuple> readForSync() {
		List<Tuple> list = null;

		Pattern pattern = (Pattern) new Pattern().addField("?", "?");

		try {
			list = mDomainDump.read(pattern, "", null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public void clear() {
		Pattern pattern = (Pattern) new Pattern().addField("?", "?");

		try {
			mDomainDump.take(pattern, "", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String clearCaracters(Expression exp) {
		String result = exp.toString().replace("\"", "");

		int firstIndex = 0;

		while (firstIndex != -1) {
			firstIndex = result.indexOf("{ 'map.sensors.myArrayList': { $elemMatch : ", firstIndex);

			try {
				if (firstIndex != -1) {
					int lastIndex1 = result.indexOf("} }", firstIndex) + 2;
					result = result.substring(0, lastIndex1) + ", " + result.substring(lastIndex1 + 6);
					lastIndex1 = result.indexOf("} }", lastIndex1) + 2;
					result = result.substring(0, lastIndex1) + ", " + result.substring(lastIndex1 + 6);
					firstIndex += "{ 'map.sensors.myArrayList': { $elemMatch : ".length();
				}
			} catch (StringIndexOutOfBoundsException e) {
				break;
			}
		}

		return result;
	}

	private static List<Tuple> convertJsonToTuples(String result) {
		List<Tuple> tuples = new ArrayList<Tuple>();

		if (result == null) {
			return tuples;
		} else if (result.equals("[]")) {
			return tuples;
		}

		try {
			JSONArray array = new JSONArray(result);

			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Tuple tuple = (Tuple) new Tuple();

				Iterator iter = obj.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					try {
						if (key.equals("_id") || key.equals("_class")) {
						} else {
							JSONObject json2 = obj.getJSONObject(key);

							Iterator iter2 = json2.keys();
							while (iter2.hasNext()) {
								String key2 = (String) iter2.next();

								if (json2.get(key2) instanceof JSONObject) {
									JSONArray array2 = json2.getJSONObject(key2).getJSONArray("myArrayList");

									for (int j = 0; j < array2.length(); j++) {
										JSONObject json3 = array2.getJSONObject(j).getJSONObject("map");

										Tuple tuple2 = new Tuple();

										Iterator iter3 = json3.keys();
										while (iter3.hasNext()) {
											String key3 = (String) iter3.next();
											tuple2.addField(key3, json3.get(key3));
										}

										tuple.addField("sensor", tuple2);
									}
								} else {
									tuple.addField(key2, json2.get(key2));
								}
							}
						}
					} catch (JSONException e) {

					}
				}

				tuples.add(tuple);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return tuples;
	}

	private static String filter(String input, String localCaos) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    
	    String localEndpoint = "";
		
		if(localCaos.equals("off-ms")) {
			localEndpoint = "http://caos-data.default.svc.cluster.local:31000/api/caos/filter";	
		} else {
			localEndpoint = "http://"+endpoint+":31000/api/caos/filter";
		}
		
	    HttpPost httppost = new HttpPost(localEndpoint);
	    httppost.setHeader("Accept", "application/json");
	    httppost.setHeader("Content-type", "application/json");

	    try {
	    	httppost.setEntity(new StringEntity(input));
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
            InputStream inputStream = response.getEntity().getContent(); //Colocar resposta do servidor

            // 8. convert inputstream to string
            if (inputStream != null) {
				return convertInputStreamToString(inputStream);
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	    
	    return null;
	}
	
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}

        inputStream.close();
        return result;
    }

}
