/****************************************************\
 * MineStock Bukkit/Vault Plugin					*
 * Author: abujaki21							    *
 * Version: 0.4.9 Pre-alpha 140141					*
 * Description: Stock trading plugin for vault and	*
 * 	Bukkit-enabled minecraft servers				*
\****************************************************/

package io.github.abujaki.minestock;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MineStock extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null; //May be removed entirely. Not important, and throws NPEs
	protected TransactionEngine transactionEngine = new TransactionEngine();
	private ChatColor colError = ChatColor.RED, colRoutine = ChatColor.DARK_GREEN, colTest = ChatColor.DARK_AQUA;

	//Enabler and Disabler
	@Override
	public void onDisable() {
		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
		//transactionEngine.save(); will save unresolved orders to file 
	}

	@Override
	public void onEnable() {
		if (!setupEconomy() ) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		setupPermissions();
		//transactionEngine.load(); will load the unresolved orders from file
		//memoryCard.load(); will load the previous state of the stocks form file
		log.info("Setting up test values");

	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		//--------------------Start of Command Block--------------------\\
		//===================================================Buy Stocks
		if(command.getLabel().equalsIgnoreCase("stockbuy")){
			//stockbuy stock amount priceeach
			if(!(sender instanceof Player)){
				sender.sendMessage(colError + "This command requires you to be a logged in player");
				return true;
			}
			else{
				//Check to see if we have 3 arguments
				if (args.length >= 4) {
					sender.sendMessage(colError + "Too many arguments");
					return false;
				} 
				if (args.length <= 2) {
					sender.sendMessage(colError + "Not enough arguments");
					return false;
				}
				//Convert args 2 and 3 to integers
				try{
					int amt = Integer.parseInt(args[1]);
					int price = Integer.parseInt(args[2]);    	    	
					if ((amt <= 0)||(price <= 0)){ //Smartass check
						sender.sendMessage(colError + "You cannot buy with negative values.");
						return false;
					}
					else return buyStock(sender, args[0], amt, price);	//-----------Finally run the buystock code
				}
				catch(NumberFormatException e){
					return false; //arg 1 or 2 was not a number
				}
			}
		}
		//===================================================Sell Stocks
		else if(command.getLabel().equalsIgnoreCase("stocksell")){
			//stockbuy stock amount priceeach
			if(!(sender instanceof Player)){
				sender.sendMessage(colError + "This command requires you to be a logged in player");
				return true;
			}
			else{
				//Check to see if we have 3 arguments
				if (args.length >= 4) {
					sender.sendMessage(colError + "Too many arguments");
					return false;
				} 
				if (args.length <= 2) {
					sender.sendMessage(colError + "Not enough arguments");
					return false;
				}
				//Convert args 2 and 3 to integers
				try{
					int amt = Integer.parseInt(args[1]);
					int price = Integer.parseInt(args[2]);    	    	
					if ((amt <= 0)||(price <= 0)){ //Smartass check
						sender.sendMessage(colError + "You cannot sell with negative values.");
						return false;
					}
					else return sellStock(sender, args[0], amt, price); //-----------Finally run the sellstock code
				}
				catch(NumberFormatException e){
					return false; //arg 1 or 2 was not a number
				}
			}
		}
		//===================================================Cancel stock order
		//===================================================Launch Stock IPO
		else if(command.getLabel().equalsIgnoreCase("stocklaunchIPO")){
			//Stocks require a Friendly name, A code, and a controlling player
			//Launching requres a number of stocks to be made, a number to be sold, and an opening price
			if(args.length >= 7){ //too many arguments
				sender.sendMessage(colError + "Too many arguments");
				return false;
			}
			if(args.length <=5){ //Not enough arguments
				sender.sendMessage(colError + "Not enough arguments");
				return false;
			}
			try{
				//Make those magic numbers.
				String stockCode = args[0];
				String friendlyName = args[1];
				String plOwn = args[2];
				int numCreate = Integer.parseInt(args[3]);
				int numSell = Integer.parseInt(args[4]);
				float startingPrice = Float.parseFloat(args[5]);
				if(numCreate <= 0){//Smartass check
					sender.sendMessage(colError + "You can't create 0 or fewer stocks");
					return false;
				}
				if(numCreate <= numSell){//Other smartass check
					sender.sendMessage(colError + "You can't sell more stocks than exist");
					return false;
				}
				//Selling 0 stocks, or selling stocks for 0 is allowed.
				//But selling less than that is not
				if((numSell < 0) || (startingPrice < 0)){
					//Sell stock smartass check
					sender.sendMessage(colError + "You can't sell with negative values");
					return false;
				}
				//Check to make sure the receiving player is both:
				if((getServer().getPlayer(plOwn).isOnline()) && //Online
						(getServer().getPlayer(plOwn).getName() != sender.getName())){ //Not the broker

					//All is well.
					//Register the stock
					if(transactionEngine.registerStock(new Stock(stockCode, friendlyName, plOwn))){
						//Stock registered. Give stocks to owner
						transactionEngine.giveStock(plOwn, stockCode, numCreate);
						//Sell the stocks
						StockOrder ipo = new StockOrder(stockCode, numSell, startingPrice, plOwn);
						transactionEngine.match(ipo, false);
						//Notify Broker
						sender.sendMessage(colRoutine + "Stock " + stockCode + "(" + friendlyName + ") has been successfully registered.");
						sender.sendMessage(colRoutine + "" + numCreate + " stocks were created, and dispensed to " + plOwn);
						sender.sendMessage(colRoutine + "" + numSell + "/" + numCreate + "of those stocks are on the market for " + startingPrice + econ.currencyNamePlural() + " each.");
						//Notify controlling player
						getServer().getPlayer(plOwn).sendMessage(colRoutine + "Stock " + stockCode + "(" + friendlyName + ") has been successfully registered for you.");
						getServer().getPlayer(plOwn).sendMessage(colRoutine + "" + numCreate + " stocks were created, and dispensed to you by " + sender.getName());
						getServer().getPlayer(plOwn).sendMessage(colRoutine + "" + numSell + "/" + numCreate + "of those stocks have been put on the market for " + startingPrice + econ.currencyNamePlural() + " each.");
						return true;
					}
					else{
						sender.sendMessage(colError + stockCode + " is already taken. The stock was not registered");
						return false;
					}
				}
			}
			catch (NumberFormatException e){
				// arguments 4,5,6 were not numbers
				sender.sendMessage(colError + "Incorrect arguments");
				return false;
			}
		}
		//--------------------End of commands block--------------------\\
		return false;
	}

	private boolean buyStock(CommandSender sender, String stock, int amount, int priceEach){
		//function to place a stock order
		sender.sendMessage(colRoutine + "You wish to buy " + String.valueOf(amount) + " of " + stock + " stock at " + String.valueOf(priceEach) + ".");
		//If the user has enough money, deduct that much and place a stock order.
		if(econ.getBalance(sender.getName()) >= (amount * priceEach)){
			EconomyResponse r = econ.withdrawPlayer(sender.getName(), amount*priceEach);
			//Copypasta from example code
			if(r.transactionSuccess()) {
				sender.sendMessage(String.format(colRoutine + "You paid %s and now have %s", econ.format(r.amount), econ.format(r.balance)));
				transactionEngine.match(new StockOrder(stock, amount, priceEach, sender.getName()), true);
			} else {
				sender.sendMessage(String.format(colError + "An error occured: %s", r.errorMessage));
			}
		} else {
			sender.sendMessage(colError + "You don't have enough money to make that order");
		}
		sender.sendMessage(String.format(colTest + "You now have %s of %s stock", transactionEngine.checkStockAmount(sender.getName(), stock),stock));
		return true;
	}

	private boolean sellStock(CommandSender sender, String stock, int amount, int priceEach){
		//function to put stocks up for sale
		sender.sendMessage(colRoutine + "You wish to sell " + String.valueOf(amount) + " of " + stock + " stock at " + String.valueOf(priceEach) + ".");
		if(amount >= transactionEngine.checkStockAmount(sender.getName(), stock)){
			transactionEngine.match(new StockOrder(stock, amount, priceEach, sender.getName()), false);
		} else {
			sender.sendMessage(String.format(colError + "You don't have enough stocks to sell %s", amount));
			return false;
		}
		sender.sendMessage(String.format(colTest + "You now have %s of %s stock", transactionEngine.checkStockAmount(sender.getName(), stock),stock));
		return true;
	}

	//Vault Setup Block
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}
	//End of Vault Setups

	//Demo Vault code for Reference
	/*
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            log.info("Only players are supported for this Example Plugin, but you should not do this!!!");
            return true;
        }

        Player player = (Player) sender;

        if(command.getLabel().equals("test-economy")) {
            // Lets give the player 1.05 currency (note that SOME economic plugins require rounding!)
            sender.sendMessage(String.format("You have %s", econ.format(econ.getBalance(player.getName()))));
            EconomyResponse r = econ.depositPlayer(player.getName(), 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were given %s and now have %s", econ.format(r.amount), econ.format(r.balance)));
            } else {
                sender.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
            return true;
        } else if(command.getLabel().equals("test-permission")) {
            // Lets test if user has the node "example.plugin.awesome" to determine if they are awesome or just suck
            if(perms.has(player, "example.plugin.awesome")) {
                sender.sendMessage("You are awesome!");
            } else {
                sender.sendMessage("You suck!");
            }
            return true;
        } else {
            return false;
        }}
	 */
	//End of Demo code

}//End of Class
