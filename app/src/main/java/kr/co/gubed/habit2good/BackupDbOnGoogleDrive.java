package kr.co.gubed.habit2good;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Objects;

import kr.co.gubed.habit2good.gpoint.util.CommonUtil;

@SuppressWarnings("unused")
class BackupDbOnGoogleDrive {
    private final Context context;
    private HabitDbAdapter dbAdapter;

    private DriveResourceClient mDriveResourceClient;
    private static DriveFolder baseFolder, rootFolder;


    public BackupDbOnGoogleDrive(Context context) {
        this.context = context;
        dbAdapter = new HabitDbAdapter(context);
        dbAdapter.open();
    }

    public void initializeDriveClient(int requestCode) {
        //DriveClient mDriveClient = Drive.getDriveClient(context, GoogleSignIn.getLastSignedInAccount(context));
        try {
            mDriveResourceClient = Drive.getDriveResourceClient(context, GoogleSignIn.getLastSignedInAccount(context));
            onDriveClientReady(requestCode);
        } catch (Exception e) {
            Toast.makeText(context, "Drive 접근 권한 획득 실패", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void onDriveClientReady(int requestCode) {
        getRootFolder(requestCode);
    }

    private void getRootFolder(final int requestCode) {
        Task<DriveFolder> appFolderTask = mDriveResourceClient.getRootFolder();

        appFolderTask.addOnSuccessListener(new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {
                baseFolder = driveFolder;
                rootFolder = baseFolder;
                Log.i(getClass().getName(), "getRootFolder baseFolder="+baseFolder.toString());
                checkBackupHomeFolder(requestCode);
                //checkForBackup(requestCode);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(getClass().getName(), "getRootFolder fail");
                        Toast.makeText(context, "백업을 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkBackupHomeFolder(final int requestCode) {
        final Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "habit2good"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();

        Task<MetadataBuffer> queryTask = mDriveResourceClient.queryChildren(baseFolder, query);
        Log.i(getClass().getName(), "baseFolder="+baseFolder.toString());

        queryTask.addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadataBuffer) {
                Log.i(getClass().getName(), "Drive backup home folder check:getCount="+metadataBuffer.getCount());
                if (metadataBuffer.getCount() > 0) {
                    Metadata metadata = metadataBuffer.get(0);
                    baseFolder = metadata.getDriveId().asDriveFolder();

                    if (requestCode == CommonUtil.REQUEST_CODE_SIGN_IN_FOR_BACKUP) {
                        mDriveResourceClient.delete(metadata.getDriveId().asDriveResource());
                        createFolderAndBackup(requestCode);
                    } else {
                        checkForBackup(requestCode);
                    }
                } else {
                    createFolderAndBackup(requestCode);
                }
                metadataBuffer.release();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        createFolderAndBackup(requestCode);
                    }
                });
    }

    private void createFolderAndBackup(final int requestCode) {
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(createContentsTask).continueWithTask(new Continuation<Void, Task<DriveFolder>>() {
            @Override
            public Task<DriveFolder> then(@NonNull Task<Void> task) throws Exception {
                DriveContents contents = createContentsTask.getResult();

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("habit2good")
                        .setMimeType(DriveFolder.MIME_TYPE)
                        .setStarred(true)
                        .build();
                return mDriveResourceClient.createFolder(rootFolder, changeSet);
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DriveFolder>() {
                    @Override
                    public void onSuccess(DriveFolder driveFolder) {
                        baseFolder = driveFolder;
                        checkForBackup(requestCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "백업을 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkForBackup(final int requestCode) {
        final Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "habit2good.db"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();

        Task<MetadataBuffer> queryTask = mDriveResourceClient.queryChildren(baseFolder, query);

        queryTask.addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadataBuffer) {
                /* if count is 0, the file doesn't exist */
                Log.i(getClass().getName(), "metadataBuffer.getCount="+metadataBuffer.getCount());
                if (requestCode == CommonUtil.REQUEST_CODE_SIGN_IN_FOR_BACKUP) {
                    if (metadataBuffer.getCount() != 0) { /* 기존 화일 삭제, 안그러면 Drive 에 중복 화일 계속 생성됨 */
                        Metadata metadata = metadataBuffer.get(0);
                        //mDriveResourceClient.trash(metadata.getDriveId().asDriveResource());
                        mDriveResourceClient.delete(metadata.getDriveId().asDriveResource());
                        Log.i(getClass().getName(), "REQUEST_CODE_SIGN_IN_FOR_BACKUP old drive file was deleted");
                    }
                    backUpDatabase(); /* Make file backup */
                } else if (requestCode == CommonUtil.REQUEST_CODE_SIGN_IN_FOR_RESTORE) {
                    if (metadataBuffer.getCount() != 0) {
                        Metadata metadata = metadataBuffer.get(0);
                        restoreBackUp(metadata.getDriveId().asDriveFile());
                    } else {
                        Toast.makeText(context, "복구를 실패했습니다.\n클라우드에 복구 파일이 없습니다.", Toast.LENGTH_LONG).show();
                    }
                }
                metadataBuffer.release();
            }
        });
    }

    final String dbPath = "/data/kr.co.gubed.habit2good/databases/";

    private void restoreBackUp(DriveFile driveFile) {


        Task<DriveContents> openFileTask = mDriveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY);

        openFileTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                DriveContents backupContents = task.getResult();
                InputStream inputStream = Objects.requireNonNull(backupContents).getInputStream();

                final File data = Environment.getDataDirectory();

                File dbFileOld = new File(data, dbPath + HabitDbAdapter.DATABASE_NAME);

                if (dbFileOld.exists()) {
                    dbAdapter.close();
                    dbFileOld.delete();
                }

                File dbFileNew = new File(data, dbPath + HabitDbAdapter.DATABASE_NAME);
                //final FileOutputStream fileOutputStream;
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(dbFileNew);
                } catch (FileNotFoundException e) {
                    Log.e(getClass().getName(), "Could not get input stream from local file");
                    e.printStackTrace();
                    //return;
                }

                Log.i(getClass().getName(), "Attempting to restore from database");
                byte[] buffer = new byte[4096];
                int c;

                while ((c = inputStream.read(buffer, 0, buffer.length)) > 0) {
                    Objects.requireNonNull(fileOutputStream).write(buffer, 0, c);
                }
                Objects.requireNonNull(fileOutputStream).flush();
                fileOutputStream.close();
                inputStream.close();
                Log.i(getClass().getName(), "Database restored");

                Task<Void> discardTask = mDriveResourceClient.discardContents(backupContents);
                return discardTask;
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //dbAdapter.open();
                        Toast.makeText(context, "복구를 완료했습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "복구를 실패했습니다.", Toast.LENGTH_LONG).show();
                        Log.e(getClass().getName(), "Could not read file contents");
                    }
                });
        dbAdapter.open();
    }

    private void backUpDatabase() {
        Log.i(getClass().getName(), "Creating Drive backup, dbPath="+dbPath);
        File data = Environment.getDataDirectory();
        File dbFile = new File(data, dbPath + HabitDbAdapter.DATABASE_NAME);

        if (!dbFile.exists()) {
            Log.e(getClass().getName(), "Local database not found!");
            return;
        }

        final FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(dbFile);
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), "Could not get input stream from local");
            e.printStackTrace();
            return;
        }

        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(createContentsTask).continueWithTask(new Continuation<Void, Task<DriveFile>>() {
            @Override
            public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                DriveContents contents = createContentsTask.getResult();

                OutputStream outputStream = Objects.requireNonNull(contents).getOutputStream();

                byte[] buffer = new byte[4096];
                int c;

                while ((c = fileInputStream.read(buffer, 0, buffer.length)) > 0) {
                    outputStream.write(buffer, 0, c);
                }
                outputStream.flush();
                outputStream.close();
                fileInputStream.close();

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("habit2good.db")
                        .setMimeType("application/x-sqlite3")
                        .setStarred(false)
                        .build();
                return mDriveResourceClient.createFile(baseFolder, changeSet, contents);
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
                        Toast.makeText(context, "백업을 완료했습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "백업을 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void importDBToDrive() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/kr.co.gubed.habit2good/databases/";
                String backupDBPath = "/Android/data/kr.co.gubed.habit2good/files/habit2good/";

                File currentDB = new File(data, currentDBPath+HabitDbAdapter.DATABASE_NAME);
                File backupDB = new File(sd,backupDBPath+HabitDbAdapter.DATABASE_NAME);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(this.context, "복구가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportDBToDrive() {
        //dbHelper = new HabitDatabaseOpenHelper(context);
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        String currentDBPath = "/data/kr.co.gubed.habit2good/databases/";
        String backupDBPath = "/Android/data/kr.co.gubed.habit2good/files/habit2good/";
        File file = new File(sd, backupDBPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            if (sd.canWrite()) {

                File currentDB = new File(data, currentDBPath+HabitDbAdapter.DATABASE_NAME);
                File backupDB = new File(sd,backupDBPath+HabitDbAdapter.DATABASE_NAME);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(this.context, "백업이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/kr.co.gubed.habit2good/databases/";
                String backupDBPath = "/Android/data/kr.co.gubed.habit2good/files/habit2good/";

                File currentDB = new File(data, currentDBPath+HabitDbAdapter.DATABASE_NAME);
                File backupDB = new File(sd,backupDBPath+HabitDbAdapter.DATABASE_NAME);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(this.context, "복구가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportDB() {
        //dbHelper = new HabitDatabaseOpenHelper(context);
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        String currentDBPath = "/data/kr.co.gubed.habit2good/databases/";
        String backupDBPath = "/Android/data/kr.co.gubed.habit2good/files/habit2good/";
        File file = new File(sd, backupDBPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            if (sd.canWrite()) {

                File currentDB = new File(data, currentDBPath+HabitDbAdapter.DATABASE_NAME);
                File backupDB = new File(sd,backupDBPath+HabitDbAdapter.DATABASE_NAME);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(this.context, "백업이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* not working */
    private void getAppFolder(final int requestCode) {
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

        Tasks.whenAll(appFolderTask, createContentsTask).continueWithTask(new Continuation<Void, Task<DriveFile>>() {
            @Override
            public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                DriveFolder parent = appFolderTask.getResult();
                DriveContents contents = createContentsTask.getResult();
                baseFolder = parent;

                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setTitle("habit2good.db")
                        .setMimeType("application/x-sqlite3")
                        .setStarred(false)
                        .build();
                return mDriveResourceClient.createFile(Objects.requireNonNull(parent), metadataChangeSet, contents);
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
                        checkForBackup(requestCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(getClass().getName(), "getHomeFolder fail");
                        e.printStackTrace();
                        Toast.makeText(context, "백업을 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
