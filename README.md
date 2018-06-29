
# CustomFileConfig
API that bridges the gap between other data representation formats and Spigot's Configuration API

## Goals
The solution for supporting alternate configuration formats must be
- extensible
- fast
- connect directly with the Bukkit Configuration File
- serve as an alternative to the YamlConfiguration class


## Problems Seeking to solve
 - Uprooting entire code base to support an alternative configuration format
 - End user complaints of configuration breaking due to misalignment of indent
 - Allow switch to configuration formats that are both "human readable" and "human modifiable".



## How it works

Each of the implementations work by acting as a translator between the configuration format and Bukkit's configuration api. These translation needs to happen in both way's which brings us to the Constructor and Representer classes. The Representer builds a configuration format representation of objects passed to it and the Constructor constructs Java objects from the configuration representation.
	
## How to use
Simply replace YamlConfiguration with any of the provided solutions to seamlessly began saving and loading data in a completely different format.


```
  YamlConfiguration config = new YamlConfiguration();
  File file = new File(plugin.getDataFolder(),"data.yml");

  
  try {
    config.load(file);
  } catch (InvalidConfigurationException e) {
    e.printStackTrace();
  } catch (FileNotFoundException e) {
    e.printStackTrace();
  } catch (IOException e) {
    e.printStackTrace();
  }
  
  
```



In that code block, the first line can easily be replaced with something like 
```
  GsonConfiguration config = new GsonConfiguration();
```
and use Google's Gson library.

If you are really ambitious, this api will meet you there and even handle the Xml format.
```
  XmlConfiguration config = new XmlConfiguration();
```


Since these alternative configuration formats are not included in spigot by default, you will need to shade them in to your final build.
```
        <dependency>
            <groupId>[library group id]</groupId>
            <artifactId>[library artifact id]</artifactId>
            <version>[library version]</version>
            <scope>compile</scope>
        </dependency>
		
	<build>
        <plugins>
      
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.1.1</version>
                <configuration>
                    <relocations>
                        <relocation>
                            <pattern>[library package]</pattern>
                            <shadedPattern>[your package].[library name]</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
	```
  
  Then you just copy the neccessary files to your project. (Shading this enourmous project may not be the best idea)
  
  Happy coding!
