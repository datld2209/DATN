package com.example.myapplication.Activitis;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.example.myapplication.Adapters.HourlyAdapters;
import com.example.myapplication.Domains.Hourly;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import org.json.JSONObject;
import org.json.JSONArray;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity1 extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    private Handler handler;
    private boolean isServiceRunning = false;
    private String PBui, PTemp, PCo, PHumid, PRain;
    private String Text;
    TextView tvTemp, tvTime, tvBui, tvCo, tvHumid, tvChatluong, tvtb, tvtb2;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);


        tvTime = findViewById(R.id.textView2);
        tvTemp = findViewById(R.id.textView3);
        tvBui = findViewById(R.id.textView5);
        tvCo = findViewById(R.id.textView7);
        tvHumid = findViewById(R.id.textView9);
        tvChatluong = findViewById(R.id.textView4);
        tvtb = findViewById(R.id.textViewtb);
        tvtb2 = findViewById(R.id.textView4);
        imageView = findViewById(R.id.img);

        tvBui.addTextChangedListener(new MyTextWatcher());
        tvCo.addTextChangedListener(new MyTextWatcher());
        tvHumid.addTextChangedListener(new MyTextWatcher());
        tvTemp.addTextChangedListener(new MyTextWatcher());

        handler = new Handler(Looper.getMainLooper());
        startUpdatingTime();

        initRecyclerview();
        setVariable();
        setVariable1();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefHumid = database.getReference("Humidity");
        DatabaseReference myRefNhietDo = database.getReference("Temperature");
        DatabaseReference myRefBui = database.getReference("Dust Density");
        DatabaseReference myRefCo = database.getReference("Co Value");
        DatabaseReference myRefRain = database.getReference("Rain");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Rain").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String rain = dataSnapshot.getValue(String.class);
                PRain = rain;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rain.equals("0")) {
                            imageView.setImageResource(R.drawable.rainy);
                            tvtb2.setText("Bạn hãy nhớ cầm theo ô khi đi ra ngoài nhé!");

                        } else {
                            imageView.setImageResource(R.drawable.cloudy_sunny);
                            tvtb2.setText("  ");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
        databaseReference.child("Humidity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String humid = dataSnapshot.getValue(String.class);
                PHumid = humid;
                // Cập nhật các biến khác nếu cần
                updateTextView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
        databaseReference.child("Temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String temp = dataSnapshot.getValue(String.class);
                PTemp = temp;
                // Cập nhật các biến khác nếu cần
                updateTextView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
        databaseReference.child("Dust Density").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bui = dataSnapshot.getValue(String.class);
                PBui = bui;
                // Cập nhật các biến khác nếu cần
                updateTextView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
        databaseReference.child("Co Value").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String co = dataSnapshot.getValue(String.class);
                PCo = co;
                // Cập nhật các biến khác nếu cần
                updateTextView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        myRefNhietDo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nhietDo = dataSnapshot.getValue(String.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTemp.setText(nhietDo + "°C"); // Hiển thị nhiệt độ trên tv2
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
        myRefBui.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Bui = dataSnapshot.getValue(String.class);
                double buiValue = Double.parseDouble(Bui);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvBui.setText(Bui + "µg/m³"); // Hiển thị nồng độ bụi
                        double coValue = Double.parseDouble(tvCo.getText().toString().replace("ppm", "")); // Lấy giá trị CO từ TextView tvCo
                        checkDangerousLevel(coValue, buiValue); // Truyền cả hai giá trị vào phương thức checkDangerousLevel
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
        myRefCo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Co = dataSnapshot.getValue(String.class);
                double coValue = Double.parseDouble(Co);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCo.setText(Co + "ppm"); // Hiển thị nồng độ CO
                        double buiValue = Double.parseDouble(tvBui.getText().toString().replace("µg/m³", "")); // Lấy giá trị bụi từ TextView tvBui
                        checkDangerousLevel(coValue, buiValue); // Truyền cả hai giá trị vào phương thức checkDangerousLevel
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });


        myRefHumid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Humid = dataSnapshot.getValue(String.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvHumid.setText(Humid + "%"); // Hiển thị nhiệt độ trên tv2
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void checkDangerousLevel(double coValue, double buiValue) {
        if (coValue > 100 || buiValue > 255) {
            tvChatluong.setText("Chất lượng không khí: Nguy hiểm");
            sendNotification("Cảnh báo", "Nồng độ CO vượt quá mức nguy hiểm!");
        } else if ((100 < coValue && coValue > 50) || (154 < buiValue && buiValue < 255)) {
            tvChatluong.setText("Chất lượng không khí: Có hại cho sức khỏe");
            sendNotification("Cảnh báo", "2");
        } else if ((50 < coValue && coValue > 35) || (50 < buiValue && buiValue < 154)) {
            tvChatluong.setText("Chất lượng không khí: Vừa phải ");
            sendNotification("Cảnh báo", "3");
        } else {
            tvChatluong.setText("Chất lượng không khí: An toàn");
            sendNotification("Cảnh báo", "4");
        }
    }

    private void startUpdatingTime() {
        handler.post(updateTimeRunnable);
    }

    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTime();
            handler.postDelayed(this, 1000); // Cập nhật mỗi giây
        }
    };

    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy, HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        tvTime.setText(currentDateTime);
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Gọi phương thức update khi bất kỳ TextView nào thay đổi
            updateTextView();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private void updateTextView() {
        // Lấy dữ liệu từ các TextView
        // Khai báo các biến để tính toán
        double PYes = 0, PNo = 0, P1 = 0, P2 = 0, P3 = 0;
        double PBuiYes = 0, PBuibyNo = 0, PBui1 = 0, PBui2 = 0, PBui3 = 0;
        double PTempbyYes = 0, PTempbyNo = 0, PTemp1 = 0, PTemp2 = 0, PTemp3 = 0;
        double PCoYes = 0, PCoNo = 0, PCo1 = 0, PCo2 = 0, PCo3 = 0;
        double PHumidYes = 0, PHumidNo = 0, PHumid1 = 0, PHumid2 = 0, PHumid3 = 0;


        //Dust: >56 Kem, 36-55 co hai cho sk, 13-15, Trung binh, <12 tot
        //CO: 0-150 TB, 150-200 xau, 200-250,250-300 rat xau, >300 Nguy hiem
        //Nhiet do: 0-15 Lanh, 15-30 mat me, 30-40 nong
        //Do am: <50% Kho, 50-70 ly tuong, >70 am uot
        // Tính tỉ lệ Yes và No từ dữ liệu của A
        //Yes: Dust<15,  CO<150, Nhiet do 15-30,doam 50-70
        String[][] A = {{"277.00", "1381", "27.60", "69.00", "Yes"},
                {"278.00", "1383", "27.50", "77.00", "Yes"},
                {"275.00", "1385", "27.70", "77.00", "Yes"},
                {"270.00", "1387", "27.60", "76.00", "Yes"},
                {"269.00", "1389", "27.80", "75.00", "Yes"},
                {"268.00", "1391", "28.00", "74.00", "Yes"},
                {"267.00", "1393", "27.60", "73.00", "Yes"},
                {"271.00", "1395", "27.50", "72.00", "Yes"},
                {"272.00", "1397", "27.40", "77.00", "Yes"},
                {"276.00", "1399", "27.30", "75.00", "Yes"},
                {"250.00", "1381", "27.20", "75.00", "Yes"},
                {"265.00", "1383", "27.10", "77.00", "Yes"},
                {"263.00", "1379", "28.10", "74.00", "Yes"},
                {"267.00", "1377", "28.30", "73.00", "Yes"},
                {"273.00", "1375", "28.40", "72.00", "Yes"},
                {"275.00", "1373", "28.20", "71.00", "Yes"},
                {"276.00", "1371", "28.10", "78.00", "Yes"},
                {"277.00", "1383", "28.30", "76.00", "Yes"},
                {"272.00", "1381", "27.90", "77.00", "Yes"},
                {"274.00", "1387", "28.20", "77.00", "Yes"},
                {"275.00", "1379", "28.40", "78.00", "Yes"},
                {"276.00", "1378", "28.50", "69.00", "Yes"},
                {"255.00", "1373", "27.20", "70.00", "Yes"},
                {"256.00", "1387", "27.30", "71.00", "Yes"},
                {"261.00", "1382", "27.20", "77.00", "Yes"},
                {"285.00", "1419", "28.00", "77.00", "No"},
                {"286.00", "1417", "27.60", "78.00", "No"},
                {"287.00", "1423", "27.50", "76.00", "No"},
                {"288.00", "1425", "28.60", "71.00", "No"},
                {"289.00", "1427", "27.30", "79.00", "No"},
                {"290.00", "1433", "27.60", "80.00", "No"},
                {"291.00", "1435", "27.60", "83.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 20
                {"292.00", "1437", "27.70", "84.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 45
                {"287.00", "1435", "27.80", "85.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 30
                {"289.00", "1427", "27.50", "76.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 48
                {"290.00", "1435", "28.10", "72.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 55
                {"291.00", "1427", "27.70", "71.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 65
                {"292.00", "1425", "27.80", "72.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 72
                {"289.00", "1441", "27.90", "77.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 90
                {"293.00", "1433", "27.30", "76.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 95
                {"291.00", "1437", "27.60", "75.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 98
                {"291.00", "1439", "27.50", "76.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 50
                {"286.00", "1445", "28.10", "73.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 60
                {"287.00", "1443", "27.90", "78.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 40
                {"288.00", "1427", "27.60", "79.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 70
                {"289.00", "1429", "27.60", "80.00", "No"},   // Bụi > 56, CO > 250, Nhiệt độ > 30, Độ ẩm 85
                {"282.00", "1433", "28.20", "76.00", "No"},
                {"283.00", "1403", "27.70", "76.00", "1"},
                {"281.00", "1405", "27.60", "77.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 28, Độ ẩm 60
                {"280.00", "1407", "27.50", "78.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 34, Độ ẩm 65
                {"282.00", "1409", "27.60", "79.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 32, Độ ẩm 45
                {"279.00", "1407", "27.80", "80.00", "1"},
                {"284.00", "1411", "27.90", "81.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 28, Độ ẩm 55
                {"285.00", "1413", "27.30", "69.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 34, Độ ẩm 50
                {"286.00", "1415", "28.00", "70.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 32, Độ ẩm 40
                {"285.00", "1405", "28.10", "72.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 37, Độ ẩm 80
                {"281.00", "1407", "28.00", "77.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 28, Độ ẩm 90
                {"283.00", "1409", "27.60", "76.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 34, Độ ẩm 95
                {"282.00", "1405", "27.50", "78.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 32, Độ ẩm 85
                {"280.00", "1403", "27.70", "79.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 37, Độ ẩm 75
                {"279.00", "1401", "27.60", "82.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 28, Độ ẩm 65
                {"278.00", "1415", "27.40", "81.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 34, Độ ẩm 55
                {"278.00", "1417", "27.70", "78.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 32, Độ ẩm 70
                {"282.00", "1419", "27.90", "79.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 37, Độ ẩm 80
                {"281.00", "1421", "28.00", "74.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 28, Độ ẩm 90
                {"283.00", "1407", "27.70", "76.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 34, Độ ẩm 95
                {"281.00", "1405", "27.80", "77.00", "1"},    // Bụi > 36, CO > 150, Nhiệt độ 32, Độ ẩm 85
                {"282.00", "1411", "27.90", "77.00", "1"},
                {"277.00", "1381", "28.60", "69.00", "2"},
                {"278.00", "1383", "29.50", "68.00", "2"},
                {"275.00", "1385", "28.70", "67.00", "2"},
                {"270.00", "1387", "28.60", "66.00", "2"},
                {"269.00", "1389", "28.80", "65.00", "2"},
                {"268.00", "1391", "29.00", "64.00", "2"},
                {"267.00", "1393", "28.60", "63.00", "2"},
                {"271.00", "1395", "28.50", "62.00", "2"},
                {"272.00", "1397", "30.40", "61.00", "2"},
                {"276.00", "1399", "29.30", "68.00", "2"},
                {"250.00", "1381", "29.20", "70.00", "2"},
                {"265.00", "1383", "30.10", "62.00", "2"},
                {"263.00", "1379", "29.10", "55.00", "2"},
                {"267.00", "1377", "29.30", "59.00", "2"},
                {"273.00", "1375", "28.40", "58.00", "2"},
                {"275.00", "1373", "29.20", "61.00", "2"},
                {"276.00", "1371", "29.10", "62.00", "2"},
                {"277.00", "1383", "29.30", "63.00", "2"},
                {"272.00", "1381", "28.90", "71.00", "2"},
                {"274.00", "1387", "30.20", "73.00", "2"},
                {"275.00", "1379", "29.40", "65.00", "2"},
                {"277.00", "1381", "16.00", "69.00", "3"},
                {"278.00", "1383", "15.00", "77.00", "3"},
                {"275.00", "1385", "16.50", "77.00", "3"},
                {"270.00", "1387", "15.10", "76.00", "3"},
                {"269.00", "1389", "15.00", "75.00", "3"},
                {"268.00", "1391", "14.00", "74.00", "3"},
                {"267.00", "1393", "13.00", "73.00", "3"},
                {"271.00", "1395", "12.00", "72.00", "3"},
                {"272.00", "1397", "11.00", "77.00", "3"},
                {"276.00", "1399", "10.00", "75.00", "3"},
                {"250.00", "1381", "9.00", "75.00", "3"},
                {"265.00", "1383", "10.20", "77.00", "3"},
                {"263.00", "1379", "12.20", "74.00", "3"},
                {"267.00", "1377", "13.50", "73.00", "3"},
                {"273.00", "1375", "14.50", "72.00", "3"},
                {"275.00", "1373", "15.00", "71.00", "3"},
                {"276.00", "1371", "14.60", "78.00", "3"},
                {"277.00", "1383", "14.70", "76.00", "3"},
                {"272.00", "1381", "13.20", "77.00", "33"},
                {"274.00", "1387", "14.70", "77.00", "3"},
                {"275.00", "1379", "15.80", "78.00", "3"},
                {"276.00", "1378", "14.20", "69.00", "3"},
                {"255.00", "1373", "13.60", "70.00", "3"},
                {"256.00", "1387", "14.70", "71.00", "3"},
                {"261.00", "1382", "13.90", "77.00", "3"}};

        int C = A.length;

        for (String[] anA : A) {
            if (anA[4].equals("Yes")) {
                PYes++;
            } else if (anA[4].equals("1")) {
                P1++;

            } else if (anA[4].equals("2")) {
                P2++;

            } else if (anA[4].equals("3")) {
                P3++;

            } else {
                PNo++;
            }
        }

        // Tính tỉ lệ input1
        for (String[] anA : A) {
            if (anA[4].equals("Yes") && anA[0].equals(PBui)) {
                PBuiYes++;
            }
            if (anA[4].equals("No") && anA[0].equals(PBui)) {
                PBuibyNo++;
            }
            if (anA[4].equals("1") && anA[0].equals(PBui)) {
                PBui1++;
            }
            if (anA[4].equals("2") && anA[0].equals(PBui)) {
                PBui2++;
            }
            if (anA[4].equals("3") && anA[0].equals(PBui)) {
                PBui3++;
            }

        }

        // Tính tỉ lệ input2
        for (String[] anA : A) {
            if (anA[4].equals("Yes") && anA[1].equals(PCo)) {
                PCoYes++;
            }
            if (anA[4].equals("No") && anA[1].equals(PCo)) {
                PCoNo++;
            }
            if (anA[4].equals("1") && anA[1].equals(PCo)) {
                PCo1++;
            }
            if (anA[4].equals("2") && anA[1].equals(PCo)) {
                PCo2++;
            }
            if (anA[4].equals("3") && anA[1].equals(PCo)) {
                PCo3++;
            }
        }

        // Tính tỉ lệ input3
        for (String[] anA : A) {
            if (anA[4].equals("Yes") && anA[2].equals(PTemp)) {
                PTempbyYes++;
            }
            if (anA[4].equals("No") && anA[2].equals(PTemp)) {
                PTempbyNo++;
            }
            if (anA[4].equals("1") && anA[2].equals(PTemp)) {
                PTemp1++;
                ;
            }
            if (anA[4].equals("2") && anA[2].equals(PTemp)) {
                PTemp2++;
                ;
            }
            if (anA[4].equals("3") && anA[2].equals(PTemp)) {
                PTemp3++;
                ;
            }
        }

        // Tính tỉ lệ input4
        for (String[] anA : A) {
            if (anA[4].equals("Yes") && anA[3].equals(PHumid)) {
                PHumidYes++;
            }
            if (anA[4].equals("No") && anA[3].equals(PHumid)) {
                PHumidNo++;
            }
            if (anA[4].equals("1") && anA[3].equals(PHumid)) {
                PHumid1++;
            }
            if (anA[4].equals("2") && anA[3].equals(PHumid)) {
                PHumid2++;
            }
            if (anA[4].equals("3") && anA[3].equals(PHumid)) {
                PHumid3++;
            }
        }

        // Tính toán kết quả
        double Z1 = (PBuiYes / PYes) * (PTempbyYes / PYes) * (PCoYes / PYes) * (PHumidYes / PYes);
        double Z2 = (PBuibyNo / PNo) * (PTempbyNo / PNo) * (PCoNo / PNo) * (PHumidNo / PNo);
        double Z3 = (PBui1 / P1) * (PTemp1 / P1) * (PCo1 / P1) * (PHumid1 / P1);
        double Z4 = (PBui2 / P2) * (PTemp2 / P2) * (PCo2 / P2) * (PHumid2 / P2);
        double Z5 = (PBui3 / P3) * (PTemp3 / P3) * (PCo3 / P3) * (PHumid3 / P3);

        double ZYes = Z1 / (Z1 + Z2 + Z3 + Z4 + Z5);
        double ZNo = Z2 / (Z1 + Z2 + Z3 + Z4 + Z5);
        double Zkq1 = Z3 / (Z1 + Z2 + Z3 + Z4 + Z5);
        double Zkq2 = Z4 / (Z1 + Z2 + Z3 + Z4 + Z5);
        double Zkq3 = Z5 / (Z1 + Z2 + Z3 + Z4 + Z5);


        // Sau khi tính toán kết quả
        if (Zkq1 > ZYes && Zkq1 > ZNo && Zkq1 > Zkq2 && Zkq1 > Zkq3) {
            tvtb.setText("Lời khuyên : Hãy đeo khẩu trang");
        } else if (Zkq2 > ZYes && Zkq2 > ZNo && Zkq2 > Zkq1 && Zkq2 > Zkq3) {
            tvtb.setText("Lời khuyên : Hãy uống nhiều nước ");
        } else if (Zkq3 > ZYes && Zkq3 > ZNo && Zkq3 > Zkq1 && Zkq3 > Zkq2) {
            tvtb.setText("Lời khuyên : Nhớ mặc áo ấm ");
        } else if (ZYes > Zkq1 && ZYes > ZNo && ZYes > Zkq2 && ZYes > Zkq3) {
            tvtb.setText("Lời khuyên : Trời rất đẹp, Có thể ra ngoài");
        } else if (ZNo > Zkq1 && ZNo > ZYes && ZNo > Zkq2 && ZNo > Zkq3) {
            tvtb.setText("Lời khuyên :Không nên ra ngoài");


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeRunnable);
    }


    private void setVariable() {
        TextView next7dayBtn = findViewById(R.id.nextBtn);
        next7dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity1.this, FutureActivity.class));
            }
        });
    }

    private void setVariable1() {
        ConstraintLayout backBtn = findViewById(R.id.backBtn1);
        backBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity1.this, MainActivity.class)));
    }


    //    private void initRecyclerview() {
//        ArrayList<Hourly> items= new ArrayList<>();
//        items.add(new Hourly("9 pm",28,"cloudy"));
//        items.add(new Hourly("11 pm",29,"sunny"));
//        items.add(new Hourly("12 pm",30,"wind"));
//        items.add(new Hourly("1 am",29,"rainy"));
//        items.add(new Hourly("2 am",27,"storm"));
//
//        recyclerView=findViewById(R.id.view1);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
//
//        adapterHourly = new HourlyAdapters(items, MainActivity1.this);
//        recyclerView.setAdapter(adapterHourly);
//
//    }
    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(this, MainActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.cloudy)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void initRecyclerview() {
        ArrayList<Hourly> items = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/forecast?lat=21.0285&lon=105.8542&appid=f1bb8c8754ef4707f529c00449b1c751")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String apiResponse = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(apiResponse);
                        JSONArray list = jsonObject.getJSONArray("list");

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject item = list.getJSONObject(i);
                            String dtTxt = item.getString("dt_txt");

                            // Phân tích chuỗi ngày giờ
                            Date date = inputFormat.parse(dtTxt);

                            // Chỉ lấy phần giờ
                            String hour = outputFormat.format(date);

                            JSONObject main = item.getJSONObject("main");
                            double temp = main.getDouble("temp");

                            JSONArray weather = item.getJSONArray("weather");
                            JSONObject weatherItem = weather.getJSONObject(0);
                            String description = weatherItem.getString("description");
                            String icon;
                            switch (description) {
                                case "clear sky":
                                    icon = "sunny";
                                    break;
                                case "broken clouds":
                                    icon = "cloudy";
                                    break;
                                case "overcast clouds":

                                case "scattered clouds":
                                    icon = "cloudy_sunny";
                                    break;
                                case "light rain":
                                case "moderate rain":
                                    icon = "rainy";
                                    break;
                                case "thunderstorm":
                                    icon = "storm";
                                    break;
                                case "snow":
                                case "light snow":
                                    icon = "snowy";
                                    break;
                                case "mist":
                                    icon = "misty";
                                    break;
                                default:
                                    icon = "cloudy";
                                    break;
                            }
                            if (hour.equals("00")) {
                                hour = "24";
                            }

                            String amPm;
                            int hourInt = Integer.parseInt(hour);
                            if (hourInt == 0) {
                                hourInt = 12;
                                amPm = "AM";
                            } else if (hourInt < 12) {
                                amPm = "AM";
                            } else if (hourInt == 12) {
                                amPm = "PM";
                            } else {
                                hourInt -= 12;
                                amPm = "PM";
                            }
                            items.add(new Hourly(hourInt+" " + amPm, (int) (temp - 273.15), icon));
                            // Kiểm tra nếu dtTxt kết thúc là 00:00:00 thì dừng lại
                            if (dtTxt.endsWith("00:00:00")) {
                                break;
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> {

                        recyclerView = findViewById(R.id.view1);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

                        adapterHourly = new HourlyAdapters(items, MainActivity1.this);
                        recyclerView.setAdapter(adapterHourly);
                        adapterHourly.notifyDataSetChanged(); // Cập nhật dữ liệu mới cho adapter
                    });
                }
            }
        });
    }
}