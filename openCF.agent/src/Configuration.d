import std.file;
import std.stdio;
import std.string;
import core.exception;
import std.ascii;

import util.Logger;

/****
 * Configuration class
 *
 * Reads in the configuration from a given configuration file and provides
 * the key-value pairs for the application via the get(key) method.
 */
class Configuration {
	private string[string] configMap;
	
	this(string configfileName) {
		auto configArray = slurp!(string, string)(configfileName, "%s = %s");
		
		foreach(line; configArray) {
			try {
				string key = line[0];
				string value = chomp(line[1]);
				configMap[key] = value;
			} catch (Exception e) {
				Logger.myError("Exception caught -> malformed line in config file, skipping line", __FILE__, __LINE__);
			}
		}
	}
	
	/****
	 * prints the configuration key-value pairs
	 *
	 */
	public string toString() {
		string result;
		foreach(key; configMap.keys) {
			result ~= key ~ " - " ~  configMap[key] ~ "\n";
		}
		return result;
	}
	
    /****
     * getter for configuration values
     * 
     * Returns a configuration value for a given key. 
     * If the configuration does not contain this key or the key is an empty string,
     * this method returns an empty string.
     */
	public string get(string key) {
		string value;
		try {
			value = configMap[key];
		} catch (RangeError e) {
			Logger.myError("RangeError caught -> no key [" ~ key ~ "] found", __FILE__, __LINE__);
			value = "";
		}
		return value;
	}
}