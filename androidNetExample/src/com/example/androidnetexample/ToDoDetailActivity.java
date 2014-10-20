package com.example.androidnetexample;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.example.androidnetexample.todo.parts.ToDo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ToDoDetailActivity extends Activity implements OnItemSelectedListener{
private boolean editTodo;
private boolean dataChanged;
private long originalId;
private int originalProgress;
private String originalName;
private EditText name;
private List<Integer> spinnerValues;
private int selectedSpinnerValue;

private void checkDataChanged(){
	if(!this.dataChanged){
		this.dataChanged = !(this.originalName.equals(this.name.getText().toString()));
	}
}

private void prepareSpinnerValues(){
	spinnerValues = new ArrayList<Integer>();
	for(int k=0; k<101; k++){
		this.spinnerValues.add(Integer.valueOf(k));
	}
}

@Override
public void onItemSelected(AdapterView<?> parent, View view, int position,
		long id) {
	if(Integer.valueOf(this.originalProgress) == parent.getItemAtPosition(position)){
		if(this.dataChanged){
			this.dataChanged = false;
			this.selectedSpinnerValue = ((Integer) parent.getItemAtPosition(position)).intValue();
		}
	}else{
		if(!this.dataChanged){
			this.dataChanged = true;	
		}
		this.selectedSpinnerValue = ((Integer) parent.getItemAtPosition(position)).intValue();
	}
	
}

@Override
public void onNothingSelected(AdapterView<?> parent) {
	//nothing to do
}

@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_detail_activity);
		prepareSpinnerValues();
		Bundle bndl= getIntent().getExtras();
		this.editTodo = bndl.getBoolean("editToDo");
		if(this.editTodo){
			this.originalId = bndl.getLong("toDoId");
			this.originalName = bndl.getString("toDoName");
			this.originalProgress = bndl.getInt("toDoProgress");
		}else{
			this.originalId = -1;
			this.originalName = "";
			this.originalProgress = 0;
		}
		this.dataChanged = false;
		this.name = (EditText) findViewById(R.id.txt_todo_name);
		Spinner progressSpinner = (Spinner) findViewById(R.id.spinner_todo_progress);
		ArrayAdapter<Integer> progressAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, this.spinnerValues);
		progressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		progressSpinner.setAdapter(progressAdapter);
		progressSpinner.setOnItemSelectedListener(this);
		progressSpinner.setSelection(this.originalProgress);
		this.name.setText(this.originalName);
	}

@Override
	public void onBackPressed() {
	checkDataChanged();
		if(this.editTodo){
		if(this.dataChanged){
			String name = this.name.getText().toString();
			ToDo td = new ToDo(name, this.selectedSpinnerValue, this.originalId);
			ToDoController.getInstance().updateToDo(td);
		}	
		}else{
		if(this.dataChanged){
			String name = this.name.getText().toString();
			JSONObject jSON = new ToDo(name, this.selectedSpinnerValue).toJSONObject();
			ToDoController.getInstance().addToDo(jSON);
		}
		}
		finish();
	}


}
