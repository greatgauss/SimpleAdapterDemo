package com.greatgauss.example.SimpleAdapterDemo; 

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class MainActivity extends Activity {
    private final String TAG = "main";
    private static final int MSG_HEARTBEAT = 0xabcd0000;
    
    ArrayList<HashMap<String, Object>> mListOfMap = new ArrayList<HashMap<String, Object>>();
    private ListView mListViewNetcfg = null;
    SimpleAdapter mListAdapter;
    private int ip = 0;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        String[] attr = { 
            getString(R.string.con_state),
            getString(R.string.IP_addr),
            getString(R.string.netmask),
            getString(R.string.gateway)};
        String[] attrcontent = {"","","",""};

        for(int i = 0; i < 4; i++) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("netAttr", attr[i]);
        	map.put("netAttrContent", attrcontent[i]);
        	mListOfMap.add(map);
        }

        //Init SimleAdapter and bind ArrayList with it
        mListAdapter = new SimpleAdapter(
        	this,
        	mListOfMap,
        	//id for resource file, for convenience, the file should be named as netcfg_view_item.xml
          	R.layout.netcfg_view_item,
          	new String[] {"netAttr","netAttrContent"},
          	//Textview IDed by R.id.netAttr is defined in resource file
          	new int[] {R.id.netAttr,R.id.netAttrContent}
        );

        //Init ListView and bind it to SimpleAdapter
        mListViewNetcfg = (ListView) findViewById(R.id.listviewNetCfg);

        //Draw ListView
        mListViewNetcfg.setAdapter(mListAdapter);

		mListOfMap.get(0).put("netAttrContent", "Connected");
		mListOfMap.get(3).put("netAttrContent", "192.168.1.1");

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_HEARTBEAT:
                    ip++;

                    //Update Model of MVC
                    mListOfMap.get(1).put("netAttrContent", "192.168.1." + ip);
                    mListOfMap.get(2).put("netAttrContent", "255.255.255." + ip);

                    //Update View of MVC by callback
                    mListAdapter.notifyDataSetChanged();
                    break;
                }

                super.handleMessage(msg);
            }
        };

        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                while(true) {
                    Thread.sleep(1000);
                    Message message = new Message();
                    Log.d(TAG, "Send MSG_HEARTBEAT");                     
                    message.what = MSG_HEARTBEAT;
                    handler.sendMessage(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
        });
        task.start();
	}
}
