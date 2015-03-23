package com.intel.ndg.chronos;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class CloudSettingsActivity extends ActionBarActivity {

    private EditText miFashionIP;
    private EditText miFashionPort;

    private EditText mAdServerIP;
    private EditText mAdServerPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        miFashionIP = (EditText) findViewById(R.id.ip_address);
        miFashionPort = (EditText) findViewById(R.id.port);

        mAdServerIP = (EditText) findViewById(R.id.ad_server_ip_address);
        mAdServerPort = (EditText) findViewById(R.id.ad_server_port);

        String ip_address = getIntent().getExtras().getString("@string/ip_address", "");
        if (!ip_address.equals(""))
            miFashionIP.setText(ip_address);

        String port = getIntent().getExtras().getString("@string/port", "");
        if (!port.equals(""))
            miFashionPort.setText(port);

        String ad_server_ip_address = getIntent().getExtras().getString("@string/ad_server_ip_address", "");
        if (!ip_address.equals(""))
            mAdServerIP.setText(ad_server_ip_address);

        String ad_server_port = getIntent().getExtras().getString("@string/ad_server_port", "");
        if (!port.equals(""))
            mAdServerPort.setText(ad_server_port);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_settings, menu);
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
}
