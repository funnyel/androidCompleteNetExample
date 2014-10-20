package com.example.androidnetexample.todo.parts;

import org.json.JSONException;
import org.json.JSONObject;

public class ToDo {

	private String name;
	private int progress;
	private long id;
	
	public ToDo (String nm, int prog){
		this.name = nm;
		this.progress = prog;
		this.id = -1;
	}
	
	public ToDo(String nam, int progr, long id){
		this.name = nam;
		this.id = id;
		this.progress = progr;
	}
	
	public String getName (){
		return this.name;
	}
	public int getProgress(){
		return this.progress;
	}
	public long getId(){
		return this.id;
	}
	
	public JSONObject toJSONObject(){
		JSONObject resultObject = new JSONObject();
		try {
			if(getId()!=-1){
				resultObject.put("id", this.getId());
			}
			resultObject.put("title", this.getName());
			resultObject.put("progress", this.getProgress());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultObject;
	}
	
	public static ToDo getToDoFromJSONObject(JSONObject jsonObj){
		ToDo resultToDo = null;
		try {
			JSONObject json = jsonObj;
			long id = json.getLong("id");
			int progress = json.getInt("progress");
			String name = json.getString("title");
			resultToDo = new ToDo(name, progress, id);
		} catch (Exception e) {
			e.printStackTrace();
			resultToDo = null;
		}
		return resultToDo;
	}
}
