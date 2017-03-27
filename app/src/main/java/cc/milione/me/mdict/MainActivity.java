package cc.milione.me.mdict;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText editTextWord;
    TextView tViewWord;
    TextView tViewBrep;
    TextView tViewPos1;
    TextView tViewPos2;
    TextView tViewMn1;
    TextView tViewMn2;
    String bingDictPath = "http://xtk.azurewebsites.net/BingService.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextWord = (EditText) findViewById(R.id.editTextWord);
        tViewWord = (TextView) findViewById(R.id.textViewWord);
        tViewBrep = (TextView) findViewById(R.id.textViewBrep);
        tViewPos1 = (TextView) findViewById(R.id.textViewPos1);
        tViewPos2 = (TextView) findViewById(R.id.textViewPos2);
        tViewMn1 = (TextView) findViewById(R.id.textVieMn1);
        tViewMn2 = (TextView) findViewById(R.id.textVieMn2);
    }

    public void searchOnClick(View view) {
        String wordVal = "Action=search&Format=jsonwv&Word=" + editTextWord.getText().toString();
        String wordExplain = postWeb(bingDictPath,wordVal);
        if (wordExplain == null || wordExplain.equals("")) {
            Toast.makeText(this, "mDict未查询到相关内容，请检查", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("mDict","post:" + wordExplain);
            String mn1 = praseJson(wordExplain,"mn1");
            if (mn1.equals("")||mn1.equals(" ")||mn1.equals(", ")||mn1.equals("; "))
            {
                Toast.makeText(this, "mDict未查询到相关内容，请检查", Toast.LENGTH_SHORT).show();
            }
            else {
                tViewWord.setText(praseJson(wordExplain, "word"));
                tViewBrep.setText(praseJson(wordExplain, "brep"));
                tViewPos1.setText(praseJson(wordExplain, "pos1"));
                tViewPos2.setText(praseJson(wordExplain, "pos2"));
                tViewMn1.setText(praseJson(wordExplain, "mn1"));
                tViewMn2.setText(praseJson(wordExplain, "mn2"));
            }
        }
    }

    public String praseJson(String JsonStr, String JsonData) {
        String getData = null;
        try {
            JSONArray jsonArray = new JSONArray("["+JsonStr+"]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JSONObject = jsonArray.getJSONObject(i);
                getData = JSONObject.getString(JsonData);
                Log.d("mDict","prase:" + getData + "val:" + JsonData);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            getData = "";
        }
        return getData;
    }

    public String postWeb(String webPath, final String webVal){
        String webStr = "";
        try {
            webStr = new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    String line = null;

                    try{
                        URL url = new URL(params[0]);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");

                        OutputStreamWriter outputStreamWriter =new OutputStreamWriter(connection.getOutputStream(),"utf-8");
                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                        bufferedWriter.write(webVal);
                        bufferedWriter.flush();

                        InputStream inputStream = connection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String linestr;
                        while ((linestr = bufferedReader.readLine()) != null){
                            Log.d("mDict","Json:" + linestr);
                            line = linestr;
                        }
                        bufferedReader.close();
                        inputStreamReader.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return line;
                }
            }.execute(webPath).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return webStr;
    }
}
