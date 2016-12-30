package com.akjava.mbl3d.expression.client.datalist;

import java.util.HashMap;
import java.util.Map;

public  class Mbl3dData{
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		private long cdate;
		public long getCdate() {
			return cdate;
		}
		public void setCdate(long cdate) {
			this.cdate = cdate;
		}
		private int id;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		private String type;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Map<String, String> getValues() {
			return values;
		}
		public void setValues(Map<String, String> values) {
			this.values = values;
		}
		public Mbl3dData(){
			this("","","",new HashMap<String,String>());
		}
		public Mbl3dData(String name,String type, String description, Map<String, String> values) {
			super();
			this.name=name;
			this.type = type;
			this.description = description;
			this.values = values;
		}
		private String description;
		private Map<String,String> values;
		
		public String toString(){
			return name+","+type+","+description;
		}
	}