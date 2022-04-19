package com.example.kt2mysql;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class EditActivity extends AppCompatActivity {

    private EditText editname, editusername, editpassword;
    private Button btnedit;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        preferenceHelper = new PreferenceHelper(this);

        if(preferenceHelper.getIsLogin()){
            Intent intent = new Intent(EditActivity.this,EditActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }

        editname = (EditText) findViewById(R.id.editname);
        editusername = (EditText) findViewById(R.id.edituser);
        editpassword = (EditText) findViewById(R.id.editpass);

        btnedit = (Button) findViewById(R.id.btnedit);


        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMe();
            }
        });

    }

    private void editMe() {

        final String name = editname.getText().toString();
        final String username = editusername.getText().toString();
        final String password = editpassword.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EditInterface.EDITURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        EditInterface api = retrofit.create(EditInterface.class);

        Call<String> call = api.getUserEdit(name,username,password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Responsestring", response.body().toString());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        String jsonresponse = response.body().toString();
                        try {
                            parseEditData(jsonresponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseEditData(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.optString("status").equals("true")){

            saveInfo(response);

            Toast.makeText(EditActivity.this, "Edit Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditActivity.this,WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }else {

            Toast.makeText(EditActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInfo(String response){

        preferenceHelper.putIsLogin(true);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putName(dataobj.getString("name"));

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}