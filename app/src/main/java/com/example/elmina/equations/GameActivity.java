package com.example.elmina.equations;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameActivity extends Activity implements View.OnClickListener {


    private static final int EQ_PORT = 6000;
    Handler networkHandler;

    Socket socket;

    static ArrayList<Equation> eqList;
    static Equation now;
    static int count = 0;
    static int countE = 0;
    static TextView countTxt;
    static TextView eqTxt;
    Handler h;
    static ProgressBar progressBar;
    static int n; // время на решение
    static int eqNum;
    static Button an;
    boolean isClient = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        n = 6;
        // тестовые уравнения
        Boolean tr = true;
        String[] e =
                {"2 + 3 < 10",
                "23 - 12 = 2",
                "0 > 2 - 23",
                "4 ^ 2 < 18",
                "45 + 12 = 214",
                "32 / 2 = 16",
                "2 * 4 = 8"};
        eqList = new ArrayList<Equation>();
        for (int i=0; i < 7 ; i++){
            Equation a = new Equation(e[i], tr);
            tr = !tr;
            eqList.add(a);
        }

        findViewById(R.id.answBtn).setOnClickListener(this);
        countTxt = (TextView)findViewById(R.id.count);
        eqTxt = (TextView)findViewById(R.id.eqTxt);
        progressBar = (ProgressBar) findViewById(R.id.timePrb);
        an = (Button) findViewById(R.id.answBtn);
        progressBar.setMax(n);
        isClient = (boolean)getIntent().getSerializableExtra("ISCLIENT");
        final String addr = (String) getIntent().getSerializableExtra("ADDR");

        h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (isClient) startNetworkGame(isClient, message.obj.toString());
                else startNetworkGame(isClient, null);
            }
        };
        final Thread t = new Thread(new Runnable() {
            public void run() {
                Message m = new Message();
                m.obtain();
                m.obj = addr;
                h.sendMessage(m);
            }
        });
        t.start();
        //endGame(true);
    }


    private void endGame(boolean isWinner1) {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
        Intent intent = new Intent(GameActivity.this, Result.class);
        intent.putExtra("POINTS", count);
        intent.putExtra("ISWINNER", isWinner1);
        startActivity(intent);
    }

    private void startNetworkGame(final boolean isClient, String address) {
        Handler uiHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == -1 || progressBar.getProgress() == n) {
                    SystemClock.sleep(1000);
                    an.setBackgroundColor(Color.rgb(179, 210, 105));
                    eqNum++;
                    now = eqList.get(eqNum);
                    progressBar.setProgress(0);
                    eqTxt.setText(now.text);
                    countTxt.setText(Integer.toString(count));
                } else {
                    progressBar.setProgress(progressBar.getProgress() + 1);
                }
                return true;
            }
        });
        new Thread(new NetworkThread(uiHandler, isClient, address)).start();
    }

    private class NetworkThread extends HandlerThread {

        private final Handler uiHandler;
        private final boolean isClient;
        private String address;


        public NetworkThread(Handler uiHandler, boolean isClient, String address) {
            super("client thread");
            this.uiHandler = uiHandler;
            this.isClient = isClient;
            this.address = address;
        }


        @Override
        protected void onLooperPrepared() {
            try {
                if (isClient) {
                    InetAddress serverAddress = InetAddress.getByName(address);
                    socket = new Socket(serverAddress, EQ_PORT);
                } else {
                    ServerSocket serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(EQ_PORT));
                    socket = serverSocket.accept();
                    serverSocket.close();
                }
                final PrintWriter socketWriter = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream()), true);
                final BufferedReader socketReader =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));


                networkHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        socketWriter.printf("%d", msg.what);
                        uiHandler.sendEmptyMessage(-1);
                        return true;
                    }

                });
                Thread t3 = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                String line = socketReader.readLine();
                                if (!line.isEmpty()){
                                    if (now.isRight()){
                                        countE++;
                                    }
                                    else {
                                        countE--;
                                    }
                                    uiHandler.sendEmptyMessage(-1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                Thread t2 = new Thread(new Runnable() {
                    public void run() {
                        eqNum = -1;
                        uiHandler.sendEmptyMessage(-1);
                        while (eqNum < n) {
                            SystemClock.sleep(1000);
                            uiHandler.sendEmptyMessage(1);
                        }
                        endGame(count > countE);
                    }
                });
                t3.start();
                t2.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.answBtn:
                if (now.isRight()){
                    count++;
                    findViewById(R.id.answBtn).setBackgroundColor(Color.GREEN);

                }
                else {
                    count--;
                    findViewById(R.id.answBtn).setBackgroundColor(Color.RED);
                }
                networkHandler.sendEmptyMessage(-1);
                break;
            default:
                Log.e("Equations", "Не реализовано");
        }
    }
}
