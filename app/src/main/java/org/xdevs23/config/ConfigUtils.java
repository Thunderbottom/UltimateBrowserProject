package org.xdevs23.config;

import org.xdevs23.config.AppConfig.Version;
import org.xdevs23.config.AppConfig.Version.VersionDifferences;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtils {
	
	public static String getVersionName() {
		return AppConfig.versionName;
	}
	
	public static Vector< Integer > getVersionBundle() {
		Vector< Integer > versionBundle = new Vector< Integer >();
		
		versionBundle.add( AppConfig.Version.major    );
		versionBundle.add( AppConfig.Version.minor    );
		versionBundle.add( AppConfig.Version.build    );
	//  versionBundle.add( AppConfig.Version.revision );
		
		return versionBundle;
	}
	
	public static int getVersionPart( int part ) {
		VersionDifferences versionDifference = VersionDifferences.values()[part];
		
		
		switch ( versionDifference ) {
			case major   :  return Version.major   ;
			case minor   :  return Version.minor   ;
			case build   :  return Version.build   ;
		//  case revision:  return Version.revision;
			default: return 0;
		}

	}

    public static int getVersionForwardable() {
        return (
                Integer.parseInt(
                        (new StringBuilder())
                                .append(Version.major)
                                .append(Version.minor)
                                .append(Version.build)
                        //      .append(Version.revision)
                        .toString()
        ));
    }

    public static boolean isDebuggable() {
        String debuggers = ".*(debug|dbg|rc|pre|alpha|beta).*";
        return (!AppConfig.dbgVer.contains("release")) &&
                ( ((Matcher)( Pattern.compile(debuggers).matcher(AppConfig.dbgVer) )) .matches() );
    }

}
