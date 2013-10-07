package io.github.abujaki.minestock;

import java.util.HashMap;

public class MemoryCard {
	private HashMap<String, Stock> stockList;
		
	MemoryCard(){
		 stockList = new HashMap<String, Stock>();
	}
	
	public void load(){
		//Load files into memory
	}
	public void save(){
		//Save files to memory
	}
	
	public void addStocks(String player, String stock, int amount){
		//Adds amount stock to player's account
	}
	
	public void removeStocks(String player, String stock, int amount){
		//I think we get the point by now
		
	}
	
	public boolean isStock(String check){
		if(stockList.containsKey(check)){
			return true;
		}
		return false;
	}
}
