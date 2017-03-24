package cc.milione.me.mdict;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText editTextWord;
    String bingDictPath = "http://xtk.azurewebsites.net/BingService.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextWord = (EditText) findViewById(R.id.editTextWord);
    }

    public void searchOnClick(View view) {
        String wordVal = "Action=search&Word=" + editTextWord.getText().toString();
        String wordExplain = postWeb(bingDictPath,wordVal);
        Toast.makeText(this, wordExplain, Toast.LENGTH_SHORT).show();
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
                            Log.d("MainActivity",linestr);
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
