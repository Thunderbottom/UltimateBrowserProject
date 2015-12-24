package org.xdevs23.root.utils;

import org.xdevs23.debugUtils.Logging;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Contains some utilities for use with root permissions.
 * @author xdevs23 (Simao)
 *
 */
public class RootController {
	
	public static final int RUN_COMMANDS_SAFE_MODE = 0;
	
	public static final String EXEC_DONE_RETURN =   "_::[EXEC_DONE]::";
	
	private static void d(String l) {
		Logging.logd("[RootController] " + l);
	}
	
	/**
	 * Requests root permission
	 * 
	 * @return <b>True</b> if successful,
	 * <b>false</b> if an exception is thrown.
	 */
	public static boolean requestRoot() {
		d("(requestRoot) Requesting root permission...");
		try {
			Process p = Runtime.getRuntime().exec("su");
			Thread.sleep(20);
			p.destroy();
			d("(requestRoot) Done!");
	        return true;
		}
		catch( IOException ioex) {return false;}
		catch(   Exception   ex) {return false;}
	}
	
	/**
	 * Check if su binary exists
	 * @return True if exists, false if not
	 */
	public static boolean isSuInstalled() {
		File subin  = new File("/system/bin/su"),
			 suxbin = new File("/system/xbin/su"),
			 susbin = new File("/sbin/su");
		
		return 	subin .exists()	||
				suxbin.exists()	||
				susbin.exists();
	}
	
	private static boolean checkBusyboxInstalled() {
		try {
			File bsbin  = new File("/system/bin/busybox" );
			File bsxbin = new File("/system/xbin/busybox");
			File bssbin = new File("/sbin/busybox"       );
			
			return 	bsbin .exists() ||
					bsxbin.exists() ||
					bssbin.exists()
					;
		} catch(Exception ex) {return false;}
	}
	
	/**
	 * Checks if BusyBox is installed on the system
	 * 
	 * @return <b>True</b> if installed, <b>false</b> if not
	 * or if an exception is thrown.
	 */
	public static boolean isBusyboxInstalled() {
		d("(BusyBox Checker) Checking if BusyBox is installed...");
		boolean r = checkBusyboxInstalled();
		d("(BusyBox Checker) BusyBox " + ( r ? "" : "not " ) + "detected.");
		
		return r;
	}
	
	/**
	 * @author User <i style="color:aqua;">18446744073709551615</i> at <a href="http://stackoverflow.com/users/755804/18446744073709551615">stackoverflow</a>
	 * @param is <code>InputStream</code> to read from
	 * @return Content of <code><i>InputStream is</i></code>
	 * @throws IOException
	 */
	private static String readResult(InputStream is) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int length = 0;
	    while ((length = is.read(buffer)) != -1) {
	        baos.write(buffer, 0, length);
	    }
	    return baos.toString("UTF-8");
	}

	
	
	/**
	 * 
	 * @param cmd Command to execute
	 * @param customBufSize Custom buffer size (default <i>512</i>)
	 * @param readResult Read result (dep)
	 * @return Result of command execution
	 * @throws IOException If an error occurs while execution.
	 */
	
	public static String runCommand(String cmd, int customBufSize, boolean readResult) throws IOException {
			d("(runCommand) Getting root permission...");
			Process p = Runtime.getRuntime().exec("su");
			
			DataOutputStream doutps = new DataOutputStream(p.getOutputStream());
				 InputStream  dinps =                      p.getInputStream () ;
			
			d("(runCommand) Running command '" + cmd + "'");
			doutps.writeBytes(cmd + "\n");
			doutps.flush();
			
			String endread = "";
			
			
			d("(runCommand) Exiting shell...");
			doutps.writeBytes("exit\n");
			doutps.flush();
			
			d("(runCommand) Waiting for process to finish");
			try {
				p.waitFor();
			} catch(InterruptedException e) {
				
			}
			
			if(readResult) {
				d("(runCommand) Reading result...");
				endread = readResult(dinps);
			} else endread = EXEC_DONE_RETURN;
			
			doutps.close();
			try { p.destroy(); } catch(Exception ex) {}
			
			return endread;

	}

	
	/**
	 * 
	 * @param cmd Command to execute
	 * @return Result of command execution
	 * @throws IOException If an error occurs while execution.
	 */
	
	public static String runCommand(String cmd) throws IOException {
		d("(runCommand*) Overload method runCommand");
		return runCommand(cmd, 512, true);
	}
	
	/**
	 * 
	 * @param cmds Commands to execute
	 * @return Result of commands executions
	 */
	
	public static String[] runCommands(String[] cmds, int safeMode) {
		int cmdcount = cmds.length;
		
		String[] cresults = new String[cmdcount];
		
		int cexec = 0;
		
		d("(runCommands) Running commands...");
		
		try {
			for ( String cmd : cmds ) {
				cresults[cexec] = runCommand(cmd);
				cexec++;
			}
		} catch(Exception ex) {
			
		}
		
		return cresults;
	}
	
	/**
	 * 
	 * @param cmds Commands to execute
	 * @return Result of commands executions
	 * 
	 */
	public static String[] runCommands(String[] cmds) {
		d("(runCommands*) Overload method runCommands");
		try {
			return runCommands(cmds, RUN_COMMANDS_SAFE_MODE);
		} catch(Exception ex) {  return new String[1]; }
	}
	
	/**
	 * 
	 * @param cmd Command to execute in safe mode
	 * @return Result of command execution.
	 */
	public static String runCommandSafe(String cmd) {
		try { return runCommand(cmd); }
		catch (IOException ex) { return ""; }
	}

	
}
