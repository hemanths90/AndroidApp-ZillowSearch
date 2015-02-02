package com.example.zillowsearch;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

	Spinner spnr;
	Button mSearch;
	EditText mAddress, mCity;
	String itemState;
    String text, phpData;
	String address, city, state;
	
	String[] StateDropdown = {	      	
			"Choose State",
			"AL",
			"AK",
			"AZ",
			"AR",
			"CA",
			"CO",
			"CT",
			"DC",
			"FL",
			"GA",
			"HI",
			"ID",
			"IL",
			"IN",
			"IA",
			"KS",
			"KY",
			"LA",
			"ME",
			"MD",
			"MA",
			"MI",
			"MN",
			"MS",
			"MO",
			"MT",
			"NE",
			"NV",
			"NH",
			"NJ",
			"NM",
			"NY",
			"NC",
			"ND",
			"OH",
			"OK",
			"OR",
			"PA",
			"RI",
			"SC",
			"SD",
			"TN",
			"TX",
			"UT",
			"VT",
			"VA",
			"WA",
			"WV",
			"WI",
			"WY"
	  	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        spnr = (Spinner)findViewById(R.id.state);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, StateDropdown);
        spnr.setAdapter(adapter);
        spnr.setOnItemSelectedListener(
                  new AdapterView.OnItemSelectedListener() {
                      @Override
                      public void onItemSelected(AdapterView<?> arg0, View arg1,
                              int arg2, long arg3) {
                        int position = spnr.getSelectedItemPosition();
                        itemState = StateDropdown[position];
                      }
                      @Override
                      public void onNothingSelected(AdapterView<?> arg0) {
                      }
                  }
              );
        
        
        mSearch = (Button)findViewById(R.id.submit);
        mAddress  = (EditText)findViewById(R.id.streetAddress);
        mCity = (EditText)findViewById(R.id.city);

        mSearch.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                	try {

                	address = mAddress.getText().toString();
                	city = mCity.getText().toString();
                	state = itemState;
                    Log.v("Address:", mAddress.getText().toString());
                    Log.v("City:", mCity.getText().toString());
                    Log.v("State:", itemState);
                    
                    boolean errFlag = errorHandlingFunc();             

                    	if(!errFlag){
                    		String url = "http://hemanths90webhw8-env.elasticbeanstalk.com/ServerSideSearch.php?StreetAddress="+URLEncoder.encode(mAddress.getText().toString(), "UTF-8")+"&City="+URLEncoder.encode(mCity.getText().toString(), "UTF-8")+"&State="+URLEncoder.encode(itemState, "UTF-8");
                    		TaskZillow t = new TaskZillow();
                    		t.execute(url);
                    	}
                	}
                	catch(Exception e) {
            			e.printStackTrace();                		
                	}
                    
                }
            });
        
        ImageView img = (ImageView)findViewById(R.id.zillowLogoIcon);
        img.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.zillow.com/"));
                startActivity(intent);
            }
        });
        
    }
    
    
    protected boolean errorHandlingFunc() {
    	boolean eFlag = false;
    	
    	TextView errorAddress = (TextView)findViewById(R.id.errorAddress);
    	TextView errorCity = (TextView)findViewById(R.id.errorCity);
    	TextView errorState = (TextView)findViewById(R.id.errorState);
    	
    	errorAddress.setVisibility(TextView.INVISIBLE);
    	errorCity.setVisibility(TextView.INVISIBLE);
    	errorState.setVisibility(TextView.INVISIBLE);    	

    	if(address.isEmpty()) {
    		errorAddress.setVisibility(TextView.VISIBLE);
    		eFlag = true;
    	}
    	if(city.isEmpty()) {
    		errorCity.setVisibility(TextView.VISIBLE);
    		eFlag = true;
    	}
    	if(state == "Choose State") {
    		errorState.setVisibility(TextView.VISIBLE);
    		eFlag = true;
    	}
    	
    	return eFlag;
    		
    }
    
    
    
    
    
    //Async Task
    class TaskZillow extends AsyncTask<String,String,String>
    {

		protected void onPostExecute(String jsonText) {
		try{
			Log.v("json sent:", jsonText);
	    	TextView ErrorMsg = (TextView)findViewById(R.id.ErrorMsg);	    	
	    	ErrorMsg.setVisibility(TextView.INVISIBLE);

	        if(jsonText.trim().equals("508") || jsonText.trim().equals("1") || jsonText.trim().equals("2") || jsonText.trim().equals("3") || jsonText.trim().equals("4") || jsonText.trim().equals("500") || jsonText.trim().equals("501") || jsonText.trim().equals("502") || jsonText.trim().equals("503") || jsonText.trim().equals("504") || jsonText.trim().equals("505") || jsonText.trim().equals("506") || jsonText.trim().equals("507")){
	        	Log.v("json sent:", "inside if");
	        	ErrorMsg.setVisibility(TextView.VISIBLE);
	        }
	        else {
	        	Log.v("json sent:", "inside else");
				Intent childIntent  = new Intent(MainActivity.this,ResultActivity.class);     
				childIntent.putExtra("ResultString", jsonText);   
				startActivity(childIntent);
	        }
		} catch(Exception e) {
			Toast.makeText(getApplicationContext(), "Cannot connect to internet", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		}
		
		@Override
		protected void onPreExecute() {
		    // TODO Auto-generated method stub
		    super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... url) {
        	try {
        	String posturl = url[0];
        	Log.v("postURL: ", posturl);

        	HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(posturl);  
     
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                Log.v("postURL: ", "response");
                
                InputStream is = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            	StringBuilder stringBuild = new StringBuilder();
            	String line = null;
            	while ((line = reader.readLine()) != null) {
            		stringBuild.append(line + "\n");
            	}
            	is.close();
            	phpData = stringBuild.toString();
            	Log.v("json data",phpData);
            	//mAddress.setText(json);
            	
            	
            }		
    		
    		catch (Exception e) {
    			// TODO Auto-generated catch block
    			Log.v("logger",e.toString());
    			e.printStackTrace();
    		}
			return phpData;

            
		
		}
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
