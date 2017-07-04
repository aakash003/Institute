package com.nit.institute.Student;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nit.institute.Course.CourseList;
import com.nit.institute.R;
import com.nit.institute.SQLite.DatabaseHandler;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.List;

public class StudentsAttendance extends AppCompatActivity {

    int GRID = 0;
    int LIST = 1;

    int flag_back=0;
    int flag_next=0;
    ArrayList<StudentsGetterSetter> studentAttendanceLinkedList = new ArrayList<>();
    ListView listView;

    GridView gridView;
    DatabaseHandler db;
    String courseCode;
    ArrayAdapter<StudentsGetterSetter> adapter;
    Button bSubmit;
    String dayOfWeek;
    SharedPreferences pref;
    ImageView bBack;
    ImageView bNext;
    String fileName,currentdate;
    int column;

    String dob;


     int SelectedPosition;
    int j=0;
    int i=0;
    int ql,qg;
    int rl,rg;
    int p=0;
    private String[] arraySpinner;
    private String[] arraySpinner2;
    boolean[] array = new boolean[200];
    private String type;
    private String per;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_attendance);

        this.arraySpinner = new String[] {
                "Regular", "Extra"};
        this.arraySpinner2 = new String[] {
                "P1-8:00-9:00","P2-9:00-10:00","P3-10:00-11:00","P4-11:00-12:00","P5-1:00-2:00","P6-2:00-3:00","P7-3:00-4:00","P8-4:00-5:00"};
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        pref=getApplicationContext().getSharedPreferences("FileName",Context.MODE_PRIVATE);

        db=new DatabaseHandler(this);
        courseCode=getIntent().getStringExtra("CourseCode");
        column=getIntent().getIntExtra("ColumnIndex",6);



        bSubmit=(Button)findViewById(R.id.bSubmit);
        gridView=(GridView)findViewById(R.id.gV);


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        dialog.setTitle(" ");

        final EditText titleBox = new EditText(this);
        dob=DateFormat.getDateInstance().format(new Date());
        titleBox.setText(dob);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        dayOfWeek = simpledateformat.format(date);
        layout.addView(titleBox);
        titleBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentDate = Calendar.getInstance();
                int smYear = mcurrentDate.get(Calendar.YEAR);
                int smMonth = mcurrentDate.get(Calendar.MONTH);
                int smDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int smdd=mcurrentDate.get(Calendar.DAY_OF_WEEK);


                DatePickerDialog mDatePicker = new DatePickerDialog(StudentsAttendance.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth=selectedmonth+1;
                          dob= selectedday + "-" + selectedmonth + "-" + selectedyear;
                        titleBox.setText("" + dob);

                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                        Date date = new Date(selectedyear,selectedmonth, selectedday-4);
                        dayOfWeek = simpledateformat.format(date);
                        Log.d("Day",dayOfWeek);
                    }
                }, smYear, smMonth, smDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });




        final Spinner sp=new Spinner(this);
        layout.addView(sp);
      sp.setMinimumHeight(200);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arraySpinner);
        sp.setAdapter(adapter);

        final Spinner sp2=new Spinner(this);
        layout.addView(sp2);
        sp2.setMinimumHeight(200);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arraySpinner2);
        sp2.setAdapter(adapter2);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                currentdate=titleBox.getText().toString();
                type=sp.getSelectedItem().toString();
                per=sp2.getSelectedItem().toString();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Intent i=new Intent(StudentsAttendance.this, CourseList.class);
                startActivity(i);
            }
        });

        dialog.setView(layout);
        dialog.show();



        Log.d("col", String.valueOf(column));


        fileName=db.getFileName(courseCode);
        fileName=fileName.substring(fileName.lastIndexOf("/")+1);
        studentAttendanceLinkedList=db.getStudentsListForAttendance(courseCode);

        listView=(ListView)findViewById(R.id.lv);
        listView.setDivider(null);
        listView.setDividerHeight(0);

        bBack=(ImageView)findViewById(R.id.bBack);
        bNext=(ImageView)findViewById(R.id.bNext);
        Log.d("LINKL", String.valueOf(studentAttendanceLinkedList.size()));

       ql=studentAttendanceLinkedList.size()/7;
        rl=studentAttendanceLinkedList.size()%7;
        qg=studentAttendanceLinkedList.size()/20;
        rg=studentAttendanceLinkedList.size()%20;
       /* if(i==0){
        adapter=new StudentAttendanceAdapter(this,studentAttendanceLinkedList.subList(i*7,(i*7)+7), LIST, GRID);
            listView.setAdapter(adapter);
            bBack.setVisibility(View.VISIBLE);
            bNext.setVisibility(View.VISIBLE);
            for(int x=0;x<7;x++)
            {
                if(array[x]==true)
                {
                    studentAttendanceLinkedList.get(x).setAttendanceStatus(true);
                }
            }
        }




        bNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                i++;
                if(i<ql) {
                    adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(i * 7, (i * 7) + 7), LIST, GRID);
                    listView.setAdapter(adapter);
                    bBack.setVisibility(View.VISIBLE);
                    bNext.setVisibility(View.VISIBLE);
                    for (int x = 0; x < 7; x++) {
                        if (array[(7 * i) + x] == true) {
                            studentAttendanceLinkedList.get((7 * i) + x).setAttendanceStatus(true);
                        }
                    }
                }
                else
                {
                    adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(i * 7, (i * 7) + rl), LIST, GRID);
                    listView.setAdapter(adapter);
                    bNext.setVisibility(View.VISIBLE);
                    bBack.setVisibility(View.VISIBLE);
                    for(int x=0;x<rl;x++)
                    {
                        if (array[(7 * i) + x] == true) {
                            studentAttendanceLinkedList.get((7 * i) + x).setAttendanceStatus(true);
                        }
                    }
                }
            }
        });



        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i--;
                if(i>=0)
                {
                    adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(i * 7, (i * 7) + 7), LIST, GRID);
                    listView.setAdapter(adapter);
                    bNext.setVisibility(View.VISIBLE);
                    bBack.setVisibility(View.VISIBLE);
                    for(int x=0;x<7;x++)
                    {
                        if (array[(7 * i) + x] == true) {
                            studentAttendanceLinkedList.get((7 * i) + x).setAttendanceStatus(true);
                        }
                    }
                }
                 if(i==0){
                    bBack.setVisibility(View.INVISIBLE);
                }
            }
        });




*/

       // studentAttendanceLinkedList.get(2);



        int screenHeight = ((WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//////////////////////////////////////////////////
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                        StudentsAttendance.this);
                builder.setTitle("Institute");
                builder.setMessage("Confirm Submission?");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                column++;
                                writeSheet(column,fileName,array,studentAttendanceLinkedList,currentdate);
                                dialog.dismiss();
                                Log.d("After",String.valueOf(column));
                                db.setColumn(courseCode,column);
                                Intent i=new Intent(StudentsAttendance.this,CourseList.class);
                                startActivity(i);
                            }
                        });
                builder.setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list:
                    listView.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.INVISIBLE);
                GRID=0;
                LIST=1;
                if(i==0){
                    adapter=new StudentAttendanceAdapter(this,studentAttendanceLinkedList.subList(i*7,(i*7)+7),LIST,GRID);
                    listView.setAdapter(adapter);
                    bBack.setVisibility(View.INVISIBLE);
                    bNext.setVisibility(View.VISIBLE);
                    for(int x=0;x<7;x++)
                    {
                        if(array[x]==true)
                        {
                            studentAttendanceLinkedList.get(x).setAttendanceStatus(true);
                        }
                    }
                }

                bNext.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        i++;
                        if(i<ql) {
                            adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(i * 7, (i * 7) + 7), LIST, GRID);
                            listView.setAdapter(adapter);
                            bBack.setVisibility(View.VISIBLE);
                            bNext.setVisibility(View.VISIBLE);
                            for (int x = 0; x < 7; x++) {
                                if (array[(7 * i) + x] == true) {
                                    studentAttendanceLinkedList.get((7 * i) + x).setAttendanceStatus(true);
                                }
                            }
                        }
                        else
                        {
                            adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(i * 7, (i * 7) + rl), LIST, GRID);
                            listView.setAdapter(adapter);
                            bNext.setVisibility(View.INVISIBLE);
                            bBack.setVisibility(View.VISIBLE);
                            for(int x=0;x<rl;x++)
                            {
                                if (array[(7 * i) + x] == true) {
                                    studentAttendanceLinkedList.get((7 * i) + x).setAttendanceStatus(true);
                                }
                            }
                        }
                    }
                });



                bBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        i--;
                        if(i>=0)
                        {
                            adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(i * 7, (i * 7) + 7), LIST, GRID);
                            listView.setAdapter(adapter);
                            bNext.setVisibility(View.VISIBLE);
                            bBack.setVisibility(View.VISIBLE);
                            for(int x=0;x<7;x++)
                            {
                                if (array[(7 * i) + x] == true) {
                                    studentAttendanceLinkedList.get((7 * i) + x).setAttendanceStatus(true);
                                }
                            }
                        }
                        if(i==0){
                            bBack.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                Toast.makeText(getApplicationContext(),"LISTVIEW",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_grid:
                GRID=1;
                LIST=0;
                    listView.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.VISIBLE);

                if(j==0){
                    adapter=new StudentAttendanceAdapter(this,studentAttendanceLinkedList.subList(j*20,(j*20)+20), LIST, GRID);
                    gridView.setAdapter(adapter);
                    bBack.setVisibility(View.INVISIBLE);
                    bNext.setVisibility(View.VISIBLE);
                    for(int x=0;x<20;x++)
                    {
                        if(array[x]==true)
                        {
                            studentAttendanceLinkedList.get(x).setAttendanceStatus(true);
                        }
                    }
                }




                bNext.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        j++;
                        if(j<qg) {
                            adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(j * 20, (j * 20) + 20), LIST, GRID);
                            gridView.setAdapter(adapter);
                            bBack.setVisibility(View.VISIBLE);
                            bNext.setVisibility(View.VISIBLE);
                            for (int x = 0; x < 20; x++) {
                                if (array[(20 * j) + x] == true) {
                                    studentAttendanceLinkedList.get((20 * j) + x).setAttendanceStatus(true);
                                }
                            }
                        }
                        else
                        {
                            adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(j * 20, (j * 20) + rg), LIST, GRID);
                            gridView.setAdapter(adapter);
                            bNext.setVisibility(View.INVISIBLE);
                            bBack.setVisibility(View.VISIBLE);
                            for(int x=0;x<rg;x++)
                            {
                                if (array[(20 * j) + x] == true) {
                                    studentAttendanceLinkedList.get((20 * j) + x).setAttendanceStatus(true);
                                }
                            }
                        }
                    }
                });



                bBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        j--;
                        if(j>=0)
                        {
                            adapter = new StudentAttendanceAdapter(StudentsAttendance.this, studentAttendanceLinkedList.subList(j * 20, (j * 20) + 20), LIST, GRID);
                            gridView.setAdapter(adapter);
                            bNext.setVisibility(View.VISIBLE);
                            bBack.setVisibility(View.VISIBLE);
                            for(int x=0;x<20;x++)
                            {
                                if (array[(20 * j) + x] == true) {
                                    studentAttendanceLinkedList.get((20 * j) + x).setAttendanceStatus(true);
                                }
                            }
                        }
                        if(j==0){
                            bBack.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                //Toast.makeText(getApplicationContext(),"GRIDVIEW COMING SOON!!",Toast.LENGTH_LONG).show();
                return  true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

//////////////////////////////////////////////ADAPTER//////////////////////////////////////////////////////////

    public class StudentAttendanceAdapter extends ArrayAdapter<StudentsGetterSetter> {
        List<StudentsGetterSetter> list;
        Context context;
        SparseBooleanArray sba=new SparseBooleanArray();
        int l=1;
        int g=0;



        public StudentAttendanceAdapter(Context context, List<StudentsGetterSetter> list, int LIST, int GRID) {
            super(context, R.layout.activity_students_attendance_item_layout, list);
            this.context = context;
            this.list = list;
            l=LIST;
            g=GRID;
            Log.d("LIST&&GRID", String.valueOf(l ) + String.valueOf(g));
        }



        @Override
        public StudentsGetterSetter getItem(int position)
        {
            //position=position+6;
            return list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            //position=position+6;
            return position;
        }



        public class ViewHolder {

            protected TextView participantName;

            protected TextView roll;

            ImageView present;

            ImageView absent;

        }




        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v=convertView;

           // int selectedColor = Color.parseColor("#1b1b1b");
            final StudentsGetterSetter wed = list.get(position);

            if (v == null&&l==1) {
                LayoutInflater inflator = LayoutInflater.from(context);
                v = inflator.inflate(R.layout.activity_students_attendance_item_layout, null);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.participantName = (TextView) v.findViewById(R.id.tName);
                viewHolder.roll=(TextView)v.findViewById(R.id.tRoll);
                viewHolder.participantName.bringToFront();
                viewHolder.roll.bringToFront();

                viewHolder.absent=(ImageView)v.findViewById(R.id.iAbsent);
                viewHolder.present=(ImageView)v.findViewById(R.id.iPresent);
                v.setTag(viewHolder);
                viewHolder.participantName.setTag(wed);
                viewHolder.roll.setTag(wed);
            } else if(v!=null&&l==1) {
                ViewHolder holder = (ViewHolder) v.getTag();
                holder.participantName.setTag(wed);
                holder.roll.setTag(wed);
            }
            else if(v==null&&g==1)
            {
                LayoutInflater inflator = LayoutInflater.from(context);
                v = inflator.inflate(R.layout.student_grid_layout, null);


                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.participantName = (TextView) v.findViewById(R.id.tName);
                viewHolder.roll=(TextView)v.findViewById(R.id.tRoll);
                viewHolder.participantName.bringToFront();
                viewHolder.roll.bringToFront();

                viewHolder.absent=(ImageView)v.findViewById(R.id.iAbsent);
                viewHolder.present=(ImageView)v.findViewById(R.id.iPresent);
                v.setTag(viewHolder);
                viewHolder.participantName.setTag(wed);
                viewHolder.roll.setTag(wed);
            }
            else if(v!=null&&g==1)
            {
                ViewHolder holder = (ViewHolder) v.getTag();
                holder.participantName.setTag(wed);

                holder.roll.setTag(wed);

            }
            else
            {
                ViewHolder holder = (ViewHolder) v.getTag();
                holder.participantName.setTag(wed);
                holder.roll.setTag(wed);
            }


            final ViewHolder viewHolder = (ViewHolder) v.getTag();
            if(l==1&&g==0){
            viewHolder.participantName.setText(wed.getStudentName());
            viewHolder.roll.setText(wed.getReg());}
            else if(l==0&&g==1)
            {
                viewHolder.participantName.setText(wed.getStudentName());
                viewHolder.roll.setText(wed.getReg().substring(wed.getReg().length()-3,wed.getReg().length()));
            }
            //Log.d("pos" ," "+ (k+position));

            if(l==1&&g==0) {
                if (array[i * 7 + position] == true) {
                    viewHolder.present.setVisibility(View.VISIBLE);
                    viewHolder.absent.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.absent.setVisibility(View.VISIBLE);
                    viewHolder.present.setVisibility(View.INVISIBLE);
                }

                viewHolder.absent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(getApplicationContext(),"dsdd",Toast.LENGTH_LONG).show()

                        if (viewHolder.present.getVisibility() == View.VISIBLE) {
                            array[i * 7 + position] = false;
                            viewHolder.absent.setVisibility(View.VISIBLE);
                            viewHolder.present.setVisibility(View.INVISIBLE);
                            sba.put(position, true);
                        } else {
                            array[i * 7 + position] = true;
                            // wed.setAttendanceStatus(true);
                            viewHolder.absent.setVisibility(View.INVISIBLE);
                            viewHolder.present.setVisibility(View.VISIBLE);
                            sba.put(position, false);
                        }

                    }
                });
                viewHolder.present.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.absent.getVisibility() == View.VISIBLE) {
                            array[i * 7 + position] = true;
                            viewHolder.absent.setVisibility(View.INVISIBLE);
                            viewHolder.present.setVisibility(View.VISIBLE);
                            sba.put(position, true);
                        } else {
                            array[i * 7 + position] = false;
                            viewHolder.absent.setVisibility(View.VISIBLE);
                            viewHolder.present.setVisibility(View.INVISIBLE);
                            sba.put(position, false);
                        }
                    }
                });
            }
            else if(g==1&&l==0)
            {
                if (array[j * 20 + position] == true) {
                    viewHolder.present.setVisibility(View.VISIBLE);
                    viewHolder.absent.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.absent.setVisibility(View.VISIBLE);
                    viewHolder.present.setVisibility(View.INVISIBLE);
                }

                viewHolder.absent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(getApplicationContext(),"dsdd",Toast.LENGTH_LONG).show()

                        if (viewHolder.present.getVisibility() == View.VISIBLE) {
                            array[j * 20 + position] = false;
                            viewHolder.absent.setVisibility(View.VISIBLE);
                            viewHolder.present.setVisibility(View.INVISIBLE);
                            sba.put(position, true);
                        } else {
                            array[j * 20 + position] = true;
                            // wed.setAttendanceStatus(true);
                            viewHolder.absent.setVisibility(View.INVISIBLE);
                            viewHolder.present.setVisibility(View.VISIBLE);
                            sba.put(position, false);
                        }

                    }
                });
                viewHolder.present.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.absent.getVisibility() == View.VISIBLE) {
                            array[j * 20 + position] = true;
                            viewHolder.absent.setVisibility(View.INVISIBLE);
                            viewHolder.present.setVisibility(View.VISIBLE);
                            sba.put(position, true);
                        } else {
                            array[j * 20 + position] = false;
                            viewHolder.absent.setVisibility(View.VISIBLE);
                            viewHolder.present.setVisibility(View.INVISIBLE);
                            sba.put(position, false);
                        }
                    }
                });
            }

            return v;
        }
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }



    public static void writeSheet(int column,String infileName,boolean []array,ArrayList<StudentsGetterSetter>list,String currentdate) {
        HSSFWorkbook wb;

        try {
            String outFileName =infileName ;
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


            // String currentDate = DateFormat.getDateInstance().format(new Date());

// textView is the TextView view that should display it
            //textView.setText(currentDateTimeString);

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            wb = new HSSFWorkbook(file);
            HSSFSheet worksheet = wb.getSheetAt(0);

            Cell cell = null;
            cell = worksheet.getRow(1).createCell(column);

            cell.setCellValue(currentdate);
            int i=2;
            while (i!=list.size()+2)
            {cell = worksheet.getRow(i).createCell(column);
                if(array[i-2]==true)
                cell.setCellValue("P");
                else
                    cell.setCellValue("A");
                i++;}
            file.close();
            FileOutputStream outFile =new FileOutputStream(new File(path, outFileName));
            wb.write(outFile);
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
