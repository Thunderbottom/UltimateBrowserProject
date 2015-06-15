package io.github.UltimateBrowserProject.Application;




import android.content.Context;
import android.net.Uri;
import android.util.Log;


import org.acra.ACRA;
import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.acra.ACRA.LOG_TAG;

public class JsonSender implements ReportSender {
    private Uri mFormUri = null;
    private Map<ReportField, String> mMapping = null;
    private static final String CONTENT_TYPE;

    static {
        CONTENT_TYPE = "application/json";
    }

    /**
     * <p>
     * Create a new HttpPostSender instance.
     * </p>
     *
     * @param formUri The URL of your server-side crash report collection script.
     * @param mapping If null, POST parameters will be named with
     * {@link org.acra.ReportField} values converted to String with
     * .toString(). If not null, POST parameters will be named with
     * the result of mapping.get(ReportField.SOME_FIELD);
     */
    public JsonSender(String formUri, Map<ReportField, String> mapping) {
        mFormUri = Uri.parse(formUri);
        mMapping = mapping;
    }

    public void send(Context context, CrashReportData errorContent) throws ReportSenderException {

        try {
            URL reportUrl;
            reportUrl = new URL(mFormUri.toString());
            Log.d(LOG_TAG, "Connect to " + reportUrl.toString());

            JSONObject json = createJSON(errorContent);

            sendHttpPost(json.toString(), reportUrl, ACRA.getConfig().formUriBasicAuthLogin(), ACRA.getConfig().formUriBasicAuthPassword());

        } catch (Exception e) {
            throw new ReportSenderException("Error while sending report to Http Post Form.", e);
        }

    }

    private static boolean isNull(String aString) {
        return aString == null || aString == null;

    }

    private JSONObject createJSON(Map<ReportField, String> report) {
        JSONObject json = new JSONObject();

        ReportField[] fields = ACRA.getConfig().customReportContent();
        if (fields.length == 0) {
            fields = ACRAConstants.DEFAULT_REPORT_FIELDS;
        }
        for (ReportField field : fields) {
            try {
                if (mMapping == null || mMapping.get(field) == null) {
                    json.put(field.toString(), report.get(field));
                } else {
                    json.put(mMapping.get(field), report.get(field));
                }
            } catch (JSONException e) {
                Log.e("JSONException", "There was an error creating JSON", e);
            }
        }

        return json;
    }

    //TODO: login + password
    //(isNull(login) ? null : login, isNull(password) ? null : password);
    private void sendHttpPost(String data, URL url, String login, String password) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost httPost = new HttpPost(url.toString());

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            JSONObject jsonObject = new JSONObject();

            data = data.replace("\\n", " \n ");
            data = data.replace("\\", "");
            StringEntity se = new StringEntity(getDataForMandrill(data));
            httPost.setEntity(se);

//sets a request header so the page receving the request
            //will know what to do with it
            httPost.setHeader("Accept", "application/json");
            httPost.setHeader("Content-type", "application/json");


            HttpResponse httpResponse = httpClient.execute(httPost);

            Log.d(LOG_TAG, "Server Status: " + httpResponse.getStatusLine());
            Log.d(LOG_TAG, "Server Response: " + EntityUtils.toString(httpResponse.getEntity()));


        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private String getDataForMandrill(String data) {


        try {

            JSONObject email_json = new JSONObject();
            email_json.put("email", "3.14chinmay@gmail.com"); // recipient email address goes here
            email_json.put("name", "balzathor"); // receiver's name..can be changed
            email_json.put("type", "to");

            JSONArray email_array = new JSONArray();
            email_array.put(email_json);

            JSONObject message_json = new JSONObject();
            message_json.put("html", "");
            message_json.put("text", "Logs: \n" + data);
            message_json.put("subject", "UltimateBrowserProject Error Report"); // add your own subject
            message_json.put("from_email", "error@android.com"); // change if your preferred "from email"
            message_json.put("from_name", "Automatic Error Report"); // change it to your preferred name
            message_json.put("to", email_array);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", "6aT8LcnEVBc6PNxNtnkkOA");
            jsonObject.put("message", message_json);

            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}