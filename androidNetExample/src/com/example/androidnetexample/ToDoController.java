package com.example.androidnetexample;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Application;
import android.os.AsyncTask;

import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidnetexample.todo.parts.ToDo;

public class ToDoController extends Application {

	private static ToDoController myInstance;
	private RequestQueue myRequestQueue;
	private List<ToDo> todosList;
	private String ip;
	private int port;
	private String restApiToDo;
	private String idAddition;
	private String httpPrefix;
	private List<CustomAdapter> listeners;
	
	@Override
	public void onCreate() {
		super.onCreate();
		myInstance = this;
		this.myRequestQueue = null;
		this.todosList = new LinkedList<ToDo>();
		listeners = new LinkedList<CustomAdapter>();
		this.restApiToDo = "/api/todo";
		this.idAddition = "/";
		this.ip = "192.168.0.119";
		this.port = 9000;
		this.httpPrefix = "http://";		
	}
	
	
	public RequestQueue getRequestQueue(){
		if(this.myRequestQueue == null){
		this.myRequestQueue = Volley.newRequestQueue(getApplicationContext());	
		}
		return this.myRequestQueue;
	}
	
	public static synchronized ToDoController getInstance(){
		return myInstance;
	}
	
	private String getTodoGlobalAddress (){
		StringBuilder build = new StringBuilder();
		build.append(this.httpPrefix);
		build.append(this.ip);
		build.append(":");
		build.append(this.port);
		build.append(this.restApiToDo);
		return build.toString();
	}
	
	private String getToDoAddressById (long id){
		StringBuilder build = new StringBuilder(getTodoGlobalAddress());
		build.append(this.idAddition);
		build.append(id);
		return build.toString();
	}
	
	public void addToDo(JSONObject json){
		AddToDoTask atdt = new AddToDoTask();
		atdt.execute(json);
	}
	
	public void removeToDo(long id){
	RemoveToDoTask rtdt = new RemoveToDoTask();
	rtdt.execute(new Long(id));
	}
	
	public void updateToDo(ToDo tod){
	UpdateToDoTask utdt = new UpdateToDoTask();
	utdt.execute(tod);
	}
	
	public void getTodos(){
		LoadToDosTask ltdt = new LoadToDosTask();
		ltdt.execute();
	}
		
	public void removeToDoListener(CustomAdapter listen){
		listeners.remove(listen);
	}
	
	public void addToDoListener(CustomAdapter ca){
		listeners.add(ca);
	}
	
	private void notifyAllListeners(){
		for(int h=0; h<this.listeners.size(); h++){
			this.listeners.get(h).notifyDataChanged();
		}
	}
	
	public LinkedList<ToDo> getNewData(){
		LinkedList<ToDo> result = new LinkedList<ToDo>();
		for(int k=0; k<this.todosList.size(); k++){
			result.add(todosList.get(k));
		}
		return result;
	}
		private class LoadToDosTask extends AsyncTask<Void, Void, Void> {
						
			@Override
			protected Void doInBackground(Void... params) {
				final ToDoController tdc = ToDoController.getInstance();
				String url = tdc.getTodoGlobalAddress();
				JsonArrayRequest jSonArrReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray jArr) {
					try {
					LinkedList<ToDo> newData = new LinkedList<ToDo>();
					for(int k=0; k<jArr.length(); k++){
						JSONObject jObj = jArr.getJSONObject(k);
						ToDo newTodo = ToDo.getToDoFromJSONObject(jObj);
						if(null!=newTodo){
							newData.add(newTodo);
						}
					}	
					tdc.todosList.clear();
					for(int k=0; k<newData.size(); k++){
						tdc.todosList.add(newData.get(k));
					}
					tdc.notifyAllListeners();
					} catch (Exception errExc) {
						errExc.printStackTrace();
					}
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError eo) {
						eo.printStackTrace();
						
					}
				});
				tdc.getRequestQueue().add(jSonArrReq);
				return null;
			}
			
		}
		
		private class UpdateToDoTask extends AsyncTask<ToDo, Void, Void>{

			@Override
			protected Void doInBackground(ToDo... params) {
				ToDo td = params[0];
				final ToDoController tdc = ToDoController.getInstance();
				String url = tdc.getToDoAddressById(td.getId());
				JSONObject data = td.toJSONObject();
				JsonObjectRequest jSonRq = new JsonObjectRequest(Request.Method.PUT, url, data, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							String result = response.getString("status");
							if("OK".equals(result)){
								tdc.getTodos();
							}
						} catch (Exception er) {
							er.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
					}
				});
				tdc.getRequestQueue().add(jSonRq);
				return null;
			}
			
		}
		
		private class RemoveToDoTask extends AsyncTask<Long, Void, Void>{

			@Override
			protected Void doInBackground(Long... params) {
				final ToDoController tdc = ToDoController.getInstance();
				long value = params[0].longValue();
				String url = tdc.getToDoAddressById(value);
				JsonObjectRequest jSRq = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject resp) {
						try {
							String result = resp.getString("status");
							if("OK".equals(result)){
								tdc.getTodos();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError err) {
						err.printStackTrace();
						
					}
				});
					tdc.getRequestQueue().add(jSRq);
				return null;
			}
			
		}
		
		private class AddToDoTask extends AsyncTask<JSONObject, Void, Void>{

			@Override
			protected Void doInBackground(JSONObject... params) {
				JSONObject input = params[0];
				final ToDoController tdc = ToDoController.getInstance();
				String url = tdc.getTodoGlobalAddress();
				JsonObjectRequest jSRq = new JsonObjectRequest(Request.Method.POST, url, input, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject arg0) {
						try {
							JSONObject obj= arg0.getJSONObject("data");
							String name = obj.getString("title");
							long id = obj.getLong("id");
							int progresss = obj.getInt("progress");
							ToDo td = new ToDo(name, progresss, id);
							tdc.todosList.add(td);
							tdc.notifyAllListeners();
						} catch (Exception eo) {
							eo.printStackTrace();
						}
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						arg0.printStackTrace();
					}
				});
				tdc.getRequestQueue().add(jSRq);
				return null;
			}
			
		} 
}
