package com.x.xsnmp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvFunction=(ListView)findViewById(R.id.listViewFunction);
        ArrayList<HashMap<String,Object>>  listItem=new ArrayList<HashMap<String, Object>>();
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("itemImage",R.drawable.ic_users);
        map.put("itemCaption",getString(R.string.function_device));
        map.put("itemDescr",getString(R.string.function_device_detail));
        listItem.add(map);

        map=new HashMap<String, Object>();
        map.put("itemImage",R.drawable.ic_edit);
        map.put("itemCaption",getString(R.string.function_device_setting));
        map.put("itemDescr",getString(R.string.function_device_setting_detail));
        listItem.add(map);

        map=new HashMap<String, Object>();
        map.put("itemImage",R.drawable.ic_settings);
        map.put("itemCaption",getString(R.string.function_setting));
        map.put("itemDescr",getString(R.string.function_setting_detail));
        listItem.add(map);

        SimpleAdapter simpleAdapter=new SimpleAdapter(this,
                listItem,
                R.layout.itemfunction,
                new String[]{"itemImage","itemCaption","itemDescr"},
                new int[]{R.id.itemImage,R.id.itemCaption,R.id.itemDescr});

        lvFunction.setAdapter(simpleAdapter);

        lvFunction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position)
                {
                    case 0:
                        intent=new Intent(getApplicationContext(),DeviceActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        break;

                    case 2:

                        break;

                }
                //Toast.makeText(getApplicationContext(),"点击了第"+position+"个项目。",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
