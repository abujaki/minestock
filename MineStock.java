/****************************************************\
 * MineStock Bukkit/Vault Plugin					*
 * Author: Andrew Bujáki							*
 * Version: 0.0.1									*
 * Description: Stock trading plugin for vault and	*
 * 	Bukkit-enabled minecraft servers				*
\****************************************************/

package io.github.abujaki.minestock;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
//import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MineStock extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;

//Enabler and Disabler
    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
    }
 
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
    	if(command.getLabel().equalsIgnoreCase("stockbuy")){
    		if(!(sender instanceof Player)){
    			sender.sendMessage("This command requires you to be a logged in player");
    			return true;
    		}
    		else return buystock();
    	}else if(command.getLabel().equalsIgnoreCase("stocksell")){
    		if(!(sender instanceof Player)){
    			sender.sendMessage("This command requires you to be a logged in player");
    			return true;
    		}
    		else{ return sellstock();}
    	}else if(command.getLabel().equalsIgnoreCase("stockcheck")){
    		if(args.equals(null)){
    			return checkstock();
    		}else{
    			return checkstock(args[1]);
    		}
    	}
    	return false;
    }

    private boolean buystock(){
    	//function to place a stock order
    	
    	return true;
    }
    
    private boolean sellstock(){
    	//function to put stocks up for sale
    	return true;
    }
    
    private boolean checkstock(String stock){
    	//Print out stock info to commandsender
    	return true;
    }
    private boolean checkstock(){
    	//Print out ticker to commandsender
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

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
//End of Vault Setups
    
//Demo Vault code for Reference and Lolz
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
