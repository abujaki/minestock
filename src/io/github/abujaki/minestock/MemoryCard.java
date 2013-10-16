package io.github.abujaki.minestock;

import java.util.HashMap;


public class MemoryCard {
	private HashMap<String, Stock> stockList;
	private HashMap<String, Integer> ownedStock;

	MemoryCard(){
		stockList = new HashMap<String, Stock>();
		ownedStock = new HashMap<String, Integer>();
	}

	private String pair(String player, String stock){
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
	public boolean moveStocks(String fromPlayer, String toPlayer, String stock, int amount){
		String frm = pair(fromPlayer, stock);
		String to = pair(toPlayer, stock);
		if(ownedStock.containsKey(frm)){//Check to see if they own any stocks of that kind
			if(ownedStock.get(frm) >= amount){//Check to see if they own enough
				//Remove the stock
				ownedStock.put(frm, ownedStock.get(frm) - amount);
				if(ownedStock.containsKey(to)){
					ownedStock.put(to, ownedStock.get(to) + amount);
				}
				else{
					ownedStock.put(to, amount);
				}
				return true;
			}
		}
		return false; //From player didn't have enough stocks, that scoundral. >:(
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
		if(ownedStock.containsKey(pair(player, stock))){
			return ownedStock.get(pair(player,stock));
		}
		return 0;
	}
	
	public void giveStock(String player, String stock, int amount){
		ownedStock.put(pair(player, stock), amount);
	}
}