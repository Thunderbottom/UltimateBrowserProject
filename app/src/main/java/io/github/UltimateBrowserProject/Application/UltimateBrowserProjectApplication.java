package io.github.UltimateBrowserProject.Application;


import android.app.Application;
import org.acra.*;
import org.acra.annotation.ReportsCrashes;

import io.github.UltimateBrowserProject.R;


@ReportsCrashes(
        formUri = "http://mandrillapp.com/api/1.0/messages/send.json",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = R.drawable.ic_action_report_problem, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.

)

public class UltimateBrowserProjectApplication extends Application {
    private ReportsCrashes mReportsCrashes;

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);

        mReportsCrashes = this.getClass().getAnnotation(ReportsCrashes.class);
        JsonSender jsonSender = new JsonSender(mReportsCrashes.formUri(), null);
        ACRA.getErrorReporter().setReportSender(jsonSender);

    }
}
