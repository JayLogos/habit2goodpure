package kr.co.gubed.habit2good;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFile extends AsyncTask<String, String, String> {
    Context context;
    String fileName;
    String uid;

    HttpURLConnection conn = null;
    DataOutputStream dos = null;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1024;
    File sourceFile;
    int serverResponseCode;
    String TAG = getClass().getName();

    public UploadFile(Context context) {
        this.context = context;
    }

    public void setPath(String uid, String uploadFilePath) {
        this.fileName = uploadFilePath;
        this.sourceFile = new File(uploadFilePath);
        this.uid = uid;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        if(!sourceFile.isFile()) {
            Log.e(TAG, "source file("+sourceFile+") is not exist");
            return null;
        }  else {
            String success = "Success";
            Log.i(TAG, sourceFile+" is a File");

            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(strings[0]);
                Log.i(TAG, strings[0]);

                // Open HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);     // Allow Inputs
                conn.setDoOutput(true);     //Allow Outputs
                conn.setUseCaches(false);   //Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                Log.i(TAG, "fileName is "+fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                // 사용자 이름으로 폴더를 생성하기 위해 사용자 이름을 서버로 전송한다.
                // 하나의 인자 전달 data1 = newImage
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"data1\""+lineEnd);   // name의 \ \ 안 인자가 php의 key
                dos.writeBytes(lineEnd);
                dos.writeBytes(uid);     // newImage라는 값을 넘김--> uid를 폴더 이름으로 사용
                dos.writeBytes(lineEnd);

                // 이미지 전송, 데이터 전달 uploaded_file이라는 php key 값에 저장되는 내용은 fileName
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""+fileName+"\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necessary after file data
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i(TAG, "HTTP Response is "+serverResponseMessage+" code="+serverResponseCode);

                if (serverResponseCode == 200) {
                    Log.i(TAG, "file upload was completed: "+fileName);
                }

                // 결과 확인
                BufferedReader rd = null;
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    Log.i(TAG, "Upload State "+ line);
                }

                // close the streams
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (Exception e) {
                Log.e(TAG, "error: "+e.toString());
            }
            return success;
        }
    }
}
