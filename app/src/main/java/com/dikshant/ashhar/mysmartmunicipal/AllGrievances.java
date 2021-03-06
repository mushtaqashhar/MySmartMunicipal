package com.dikshant.ashhar.mysmartmunicipal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AllGrievances extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton radioButton;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_grievances);

        ll = (LinearLayout) findViewById(R.id.linearLayout);
        SharedPreferences sharedPreferences = getSharedPreferences("loginSession", Context.MODE_PRIVATE);
        String totalGrievances = sharedPreferences.getString("totalGrievances", "");
        Fetch fetch = new Fetch();
        fetch.execute();

        Button button =(Button) findViewById(R.id.btn_Gview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                Toast.makeText(getApplicationContext(),radioButton.getText(), Toast.LENGTH_SHORT).show();
                String s= radioButton.getText().toString();
                int value = Integer.parseInt(s.replaceAll("[^0-9]", ""));
                Intent intent = new Intent(AllGrievances.this, MyGrievance.class);
                intent.putExtra("gid",value);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(AllGrievances.this, Home.class);
        startActivity(intent);
    }

    class Fetch extends AsyncTask<String, String, String> {

        Connection con = null;
        Boolean found = false;
        int counter=0;
        String username,totalGrievance;
        Statement stmt;
        ResultSet rSet=null;

        @Override
        protected void onPreExecute() {
            SharedPreferences sharedPreferences = getSharedPreferences("loginSession", Context.MODE_PRIVATE);
            username = sharedPreferences.getString("key", "");
            totalGrievance=sharedPreferences.getString("totalGrievances","");
            counter= Integer.parseInt(totalGrievance);
        }

        @Override
        protected String doInBackground(String... strings) {
            con = Starter.connection();
            if (con != null) {
                try {
                    stmt = con.createStatement();
                    rSet = stmt.executeQuery("select gid, department from grievances where user_name = '" + username + "'");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), totalGrievance, Toast.LENGTH_SHORT).show();
            final RadioButton[] rb = new RadioButton[counter];
            for(int i=0; i<counter; i++){
                radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                rb[i]  = new RadioButton(getApplicationContext());
                try {
                    rSet.absolute(i+1);
                    rb[i].setText("G:"+rSet.getString("gid")+":"+rSet.getString("department"));
                    rb[i].setId(i + 100);
                }catch (SQLException e) {
                    e.printStackTrace();
                }
                radioGroup.addView(rb[i]);
            }

            try {
                con.close();
                stmt.close();
                rSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}