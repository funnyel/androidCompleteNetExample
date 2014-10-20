package com.example.androidnetexample;

import java.util.LinkedList;

import com.example.androidnetexample.todo.parts.ToDo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<ToDo> {

	private Context appContext;
	private LinkedList<ToDo> datalist;
	private int resourceViewId;
	
	public CustomAdapter(Context context, int resource, LinkedList<ToDo> objects) {
		super(context, resource, objects);
		this.appContext = context;
		this.resourceViewId = resource;
		this.datalist = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View work = convertView;
		ViewHolder holder;
		
		if(work == null){
			
			LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			work = inflater.inflate(this.resourceViewId, null);
			
			holder = new ViewHolder();
			holder.name = (TextView) work.findViewById(R.id.todo_name_lbl);
			holder.progress = (ProgressBar) work.findViewById(R.id.todo_progress_progressbar);
			
			work.setTag(holder);
		}else{
			holder = (ViewHolder) work.getTag();
		}
		ToDo item = (ToDo) datalist.get(position);
		
		holder.name.setText(item.getName());
		holder.progress.setProgress(item.getProgress());
		
		return work;
		
	}

	protected static class ViewHolder {
		protected TextView name;
		protected ProgressBar progress;
	}

	public void notifyDataChanged(){
		Log.i("CustomAdapter", "notified");
		super.clear();
		LinkedList<ToDo> newData = ToDoController.getInstance().getNewData();
		Log.i("CustomAdapter", "newDataGot");
		for(int r=0; r<newData.size(); r++){
			super.add(newData.get(r));
		}
		this.datalist = newData;
		super.notifyDataSetChanged();
	}
	
}
