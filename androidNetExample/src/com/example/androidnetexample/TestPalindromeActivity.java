package com.example.androidnetexample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TestPalindromeActivity extends Activity {

	private EditText palindromeEditText;
	private TextView palindromeResultTextView;
	private int serverPort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_palindrome_activity);
		serverPort = 8118;
		this.palindromeEditText = (EditText) findViewById(R.id.txt_input_palindrome);
		this.palindromeResultTextView = (TextView) findViewById(R.id.lbl_palindrome_test_result);
		Button testPalindrome = (Button) findViewById(R.id.btn_test_palindrome);
		testPalindrome.setOnClickListener(testPalindromeOnClickListener);
		
	}
	
	OnClickListener testPalindromeOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String palindrome = palindromeEditText.getText().toString().trim();
			if(!"".equals(palindrome)){
				TestPalindromeAsyncTask tpAsyncTask = new TestPalindromeAsyncTask("192.168.0.101", serverPort);
				tpAsyncTask.execute(palindrome+"\n");
			}
			else{
				Toast.makeText(v.getContext(),"Empty String can not be verified as Palindrome\nEnter string containing characters different from \' \'", Toast.LENGTH_LONG).show();
			}
			
		}
	};
	
	public class TestPalindromeAsyncTask extends AsyncTask<String, Void, String>{
		private String loc;
		private int srvPort;
		public TestPalindromeAsyncTask(String location, int port){
			this.loc = location;
			this.srvPort = port;
		}
		@Override
		protected String doInBackground(String... params) {
			Socket comm = null;
			BufferedReader br;
			OutputStream os;
			String result = "";
			try {
				comm = new Socket(this.loc, this.srvPort);
				br = new BufferedReader(new InputStreamReader(comm.getInputStream()));
				os = comm.getOutputStream();
				os.write(("TestPalindrome\n").getBytes());
				os.write(params[0].getBytes());
				os.flush();
				String accepted = br.readLine().trim();
				if("OkTestPalindrome".equals(accepted)){
					Scanner scan = new Scanner(br.readLine());
					String res = scan.next();
					if("Result".equals(res)){
						result = scan.next();
						os.write(("OkResult\n").getBytes());
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
			} finally{
				if(comm !=null){
					try {
						if(!comm.isClosed()){						
						comm.close();
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
			palindromeResultTextView.setText(result);
			super.onPostExecute(result);
		}
		
	}
}
