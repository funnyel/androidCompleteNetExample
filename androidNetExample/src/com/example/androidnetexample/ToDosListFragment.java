package com.example.androidnetexample;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.example.androidnetexample.todo.parts.ToDo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class ToDosListFragment extends ListFragment implements OnItemLongClickListener {
	
	private CustomAdapter dataAdapter;
	private ListView lv;
	private LinkedList<ToDo> data;
	private ToDoController tdc;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View k = super.onCreateView(inflater, container, savedInstanceState);
		tdc = ToDoController.getInstance();
		data = new LinkedList<ToDo>();
		this.dataAdapter = new CustomAdapter(this.getActivity(), R.layout.todo_list_item, data);
		tdc.addToDoListener(dataAdapter);
		setListAdapter(dataAdapter);
		tdc.getTodos();
		return k;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.lv = this.getListView();
		this.lv.setOnItemLongClickListener(this);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent editIntent = new Intent(getActivity().getApplicationContext(), ToDoDetailActivity.class);
		ToDo td = (ToDo) this.getListAdapter().getItem(position);
		editIntent.putExtra("editToDo", true);
		editIntent.putExtra("toDoId", td.getId());
		editIntent.putExtra("toDoName", td.getName());
		editIntent.putExtra("toDoProgress", td.getProgress());
		startActivity(editIntent);
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		ToDo td = (ToDo) this.getListAdapter().getItem(position);
		tdc.removeToDo(td.getId());
		( (ToDosActivity) this.getActivity() ).refresh();
		return true;
	}
	
	public void finishFragment(){
		tdc.removeToDoListener(this.dataAdapter);
	}
	
	
}
