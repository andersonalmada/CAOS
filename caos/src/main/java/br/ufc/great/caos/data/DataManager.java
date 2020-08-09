package br.ufc.great.caos.data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.util.Log;
import br.ufc.great.caos.api.config.CaosConfig;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.main.SysSUManager;
import br.ufc.great.syssu.synchronizer.SynchronizerService;
import br.ufc.great.syssu.synchronizer.strategy.SizeSynchronizerStrategy;
import br.ufc.great.syssu.synchronizer.strategy.TimeSynchronizerStrategy;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {

	public static JSONArray arrayTuples;
	private static DataMonitor2 monitor;
	private static String endpoint;
	private static SysSUManager syssu;
	
	public DataManager(Object objMain) {
		arrayTuples = new JSONArray();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		CaosConfig config = objMain.getClass().getAnnotation(CaosConfig.class);
		endpoint = config.primaryEndpoint();

		SynchronizerService sync = new SynchronizerService();

		if (StrategyOffload.TIME == config.sync()) {
			sync.addStrategy(new TimeSynchronizerStrategy(config.value()));
		} else {
			sync.addStrategy(new SizeSynchronizerStrategy(config.value()));
		}
	
		syssu = new SysSUManager(endpoint);
		monitor = new DataMonitor2(objMain, syssu);

		PackageManager pm = ((Context) objMain).getPackageManager();
		String packageName = ((Context) objMain).getPackageName();

		for (ApplicationInfo app : pm.getInstalledApplications(0)) {
			if (app.sourceDir.contains(packageName)) {
				File file = new File(app.sourceDir);
				try {
					upload(file, ((Context) objMain).getPackageName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void makeData() {
		arrayTuples.put(monitor.makeData());
	}

	public static void offloading() {		
		if (!endpoint.equals("")) {
			RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
					arrayTuples.toString());

			Call<ResponseBody> call = new RetrofitConfig("http://"+endpoint+":31000").getDataService().postDatas(body);
			call.enqueue(new Callback<ResponseBody>() {
				@Override
				public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
					arrayTuples = new JSONArray();
				}

				@Override
				public void onFailure(Call<ResponseBody> call, Throwable t) {

				}
			});
		}
	}

	public static List<Tuple> filter(Pattern pattern, IFilter filter, Context context) {
		return SysSUManager.read(pattern, filter, context);	
	}

	private void upload(File file, String packagename) throws IOException {
		OkHttpClient client = new OkHttpClient();

		String url = "https://fvscopex.com/caos/index.php";

		Request request = new Request.Builder().url(url + "?isApk=" + packagename + "&size=" + file.length()).get()
				.build();
		okhttp3.Response response = client.newCall(request).execute();

		if (response.body().string().equals("false")) {
			Log.i("Upload", "Upload new version to server");
			RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("text/plain"), file))
					.addFormDataPart("apkname", packagename).addFormDataPart("size", "" + file.length()).build();
			request = new Request.Builder().url(url).post(formBody).build();
			response = client.newCall(request).execute();
		} else {
			Log.i("Upload", "Server has the latest version");
		}
	}
}
