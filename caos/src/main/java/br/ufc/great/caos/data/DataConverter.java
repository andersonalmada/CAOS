package br.ufc.great.caos.data;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class DataConverter {
	
	public static String fileToString(File file) {
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            return Base64.encodeToString(fileContent, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        return null;
    }

    public static File stringToFile(String encodedString, Context context) {
        File file = new File(context.getExternalFilesDir(null), "temp");
        try {
            byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
            FileUtils.writeByteArrayToFile(file, decodedBytes);
            return file;
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return null;
    }
}
