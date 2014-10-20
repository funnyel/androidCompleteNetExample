package com.example.androidnetexample;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends Activity{
	
private EditText searchText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		searchText = (EditText) findViewById(R.id.search_txt);
		Button search = (Button) findViewById(R.id.btn_search);
		search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String entryText = searchText.getText().toString();
				if(!"".equals(entryText)){
					try {
						Intent searchingIntent = new Intent(Intent.ACTION_WEB_SEARCH);
						searchingIntent.putExtra(SearchManager.QUERY, entryText);
						startActivity(searchingIntent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}
		});
	}
	
}
