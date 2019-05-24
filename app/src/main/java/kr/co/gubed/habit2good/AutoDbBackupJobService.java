package kr.co.gubed.habit2good;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;

import java.util.HashSet;
import java.util.Set;

import kr.co.gubed.habit2good.gpoint.util.CommonUtil;

public class AutoDbBackupJobService extends JobService {
    private final BackupDbOnGoogleDrive dbBackup = new BackupDbOnGoogleDrive(this);

    public AutoDbBackupJobService() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        signInAndBackup();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void signInAndBackup() {
        Log.i(getClass().getName(), "Start sign id");
        Set<Scope> requiredScopes = new HashSet<>(2);

        requiredScopes.add(Drive.SCOPE_FILE);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            dbBackup.initializeDriveClient(CommonUtil.REQUEST_CODE_SIGN_IN_FOR_BACKUP);
        }
    }
}
