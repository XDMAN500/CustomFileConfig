/*
 * Copyright (c) 2018 Varmetek - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.varmetek.proj.demo;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommandConfig implements CommandExecutor
{
  private static final String CONFIG_OPTIONS =  "'xml','gson', 'hjson', 'yaml', 'hocon'";
  private static final Pattern VALID_INPUT = Pattern.compile("^([A-Za-z0-9]|_|\\.)+$");

  private CustomFileConfigurationPlugin plugin;
  private ConfigDemoHelper demoHelper;


  public CommandConfig(CustomFileConfigurationPlugin plugin){
    this.plugin = plugin;
    this.demoHelper = new ConfigDemoHelper();
    plugin.getCommand("config").setExecutor(this);
  }

  @Override
  public boolean onCommand (CommandSender commandSender, Command command, String s, String[] args){
    if(args.length == 0){
      commandSender.sendMessage(goodText(" Welcome to the CustomConfig Demo! Do /config help for more commands"));
      return true;
    }
   String root = args[0].toLowerCase();
    switch (root){
      case "help":{
        commandSender.sendMessage(goodText(" Here are some useful commands"));
        commandSender.sendMessage("\n");
        commandSender.sendMessage(color("&7 /config select <type>  &a- select a config type"));
        commandSender.sendMessage(color("&7 /config load  &a- load a config type"));
        commandSender.sendMessage(color("&7 /config save &a- save current modifications"));
        commandSender.sendMessage(color("&7 /config reset &a- reset file to its original state"));
        commandSender.sendMessage(color("&7 /config info &a- display information about a config type"));
        commandSender.sendMessage(color("&7 /config edit <path> &a- make various edits to the config file"));
        commandSender.sendMessage(color("&7 /config reload &a- reload file"));
        commandSender.sendMessage(color("&7 /config  copyto &a - copy current config in another format"));
        commandSender.sendMessage(color("&7 /config  copyfrom &a - copy selected data to currently selected format"));
        return false;
      }
      case "edit":{

        return handleEditCommand(commandSender,  s, Arrays.copyOfRange(args,1,args.length));
      }
      case "select":{
          if(args.length <= 1){
            commandSender.sendMessage(goodText(
              " Use this command to select a config type. Try these: "+CONFIG_OPTIONS));
          }else{
            String choice = args[1].toLowerCase();
            switch (choice){
              case "yml":
              case "yaml":{demoHelper.selectYaml(); }break;
              case  "hjson":{demoHelper.selectHjson();}break;
              case "gson":{ demoHelper.selectGson();  }break;
              case "hocon":{ demoHelper.selectHocon();  }break;
              case "xml":{ demoHelper.selectXml();  }break;
              default:{
                commandSender.sendMessage(color("&c Unrecognized config type. Try these: "+CONFIG_OPTIONS));
                return false;
              }
            }
            commandSender.sendMessage(goodText(" "+demoHelper.getConfigType().getName()+" config type selected"));
          }
        return true;
      }

      case "save":{
        if(!demoHelper.isActive()){
          commandSender.sendMessage(badText(
            " A config type must be loaded first"));
          return false;
        }

        commandSender.sendMessage(goodText( " Attempting to save "+  demoHelper.getConfigType().getFileResource()));
        try {

          demoHelper.saveConfig();
          commandSender.sendMessage(goodText( " " + demoHelper.getConfigType().getFileResource()+ " saved successfully"));

        } catch (IOException e) {
          commandSender.sendMessage(badText(" An error occurred when saving " + demoHelper.getConfigType().getFileResource()+ " : "+ e.getMessage()));
          e.printStackTrace();
          return false;
        }

          return true;
        }

      case "load":{
        if(!demoHelper.hasChosen()){
          commandSender.sendMessage(color(
            "&c A config type must be selected first"));
          return false;
        }

        commandSender.sendMessage(ChatColor.GREEN+ " Attempting to load "+  demoHelper.getConfigType().getFileResource());
        try {

          demoHelper.loadConfig();
          commandSender.sendMessage(ChatColor.GREEN+ " " + demoHelper.getConfigType().getFileResource()+ " loaded successfully");

        } catch (IOException e) {
          commandSender.sendMessage(ChatColor.RED+ " An error occurred when loading " + demoHelper.getConfigType().getFileResource()+ " : "+ e.getMessage());
          e.printStackTrace();
          return false;
        } catch (InvalidConfigurationException e) {
          commandSender.sendMessage(badText(" "+ demoHelper.getConfigType().getFileResource() + " could not be parsed into "+ demoHelper.getConfigType().getName()));
          e.printStackTrace();
          return false;
        }

        return true;
      }
      case "reload":{
        if(!demoHelper.isActive()){
          commandSender.sendMessage(color(
            "&c A config type must be loaded first"));
          return false;
        }

        commandSender.sendMessage(ChatColor.GREEN+ " Attempting to reload "+  demoHelper.getConfigType().getFileResource());

        try {

          demoHelper.saveConfig();


        } catch (IOException e) {
          commandSender.sendMessage(ChatColor.RED+ " An error occurred when saving " + demoHelper.getConfigType().getFileResource()+ " : "+ e.getMessage());
          e.printStackTrace();
          return false;
        }
        commandSender.sendMessage(ChatColor.GREEN+ " " + demoHelper.getConfigType().getFileResource()+ " reloaded successfully");
        return true;
      }
      case "reset":{
        if(!demoHelper.hasChosen()){
          commandSender.sendMessage(color(
            "&c A config type must be selected first"));
          return false;
        }
        demoHelper.resetConfig();
        commandSender.sendMessage(color("&aFile "+  demoHelper.getConfigType().getFileResource() +" has been restored to its original state."));
        return true;
      }
      case "info":{
        if(demoHelper.hasChosen()){
          ConfigurationType type = demoHelper.getConfigType();
          commandSender.sendMessage(ChatColor.GREEN+ " ConfigType: "+ ChatColor.GRAY + type.getName() );
          commandSender.sendMessage(ChatColor.GREEN+ " File: "+ ChatColor.GRAY + type.getFileResource() );
          commandSender.sendMessage(ChatColor.GREEN+ " Description: "+ ChatColor.GRAY + type.getDescription() );
          commandSender.sendMessage(ChatColor.GREEN+ " Is Loaded: "+ ChatColor.GRAY + demoHelper.isConfigLoaded() );
        }else{
          commandSender.sendMessage(color(
            "&c A config type must be loaded first"));
          return false;
        }
        return true;
      }
      case "copyto":{
        if(args.length <= 1){
          commandSender.sendMessage(goodText(
            " Use this command to select a config type. Try these: " +CONFIG_OPTIONS));
        }else {
          String select = args[1].toLowerCase();
          ConfigurationType selType;
          switch (select) {
            case "yml":
            case "yaml": {
              selType = ConfigurationType.YAML;
            }
            break;
            case "hjson": {
              selType = ConfigurationType.HJSON;
            }
            break;
            case "gson": {
              selType = ConfigurationType.GSON;
            }
            break;
            case "hocon": {
              selType = ConfigurationType.HOCON;
            }
            break;

            case "xml":{
              selType = ConfigurationType.XML;
            }
            default: {
              commandSender.sendMessage(color("&c Unrecognized config type. Try these: "+CONFIG_OPTIONS));
              return false;
            }
          }


          commandSender.sendMessage(goodText(" Attempting copy to " + selType.getName()));

          try {
            FileConfiguration conf = demoHelper.copyTo(selType);
            try {
              selType.saveConfigurationFile(conf);
              commandSender.sendMessage(goodText("Copy complete"));
            } catch (IOException e) {
              commandSender.sendMessage(badText(" Could not save changes: " + e.getMessage()));
              e.printStackTrace();
            }

          } catch (IOException e) {
            commandSender.sendMessage(badText(" Could not complete transfer: " + e.getMessage()));
            e.printStackTrace();
            return false;
          } catch (InvalidConfigurationException e) {
            commandSender.sendMessage(badText(" Could not parse " + selType.getFileResource()));
            e.printStackTrace();
            return false;
          }


          return true;
        }
      }
      case "copyfrom":{
        if(args.length <= 1){
          commandSender.sendMessage(goodText(
            " Use this command to select a config type. Try these: "+CONFIG_OPTIONS));
        }else{
          String select = args[1].toLowerCase();
          ConfigurationType selType;
          switch (select){
            case "yml":
            case "yaml":{selType = ConfigurationType.YAML;}break;
            case  "hjson":{selType = ConfigurationType.HJSON;}break;
            case "gson":{ selType = ConfigurationType.GSON; }break;
            case "hocon":{ selType = ConfigurationType.HOCON;}break;
            case "xml":{
              selType = ConfigurationType.XML;
            }
            default:{
              commandSender.sendMessage(color("&c Unrecognized config type. Try these: "+CONFIG_OPTIONS));
              return false;
            }
          }


          commandSender.sendMessage(goodText(" Attempting copy from "+ selType.getFileResource()));
          try {
            FileConfiguration conf =  demoHelper.copyFrom(selType);
            try{
              demoHelper.saveConfig();
              commandSender.sendMessage(goodText("Copy complete"));
            }catch (IOException e){
              commandSender.sendMessage(badText(" Could not save changes: "+ e.getMessage()));
              e.printStackTrace();
            }

          } catch (IOException e) {
            commandSender.sendMessage(badText(" Could not complete transfer: "+ e.getMessage()));
            e.printStackTrace();
            return false;
          } catch (InvalidConfigurationException e) {
            commandSender.sendMessage(badText(" Could not parse "+ selType.getFileResource()));
            e.printStackTrace();
            return false;
          }
        }
        return true;
      }
      default:{
        commandSender.sendMessage(color("&c Un supported input. Try /config help"));
        return false;
      }
    }

  }

  private static String color(String input){
    return ChatColor.translateAlternateColorCodes('&',input);
  }
  private static String goodText(String input){
    return ChatColor.GREEN +  input;
  }

  private static String badText(String input){
    return ChatColor.RED +  input;
  }
  private boolean handleSetCommand(String path,CommandSender commandSender, String[] args){
    if(args.length == 0){
      commandSender.sendMessage(ChatColor.GRAY + " /config edit "+path + " set <option>");
      commandSender.sendMessage(ChatColor.GREEN + " You can now set some primitive data. Here are your options.");
      commandSender.sendMessage(ChatColor.GRAY + " int" + ChatColor.GREEN +" - sets an integer to the selected path");
      commandSender.sendMessage(ChatColor.GRAY + " long " + ChatColor.GREEN +" - sets a long to the selected path");
      commandSender.sendMessage(ChatColor.GRAY + " decimal " + ChatColor.GREEN +" - sets a decimal to  the selected path");
      commandSender.sendMessage(ChatColor.GRAY + " string " + ChatColor.GREEN +" - sets a string to the selected path");
      commandSender.sendMessage(ChatColor.GRAY + " list " + ChatColor.GREEN +" - sets a list to the selected path");
      return false;
    }

    String option = args[0].toLowerCase();
    switch (option){
      case "int":{
        if(args.length <= 1){

          commandSender.sendMessage(ChatColor.RED + " Usage: /config edit "+ path+ " set int <integer>");
          return false;
        }else{
          int num;
          try{
            num = Integer.parseInt(args[1]);
          }catch (NumberFormatException ex){
            commandSender.sendMessage(ChatColor.RED + " "+ args[1] + " is not a valid integer");
            return false;
          }
          demoHelper.getConfigFile().set(path, num);
          commandSender.sendMessage(ChatColor.GREEN + String.format("Path %s has been set to %d",path,num));

        }
        return true;
      }
      case "long": {
        if(args.length <= 1){

          commandSender.sendMessage(ChatColor.RED + " Usage: /config edit "+ path+ " set long <long>");
          return false;
        }else{
          long num;
          try{
            num = Long.parseLong(args[1]);
          }catch (NumberFormatException ex){
            commandSender.sendMessage(ChatColor.RED + " "+ args[1] + " is not a valid long integer");
            return false;
          }
          demoHelper.getConfigFile().set(path, num);
          commandSender.sendMessage(ChatColor.GREEN + String.format("Path %s has been set to %d",path,num));

        }
        return true;
      }
      case "decimal":{
        if(args.length <= 1){

          commandSender.sendMessage(ChatColor.RED + " Usage: /config edit "+ path+ " set long <long>");
          return false;
        }else{
          double num;
          try{
            num = Double.parseDouble(args[1]);
          }catch (NumberFormatException ex){
            commandSender.sendMessage(ChatColor.RED + " "+ args[1] + " is not a valid decimal");
            return false;
          }
          demoHelper.getConfigFile().set(path, num);
          commandSender.sendMessage(ChatColor.GREEN + String.format("Path %s has been set to %f",path,num));

        }
        return true;
      }
      case "string": {
        if(args.length <= 1){

          commandSender.sendMessage(ChatColor.RED + " Usage: /config edit "+ path+ " set string <text>");
          return false;
        }else{
          String text =args[1];
          demoHelper.getConfigFile().set(path, text);
          commandSender.sendMessage(ChatColor.GREEN + String.format("Path %s has been set to %s",path,text));

        }
        return true;
      }
      case "list":{
        if(args.length <= 1){

          commandSender.sendMessage(ChatColor.RED + " Usage: /config edit "+ path+ " set list <text_1> <item_2> .... <item_n>");
          return false;
        }else{
          String[] list = Arrays.copyOfRange(args,1,args.length);
          demoHelper.getConfigFile().set(path, Arrays.asList(list));
          commandSender.sendMessage(ChatColor.GREEN + String.format("Path %s has been set to %s",path,Arrays.toString(list)));

        }
        return true;
      }
    }
    return true;
  }
  private boolean handleEditCommand(CommandSender commandSender, String s, String[] args){
    if(!demoHelper.isActive()){
      commandSender.sendMessage(color("&cYou need to load a config before you use this command!"));
      return true;
    }
    if(args.length ==0){
      commandSender.sendMessage(color("&7 /config edit <path> &a- choose a path you want to edit"));
      return true;
    }

    String path = args[0];
    Matcher match = VALID_INPUT.matcher(path);
    if(!match.matches()){
      commandSender.sendMessage(badText(" Path segments must be alphanumeric"));
      return false;
    }

    if(args.length <= 1 ){
      commandSender.sendMessage(ChatColor.GREEN+ " Now you can edit that path");
      commandSender.sendMessage(ChatColor.GREEN+" Here are some options for you");
      commandSender.sendMessage(ChatColor.GRAY+"   writeLoc "+ChatColor.GREEN+" - saves current location to path");
      commandSender.sendMessage(color("&7   readLoc &a - reads current location at path"));
      commandSender.sendMessage(color("&7   tpLoc &a - teleports you to current location at path"));
      commandSender.sendMessage(color("&7   writeItem &a - saves your held item to path"));
      commandSender.sendMessage(color("&7   giveItem &a - gives you the item at path"));
      commandSender.sendMessage(color("&7   writeMe &a - saves your player data to path"));
      commandSender.sendMessage(color("&7   delete &a - deletes data at path"));
      commandSender.sendMessage(color("&7   testType &a - retieves the data type at path"));
      commandSender.sendMessage(color("&7   set &a - another set of wonderful edits can be done"));

      return false;
    }

    String choice = args[1].toLowerCase();
    switch (choice){
      case "writeloc":{
        if(commandSender instanceof Player){
          demoHelper.getConfigFile().set(path, ((Player)commandSender).getLocation());
          commandSender.sendMessage(String.format(
            ChatColor.GREEN+ " Location written to %s in config type %s ",
            ChatColor.GRAY+path+ ChatColor.GREEN,
            ChatColor.GRAY+demoHelper.getConfigType().getName()+ChatColor.GREEN
          ));
        }else{
          commandSender.sendMessage(color("&c Only players can use this command"));
          return false;
        }
        return true;
      }
      case "readloc":{
        if(commandSender instanceof Player){
          Object res= demoHelper.getConfigFile().get(path);
          if(res == null){
            commandSender.sendMessage(ChatColor.RED +" No data exists at path "+ ChatColor.GRAY + path + ChatColor.RED);
            return false;


          }else {
            if (res instanceof Location){
              Location loc = (Location) res;

              commandSender.sendMessage(String.format(
                ChatColor.GREEN + " Reading location from %s in config type %s ",
                ChatColor.GRAY + path + ChatColor.GREEN,
                ChatColor.GRAY + demoHelper.getConfigType().getName() + ChatColor.GREEN
              ));

              commandSender.sendMessage(ChatColor.GRAY + loc.toString());
            } else {
              commandSender.sendMessage(color("&c There is no location data at " + path));
              return false;
            }
          }
        }else{
          commandSender.sendMessage(color("&c Only players can use this command"));
          return false;
        }
        return true;
      }
      case "tploc": {
        if(commandSender instanceof Player){
          Object res= demoHelper.getConfigFile().get(path);
          if(res == null){
            commandSender.sendMessage(ChatColor.RED +" No data exists at path "+ ChatColor.GRAY + path + ChatColor.RED);
            return false;
          }else {
            if (res instanceof Location){
              Location loc = (Location) res;

              commandSender.sendMessage(String.format(
                ChatColor.GREEN + " Teleporting to location from %s in config type %s ",
                ChatColor.GRAY + path + ChatColor.GREEN,
                ChatColor.GRAY + demoHelper.getConfigType().getName() + ChatColor.GREEN
              ));

              ((Player) commandSender).teleport(loc);
            } else {
              commandSender.sendMessage(color("&c There is no location data at " + path));
              return false;
            }
          }
        }else{
          commandSender.sendMessage(color("&c Only players can use this command"));
          return false;
        }
        return true;
      }
      case "writeitem":{
        if(commandSender instanceof Player){
          Player player = ((Player)commandSender);
          ItemStack item = player.getInventory().getItemInMainHand();
          if(item == null){
            commandSender.sendMessage(color("&c You need an item in hand"));
            return false;
          }
          demoHelper
            .getConfigFile()
            .set(path, item);
          commandSender.sendMessage(String.format(
            ChatColor.GREEN+ " Item written to %s in config type %s ",
            ChatColor.GRAY+path+ ChatColor.GREEN,
            ChatColor.GRAY+demoHelper.getConfigType().getName()+ChatColor.GREEN
          ));
        }else{
          commandSender.sendMessage(color("&c Only players can use this command"));
          return false;
        }
        return true;
      }
      case "giveitem":{
        if(commandSender instanceof Player){
          Player player = ((Player)commandSender);
          Object res= demoHelper.getConfigFile().get(path);
          if(res == null){
            commandSender.sendMessage(ChatColor.RED +" No data exists at path "+ ChatColor.GRAY + path + ChatColor.RED);
            return false;


          }else {
            if (res instanceof ItemStack){
              ItemStack item = (ItemStack) res;

              commandSender.sendMessage(String.format(
                ChatColor.GREEN + " Giving item from %s in config type %s ",
                ChatColor.GRAY + path + ChatColor.GREEN,
                ChatColor.GRAY + demoHelper.getConfigType().getName() + ChatColor.GREEN
              ));

              player.getInventory().setItemInMainHand(item);
            } else {
              commandSender.sendMessage(color("&c There is no item  data at " + path));
              return false;
            }
          }

        }else{
          commandSender.sendMessage(color("&c Only players can use this command"));
          return false;
        }
        return true;
      }
      case "writeme":{
        if(commandSender instanceof Player){
          Player player = ((Player)commandSender);
          demoHelper.getConfigFile().set(path, player);
          commandSender.sendMessage(String.format(
            ChatColor.GREEN+ " Player written to %s in config type %s ",
            ChatColor.GRAY+path+ ChatColor.GREEN,
            ChatColor.GRAY+demoHelper.getConfigType().getName()+ChatColor.GREEN
          ));
        }else{
          commandSender.sendMessage(color("&c Only players can use this command"));
        }
      }break;
      case "delete":{
        demoHelper.getConfigFile().set(path, null);
        commandSender.sendMessage(String.format(
          ChatColor.GREEN+ " Data at path %s was remove in config type %s ",
          ChatColor.GRAY+path+ ChatColor.GREEN,
          ChatColor.GRAY+demoHelper.getConfigType().getName()+ChatColor.GREEN
        ));
      }break;
      case "testtype":{
        Object res= demoHelper.getConfigFile().get(path);
        if(res == null){
          commandSender.sendMessage(ChatColor.RED +" No data exists at path "+ ChatColor.GRAY + path + ChatColor.RED);
          return false;


        }else{
          commandSender.sendMessage(String.format(
            ChatColor.GREEN+ " The data type of data at %s is %s ",
            ChatColor.GRAY+path+ ChatColor.GREEN,
            ChatColor.GRAY+res.getClass().getCanonicalName()+ChatColor.GREEN
          ));
        }

        return true;
      }

      case "set":{
        return handleSetCommand( path, commandSender, Arrays.copyOfRange(args,1,args.length));
      }

      default:{
        commandSender.sendMessage(badText("Unknown sub command"));
      }
    }

    return true;
  }

}
