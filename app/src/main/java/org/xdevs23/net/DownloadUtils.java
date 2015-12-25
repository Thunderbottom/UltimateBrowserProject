package org.xdevs23.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.xdevs23.config.AppConfig;
import org.xdevs23.debugUtils.StackTraceParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import io.github.UltimateBrowserProject.Activity.UpdateActivity;


public class DownloadUtils {
	
	@SuppressWarnings("unused")
	private static 
	  final short
	    byteIdentifier =  8,   // 0x08
	    halfByte       =  4,   // 0x04
	    hexdeCount     =  2,   // 0x02
	    shortId        = 16,   // 0x10
	    intId          = 32,   // 0x20
	    longId		   = 64,   // 0x40
	    longerLong     = 0x80, //  128
	    longest        = 0xff  //  255
	    ;
	
	@SuppressWarnings("unused")
	private static
	  final int     
		singleKilo	= 0x0400, //  1024
		  dualKilo  = 0x0800, //  2048
		   triKilo 	= 0x0c00, //  3072
		  quadKilo 	= 0x1000, //  4096
		  octaKilo 	= 0x2000, //  8192
		 shortKilo  = 0x4000, // 16384
		   intKilo  = 0x8000, // 32768
		 bLongKilo  = 0xffff  // 65535
		 ;
	
	public  static ProgressBar progressUpdateBar    = null;
	
	private static long customLength = 4 * 1024 * 1024;
	
	public static void setCustomFileLength(long length) {
		customLength = length;
	}

	/**
	 * @deprecated <p style="text-decoration:none;font-weight:500;color:#FFAAAA;">Use <code>setProgressBar</code> instead.</p>
	 * @param pb
	 * @param setIt
	 */
	public  static void setProgressUpdateObject( ProgressBar pb, boolean setIt ) {
		progressUpdateBar = setIt ?  pb
								  :  null;
	}

    /**
     * Set the progress bar to control
     * @param id Id of the progressbar
     * @param context Actual context
     */
	public  static void setProgressBar( int id, Context context ) {
		View view = new View(context);
		progressUpdateBar = (ProgressBar) view.findViewById(id);
	}
	
	public  static final int defaultBuf 
						=    octaKilo;
	
	private static final int oneKiloByte
					    = singleKilo;
	
	public  static class ContextManagement {
		
		private static Context activeContext = null;
		
		public static void setActiveContext(Context context) {
			activeContext = context;
		}
		
		public static Context getActiveContext() {
			return activeContext;
		}
		
	}
	
	private static void logt(String loginfo) {
		Log.d(AppConfig.appName, loginfo);
	}
	
	public static void downloadFile( String dUrl, String dest ) {
		try {
			DownloadAsync AsyncDownloader = new DownloadAsync();
			AsyncDownloader.execute(dUrl, dest);
		} catch (Exception e) {
			logt("(downloadFile) Exception thrown: " + e.getMessage() + " " + e.getStackTrace());
        }
	}
	
	public static String downloadString(String url) {
		String dS = new String();
		try {
			DownloadStringAsync asyncSDownloader = new DownloadStringAsync();
			dS = asyncSDownloader.execute(new String[] {url}).get();
		} catch(Exception ex) {
			logt("Exception thrown: " + StackTraceParser.parse(ex));
		}
		
		return dS;
	}
	
	//////////////////////////////////////////////
	
	static class DownloadStringAsync extends AsyncTask < String, Integer, String > {
		
		@SuppressWarnings("unused")
		private void logt(String li) {
			Log.d(AppConfig.appName, "(DownloadSAsync) " + li);
		}
		
		@Override
		protected String doInBackground(String... dUrl) {
		    URL uro = null;
			String result  = null;
			try {
				uro = new URL(dUrl[0]);
			} catch (MalformedURLException e) {
			    /* */
            }

			try {
			    InputStream is = null;
				is = uro.openStream();
			    BufferedReader br = new BufferedReader(new InputStreamReader(is));
				result = br.readLine();
				
			    br.close();
			    is.close();
			} catch (IOException e) {

			}
		
		return result;
		}
	}
	
	//////////////////////////////////////////////

	static class DownloadAsync extends AsyncTask < String, String, String > {
		
		private void logt(String loginfo) {
			Log.d(AppConfig.appName, "(DownloadAsync) " + loginfo);
		}
		
		long lengthOfFile = 1;
	
		
		@Override
		protected String doInBackground(String... fDUrl) {
			int count;
			String doUrl = "[NOTHING]";
		    try {
		    	if(fDUrl == null) {
		    		logt("fDUrl is null");
		    	}
		    	
	    	  	logt("Converting url...");
	    	  	
	    	  	doUrl = fDUrl[0];
	    	  	
	    	  	logt("Connecting...");
	            
	    	  	URL url = new URL(doUrl);
	            URLConnection connection = url.openConnection();
	            
	            connection.connect();
	            
	            try {
	            	lengthOfFile = Long.parseLong(connection.getHeaderField("Content-Length"));
	            } catch(Exception ex) {
	            	logt("Error getting content-length, using custom length");
	            	lengthOfFile = customLength;
	            }
	            
	            
	            logt("Total bytes to download: " + String.valueOf(lengthOfFile));
	            
	            logt("Preparing download for " + doUrl);
	            InputStream input = new BufferedInputStream(url.openStream(),
	                    										defaultBuf);
	            
	
	            logt("Making needed directories...");
	            
	            File dirFile = new File( fDUrl[1].substring(0, fDUrl[1].lastIndexOf('/')));
	            dirFile.mkdirs();
	            
	            String newSaveU = fDUrl[1];
				
				logt("Downloading...");
	
	            OutputStream output = new FileOutputStream(newSaveU);
	            
	            byte data[] = new byte[oneKiloByte];
	                        
	            long total = 0;
	
	            while ( (  count = input.read(data)  ) != -1 ) {
	                total += count;
	
	                publishProgress( String.valueOf(total) );
	                output.write(data, 0, count);
	            }
	
	
	            output.flush();
	
	            output.close();
	            input.close();
	            
	            logt("Download finished");
		      } catch ( Exception e ) {
		    	  logt("Error: " + e.getMessage());
		      }
			return "";
		}
		
		@Override
	    protected void onProgressUpdate(String... progress) {
	
			long pr = Long.parseLong(progress[0]);
			long pd = (long) (Math.round((double)( (   pr * 100  ) / lengthOfFile)));
			
			logt(String.valueOf((int)pd));
			
			UpdateActivity.updateProgress((int) pd);
	    }
		
	    @Override
	    protected void onPostExecute(String result) {
	    	customLength = 4 * 1024 * 1024;
	    	logt("Result: " + result);
	    	UpdateActivity.endProgress();
	    	logt("Starting installation...");
	    	UpdateActivity.startUpdateInstallation();
	    	progressUpdateBar = null;
	
	    }
		
	}
	
		
}
