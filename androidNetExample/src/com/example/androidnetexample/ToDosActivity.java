package com.example.androidnetexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

public class ToDosActivity extends FragmentActivity {

	private FragmentManager fm;
	private String todosFragmentName = "TODOS_LIST_FRAGMENT";
	private Button addTodo;
	private boolean activityCreated;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.todos_activity);
		this.activityCreated = false;
		fm = getSupportFragmentManager();
		this.addTodo = (Button) findViewById(R.id.btn_add_todo);
		this.addTodo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent addTodoIntent = new Intent(getApplicationContext(), ToDoDetailActivity.class);
				addTodoIntent.putExtra("editToDo", false);
				startActivity(addTodoIntent);
				
			}
		});
	}
	
	
	public void refresh(){
		this.removeFragment();
		this.addFragment();
	}
	
	private void addFragment(){
		ToDosListFragment tDLFragment = new ToDosListFragment();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.todos_fragment_container,tDLFragment,  this.todosFragmentName).commit();
	}
	
	private void removeFragment(){
	ToDosListFragment tdlf = (ToDosListFragment) fm.findFragmentByTag(this.todosFragmentName);
	tdlf.finishFragment();
	fm.beginTransaction().remove(tdlf).commit();
	}
	
	@Override
	protected void onStop() {
		this.removeFragment();
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(this.activityCreated){
			this.addFragment();
		}else{
			this.activityCreated = true;
			this.activityCreatedWork();
		}
	}
	
	public void activityCreatedWork(){
		final ToDosActivity td = this;
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(200);
					while(!td.activityCreated){
						Thread.sleep(200);;
					}
					final ToDosActivity tr = td;
					Runnable uiChange = new Runnable() {
						
						@Override
						public void run() {
							tr.addFragment();
							
						}
					};
					runOnUiThread(uiChange);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}
