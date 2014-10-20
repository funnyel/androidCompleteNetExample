package com.example.androidnetexample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CalculatorActivity extends Activity {

	String type;
	EditText aNumberET;
	EditText bNumberET;
	TextView calcResult;
	int serverPort;
	
	private void setCalculationType(String s){
		if (!this.type.equals(s)){
			this.type = s;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculator_activity);
		type = "";
		serverPort = 8118;
		aNumberET = (EditText) findViewById(R.id.txt_number_a);
		bNumberET = (EditText) findViewById(R.id.txt_number_b);
		calcResult = (TextView) findViewById(R.id.lbl_calculator_result);
		Button addCompute = (Button) findViewById(R.id.btn_add);
		Button substractCompute = (Button) findViewById(R.id.btn_substract);
		Button multiplyCompute = (Button) findViewById(R.id.btn_multiply);
		Button divideCompute = (Button) findViewById(R.id.btn_divide);
		
		addCompute.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setCalculationType("Add");
				
			}
		});
		
		substractCompute.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setCalculationType("Substract");
				
			}
		});
		
		multiplyCompute.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				setCalculationType("Multiply");
		
			}
		});
		
		divideCompute.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				setCalculationType("Divide");
		
			}
		});
		Button netCompute = (Button) findViewById(R.id.btn_calculate);
		netCompute.setOnClickListener(calculateOnClickListener);
	}
	
	private boolean selectedType(){
		return !("".equals(type));
	}
	private boolean correctNumber(EditText t){
		boolean correct = false;
		try {
			Double.parseDouble(t.getText().toString().trim());
			correct = true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return correct;
	}
	
	private String prepareData(Context context){
		String result = "";
		StringBuffer bf = new StringBuffer();
		if(selectedType()){
			bf.append(type);
			bf.append(' ');
			if(correctNumber(aNumberET)){
				double a = Double.parseDouble(aNumberET.getText().toString().trim());
				bf.append(a);
				bf.append(' ');
				if(correctNumber(bNumberET)){
					double b = Double.parseDouble(bNumberET.getText().toString().trim());
					bf.append(b);
					bf.append("\n");
					if(("Divide".equals(type))&&(b==0.0)&&(a!=b)){
						Toast.makeText(context, "Can not divide non zero number by zero.", Toast.LENGTH_LONG).show();
						return result;
					}else{
						result = bf.toString();
						return result;	
					}
				}else{
					Toast.makeText(context, "Number 2 in wrong format. See example above.", Toast.LENGTH_LONG).show();
					return result;
				}
			}else{
				Toast.makeText(context, "Number 1 in wrong format. See example above.", Toast.LENGTH_LONG).show();
				return result;
			}
		}else{
			Toast.makeText(context, "No method selected, please select method for computing stated numbers.", Toast.LENGTH_LONG).show();
			return result;
		}
		
	}
	
	OnClickListener calculateOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String input = prepareData(v.getContext());
			if(!"".equals(input)){
				CalculateAsyncTask cat = new CalculateAsyncTask("192.168.0.101", serverPort);
				cat.execute(input);	
			}
			
		}
	};
	
	public class CalculateAsyncTask extends AsyncTask<String, Void, String>{

		private int port;
		private String loc;
		
		public CalculateAsyncTask (String locat, int locPort){
			this.loc = locat;
			this.port = locPort;
		}
		
		@Override
		protected String doInBackground(String... params) {
			Socket s = null;
			BufferedReader br;
			OutputStream os;
			String result ="";
			try {
				s = new Socket(loc, port);
				br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				os = s.getOutputStream();
				os.write(("Compute\n").getBytes());
				os.write(params[0].getBytes());
				os.flush();
				String accepted = br.readLine().trim();
				if("OkCompute".equals(accepted)){
					Scanner scan = new Scanner(br.readLine());
					String resp = scan.next();
					if("Result".equals(resp)){
						result = scan.next();
						os.write("OkResult\n".getBytes());
						os.flush();
						if("Disconnecting".equals(br.readLine())){
							os.close();
							br.close();
						}
					}
					scan.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(s!=null){
					try {
						if(!s.isClosed()){
							s.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
			
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			calcResult.setText(result);
			super.onPostExecute(result);
		}
	}
	
}
