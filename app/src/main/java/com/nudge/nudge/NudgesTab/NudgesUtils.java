package com.nudge.nudge.NudgesTab;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 09/10/17.
 */

public class NudgesUtils {
    private static final String TAG = "NudgeUtils";

    public static List<NudgeClass> loadNudges(Context context){
        try{
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONArray array = new JSONArray(loadJSONFromAsset(context, "nudges.json"));
            List<NudgeClass> nudgeList = new ArrayList<>();
            for(int i=0;i<array.length();i++){
                NudgeClass nudge = gson.fromJson(array.getString(i), NudgeClass.class);
                nudgeList.add(nudge);
            }
            return nudgeList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is=null;
        try {
            AssetManager manager = context.getAssets();
            Log.d(TAG,"path "+jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
