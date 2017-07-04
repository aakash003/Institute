package com.nit.institute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.nit.institute.Course.CourseList;
import com.nit.institute.Course.CreateCourse;
import com.nit.institute.SQLite.DatabaseHandler;

import java.util.List;

public class TT extends AppCompatActivity {

    TextView mon,tue,wed,thu,fri;
    Spinner tmon,ttue,twed,tthu,tfri;

    String day,time;
    int column=5;

    Button sub;


    String[] items = new String[]{"-", "P1-8:00-9:00","P2-9:00-10:00","P3-10:00-11:00","P4-11:00-12:00","P5-1:00-2:00","P6-2:00-3:00","P7-3:00-4:00","P8-4:00-5:00"};
    ArrayAdapter<String> adapter ;
    String season=null;
    String branch=null;
    String coursecode=null;
    String sem=null;
    String path=null;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tt);



        adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);

        mon=(TextView)findViewById(R.id.tMonday);
        tue=(TextView)findViewById(R.id.tTuesday);
        wed=(TextView)findViewById(R.id.tWed);
        thu=(TextView)findViewById(R.id.tThu);
        fri=(TextView)findViewById(R.id.tFri);

        tmon=(Spinner)findViewById(R.id.sMon);
        tmon.setAdapter(adapter);


        ttue=(Spinner)findViewById(R.id.sTues);
        ttue.setAdapter(adapter);

        twed=(Spinner)findViewById(R.id.sWed);
        twed.setAdapter(adapter);

        tthu=(Spinner)findViewById(R.id.sThu);
        tthu.setAdapter(adapter);

        tfri=(Spinner)findViewById(R.id.sFri);
        tfri.setAdapter(adapter);

        sub=(Button)findViewById(R.id.bSTT);

        db=new DatabaseHandler(this);

        season=getIntent().getStringExtra("Season");
        branch=getIntent().getStringExtra("Branch");
        sem=getIntent().getStringExtra("Semester");
        coursecode=getIntent().getStringExtra("CourseCode");
       path =getIntent().getStringExtra("CourseCode");

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addTime(coursecode,"Monday",tmon.getSelectedItem().toString());
                db.addTime(coursecode,"Tuesday",ttue.getSelectedItem().toString());
                db.addTime(coursecode,"Wednesday",twed.getSelectedItem().toString());
                db.addTime(coursecode,"Thursday",tthu.getSelectedItem().toString());
                db.addTime(coursecode,"Friday",tfri.getSelectedItem().toString());
                db.addCourse(coursecode,season, path,column);
                Intent i = new Intent(getApplicationContext(), CourseList.class);
                i.putExtra("FileName ", path);
                startActivity(i);


            }
        });


    }
}
