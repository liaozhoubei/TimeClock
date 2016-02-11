package com.example.bert.timeclock;

import android.os.Bundle;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * Created by Bert on 2016/2/1.
 */
public class TimerActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int STOP = 0;
    private static final int START = 1;
    private static final int PAUSE = 2;

    public static final int MESSAGE_CODE = 888;
    private int mState;
    public boolean isContinue = true;

    private ClockView mClockView;
    private Button mStart_Button;
    private Button mStop_Button;
    private Button mPause_Button;
    private Button mRecord_Button;
    private long startTime;
    private long pauseTime;

    private Timer mTimerRunner;
    private ClockView.MyHandler mMyHandler;
    private ListView mTimeListview;
    private int mRecord_ID = 1; //list每条信息的id
    private ArrayList<Record> mList; //list时间数据
    private TimeAdapter mTimeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_draw);

        mClockView = (ClockView) findViewById(R.id.clockView);
        mStart_Button = (Button) findViewById(R.id.start_button);
        mStart_Button.setOnClickListener(this);
        mStop_Button = (Button) findViewById(R.id.clear_button);
        mStop_Button.setOnClickListener(this);
        mPause_Button = (Button) findViewById(R.id.pause_button);
        mPause_Button.setOnClickListener(this);
        mRecord_Button = (Button) findViewById(R.id.record_button);
        mRecord_Button.setOnClickListener(this);

        mTimeListview = (ListView) findViewById(R.id.list_view);
        mList = new ArrayList<>();
        mTimeAdapter = new TimeAdapter((LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE), mList);
        mTimeListview.setAdapter(mTimeAdapter);

        mTimerRunner = new Timer();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                if (mState == STOP) {
                    // 开始运行
                    mState = START;
                    startTime = System.currentTimeMillis();
                    mTimerRunner.startRunning();
                    new Thread(mTimerRunner).start();
                } else if (mState == PAUSE) {
                    // 从暂停到开始
                    mState = START;
                    startTime = startTime + System.currentTimeMillis() - pauseTime;
                    mTimerRunner.startRunning();
                    new Thread(mTimerRunner).start();
                }
                break;

            case R.id.pause_button:
                // 从开始到暂停
                mState = PAUSE;
                pauseTime = System.currentTimeMillis();
                mTimerRunner.stopRunning();
                break;

            case R.id.clear_button:
                // 点击暂停后点击停止，清空所有信息
                mState = STOP;
                mTimerRunner.stopRunning();
                sendMessage(0);
                mList.clear();
                mRecord_ID = 1;
                mTimeAdapter.notifyDataSetChanged();
                break;
            case R.id.record_button:
                if (mState == START) {
                    //在点击开始按键后，可以获取并更新listView
                    Record record = new Record();
                    long coast = System.currentTimeMillis() - startTime;
                    String[] time = getTime(coast);
                    record.recordTime = "   " + time[2] + " " + time[1] + "." + time[0];
                    record.id = mRecord_ID;
                    mRecord_ID++;
                    mList.add(0, record);
                    mTimeAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 将毫秒解析为 分/秒/毫秒的String数组
     * @param time 时间
     * @return 数组 String[0]为毫秒 String[1]为秒 String[2]为分
     */
    private String[] getTime(long time) {
        String[] arr = new String[3];
        arr[0] = ((time / 10) % 100 < 10) ? "0" + ((time / 10) % 100) : "" + ((time / 10) % 100);
        arr[1] = ((time / 1000) % 60 < 10) ? "0" + ((time / 1000) % 60) : "" + ((time / 1000) % 60);
        arr[2] = time / 60000 + "";
        return arr;
    }

    // 发送时间信息
    private void sendMessage(long time) {
        Bundle bundle = new Bundle();
        bundle.putInt("Time", (int) time);
        Message msg = mClockView.getMyHandler().obtainMessage();
        msg.what = mClockView.MESSAGE_CODE;
        msg.setData(bundle);
        mClockView.getMyHandler().sendMessage(msg);
    }


    private class Timer implements Runnable {
        @Override
        public void run() {
            while (isContinue) {
                long TimeNow = System.currentTimeMillis() - startTime;
                sendMessage(TimeNow);
            }
        }
        public void stopRunning() {
            isContinue = false;
        }

        public void startRunning() {
            isContinue = true;
        }
    }



}
