package com.example.srivi.passwordgen;

import android.os.AsyncTask;
import java.util.ArrayList;

/**
 * Created by srivi on 27-05-2018.
 */

public class Async extends AsyncTask<Integer, Integer, ArrayList<String>> {

    IData iData;

    public Async(IData iData) {
        this.iData = iData;
    }

    @Override
    protected ArrayList<String> doInBackground(Integer... integers) {
        int count = integers[0];
        int length = integers[1];
        ArrayList<String> result = new ArrayList<>(  );
        int i;
        for (i = 1; i <= count; i++) {
            String password = Util.getPassword(length);
            result.add( password );
            iData.updateData( i );
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        iData.handleListData( strings );
    }

    public static interface IData {
        public void handleListData(ArrayList<String> result);
        public void updateData(int cur);
    }

}