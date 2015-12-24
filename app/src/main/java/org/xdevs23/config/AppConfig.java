package org.xdevs23.config;

import android.os.Environment;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;

@SuppressWarnings("unused")
public final class AppConfig {
	
	private static final char dot = '.';
	
	public final
		static String
			    appName = "UltimateBrowserProject",
			versionName = String.valueOf(Version.major)    + dot +
						  String.valueOf(Version.minor)    + dot +
						  String.valueOf(Version.build) /* + dot +
						  String.valueOf(Version.revision)  */,
			 mainDevUrl = "http://Thunderbottom.github.io/",
              secDevUrl = "http://xdevs23.bplaced.com/",
			 updateRoot = mainDevUrl   + "update/",
             updateRootSec = secDevUrl + "upload/update/UBP/",
			 myDataRoot = Environment.getDataDirectory() + "data/io.github.UltimateBrowserProject/",
			 debugTag   = appName,
			 dbgVer     = "dbg"
	;
	
	public static class Version {
		
		public enum VersionDifferences {
			major,
			minor,
			build,
		//  revision,
			empty
		}
		
		public static int
				major     =  2   ,
				minor     =  0   ,
				build     =  1 /*,
		    	revision  =  0  */
		;
		
		public static boolean
				useRev = false;
		
	}

}
