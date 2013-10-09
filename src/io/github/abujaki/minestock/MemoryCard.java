package io.github.abujaki.minestock;

import java.util.HashMap;

public class MemoryCard {
	private HashMap<String, Stock> stockList;
	private HashMap<String, Integer> ownedStock;
		
	MemoryCard(){
		 stockList = new HashMap<String, Stock>();
		 ownedStock = new HashMap<String, Integer>();
	}
	
	private String Pair(String player, String stock){
		return(stock.concat(".".concat(player)));
	}
	/*//Perhaps needed later for recovering information about the stocks
	private String DepairStock(String pair){
		//Oh Gods this is gonna be messy
		String exc[] = pair.split(".");
		return exc[0];
	}
	
	private String DepairPlayer(String pair){
		String exc[] = pair.split(".");
		return exc[1];
	}
	*/
	public void load(){
		//Load files into memory
	}
	public void save(){
		//Save files to memory
	}
	
	public void addStocks(String player, String stock, int amount){
		//Adds amount stock to player's account
		String key = Pair(player, stock);
		if(ownedStock.containsKey(key)){
			ownedStock.put(key, ownedStock.get(key) + amount);
		}
		else
			ownedStock.put(key, amount);
	}
	
	public boolean removeStocks(String player, String stock, int amount){
		//I think we get the point by now
		String key = Pair(player, stock);
		if(ownedStock.containsKey(key)){
			if(ownedStock.get(key) >= amount){
				//Remove the stock
				ownedStock.put(key, ownedStock.get(key) - amount);
			} else { //User doesn't have enough of the stock
				return false;
			}
		}else{ //User does not have the stock
			return false;
		}
		return true; // Everything went better than expected. :)
	}
	
	public boolean isStock(String check){
		if(stockList.containsKey(check)){
			return true;
		}
		return false;
	}
	public boolean registerStock(Stock stock){
		if(stockList.containsKey(stock.getCode())){
			//That code is already taken in the list. Reject
			return false;
		}
		stockList.put(stock.getCode(), stock);
		return true;
	}
	
	public int checkStockAmount(String player, String stock){
		if(ownedStock.containsKey(Pair(player, stock))){
			return ownedStock.get(Pair(player,stock));
		}
		return 0;
	}
}