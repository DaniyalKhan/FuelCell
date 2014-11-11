package com.fuelcell.ui;

public class DrawerItem {
	public static enum DrawerItemType {Header, Item, MainHeader};
	
	public String header;
	public String info;
	public DrawerItemType type;
	
	public DrawerItem(String header, DrawerItemType type){
		this.header = header;
		this.type = type;
	}
	public DrawerItem(String header, String info, DrawerItemType type){
		this.header = header;
		this.info = info;
		this.type = type;
	}
}