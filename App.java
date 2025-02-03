package com.example;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.example.VideoPlayer;

// import java.awt.Desktop;
// import java.io.File;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.TimeUnit;

// import com.google.gson.Gson;
// import com.google.gson.JsonIOException;
// import com.google.gson.JsonSyntaxException;

/**
 * Hello world!
 */
public final class App {

    static class AppResult {
        private final String video;
        private final int time;

        public AppResult(String video, int time) {
            this.video = video;
            this.time = time;
        }

        public String getVideo() {
            return video;
        }

        public int getTime() {
            return time;
        }
    }


    Map data;
    String json_path;
    String json;
    String video;
    int time;

    private App(String jp) {
        json_path = jp;
        try {
             //data = new Gson().fromJson(json, Map.class);
            data = new Gson().fromJson(new FileReader(json_path), Map.class);
        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String hashFrame(int[][][] frame) {
        // Convert the frame to a byte array
        byte[] frameBytes = convertToByteArray(frame);

        try {
            // Create a hash object using SHA-256
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            // Update the hash object with the frame bytes
            sha256.update(frameBytes);

            // Get the hexadecimal representation of the hash
            byte[] hashBytes = sha256.digest();
            StringBuilder hashStringBuilder = new StringBuilder();

            for (byte hashByte : hashBytes) {
                // Convert each byte to a two-digit hexadecimal representation
                hashStringBuilder.append(String.format("%02x", hashByte));
            }

            return hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ""; // Handle the exception according to your requirements
        }
    }

    private static byte[] convertToByteArray(int[][][] frame) {
        // Assuming frame values are in the range 0-255 (8-bit color)
        int height = frame.length;
        int width = frame[0].length;
        int channels = frame[0][0].length;
        byte[] frameBytes = new byte[height * width * channels];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int c = 0; c < channels; c++) {
                    frameBytes[index++] = (byte) frame[y][x][c];
                }
            }
        }

        return frameBytes;
    }

    public int runQuery(String queryPath){
        int frameWidth = 352;
        int frameHeight = 288;
        File file = new File(queryPath);
        if (file.isFile() && file.getName().toLowerCase().endsWith(".rgb")){
            double i = 0;
            try (FileInputStream fis = new FileInputStream(queryPath)) {
                byte[] frameData = new byte[frameWidth * frameHeight * 3];
                int bytesRead;

                bytesRead = fis.read(frameData);
                if (bytesRead != -1) {
                    // Process the frame data

                    // Convert frameData to a 3D array
                    int[][][] frame = new int[frameHeight][frameWidth][3];
                    int index = 0;
                    for (int y = 0; y < frameHeight; y++) {
                        for (int x = 0; x < frameWidth; x++) {
                            for (int c = 0; c < 3; c++) {
                                frame[y][x][c] = frameData[index++] & 0xFF;
                            }
                        }
                    }

                    // Hash the frame
                    String hashedFrame = hashFrame(frame);

                    // Check if hashed_frame is in the specified list
                    if (data.containsKey(hashedFrame) && ((List)data.get(hashedFrame)).size() > 1) {
                        byte[] prevFrame = frameData.clone();
                        while (Arrays.equals(frameData, prevFrame) ||
                                Arrays.asList("e1e7f23492d6ac254ff14445c7760be9d8918567c03b87b940b8f452bfbb6681",
                                        "e8935af6d0577c56aef5296daf1f9ac8e1a644ac2641200c835b1f69933f249b",
                                        "e509a000445b3f4060ade0877970cd233b0db8e02516b8f816624412a87341a8")
                                        .contains(hashedFrame)) {
                            i += 1 / 30.0;
                            bytesRead = fis.read(frameData);
                            if (bytesRead == -1) {
                                break;
                            }

                            // Convert frameData to a 3D array for the next iteration
                            index = 0;
                            for (int y = 0; y < frameHeight; y++) {
                                for (int x = 0; x < frameWidth; x++) {
                                    for (int c = 0; c < 3; c++) {
                                        frame[y][x][c] = frameData[index++] & 0xFF;
                                    }
                                }
                            }

                            hashedFrame = hashFrame(frame);
                        }
                    }

                    // Display the corresponding frame number
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) data.get(hashedFrame);
                    System.out.println(list);

                    int seconds = (int) Math.round(((double) ((Map) ((List) data.get(hashedFrame)).get(0)).get("timestamp")) - i);
                    time = seconds;
                    int hours = seconds / 3600;
                    int remainder = seconds % 3600;
                    int minutes = remainder / 60;
                    seconds = remainder % 60;

                    // Create a time object with the time components
                    // Assuming Java's Date class, you may want to use java.time for a more modern approach
                    Date timeObj = new Date(0, 0, 0, (int) hours, (int) minutes, (int) seconds);
                    // LocalTime timeObj = LocalTime.of((int)hours, (int)minutes, (int)seconds);

                    // Format the time object as a string in the desired format
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String timeStr = sdf.format(timeObj);
                    video = ((Map) ((List) data.get(hashedFrame)).get(0)).get("video").toString();
                    System.out.println("File found in " + video +
                            " at " + timeStr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
        else{
            System.out.println("Please input a valid RGB file.");
            System.exit(0);
        }
        return 0;
    }

    // public static void playVideo(){

    // }


    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        List<AppResult> results = new ArrayList<>();
        if(args.length == 0){
            System.out.println("Please input the path of query video as the first argument.");
            return;
        }
        // String[] json_paths= {"src/main/resources/Hashed_RGB_1.json", "src/main/resources/Hashed_RGB_2.json", "src/main/resources/Hashed_RGB_3.json", "src/main/resources/Hashed_RGB_4.json", "src/main/resources/Hashed_RGB_5.json", "src/main/resources/Hashed_RGB_6.json", "src/main/resources/Hashed_RGB_7.json", "src/main/resources/Hashed_RGB_8.json", "src/main/resources/Hashed_RGB_9.json", "src/main/resources/Hashed_RGB_10.json", "src/main/resources/Hashed_RGB_11.json", "src/main/resources/Hashed_RGB_12.json", "src/main/resources/Hashed_RGB_13.json", "src/main/resources/Hashed_RGB_14.json", "src/main/resources/Hashed_RGB_15.json", "src/main/resources/Hashed_RGB_16.json", "src/main/resources/Hashed_RGB_17.json", "src/main/resources/Hashed_RGB_18.json", "src/main/resources/Hashed_RGB_19.json", "src/main/resources/Hashed_RGB_20.json"};
        String[] json_paths = {"src/main/resources/Hashed_RGB_1_to_4.json", "src/main/resources/Hashed_RGB_5_to_8.json", "src/main/resources/Hashed_RGB_9_to_12.json", "src/main/resources/Hashed_RGB_13_to_16.json", "src/main/resources/Hashed_RGB_17_to_20.json"};
        
        List<Thread> threads = new ArrayList<>();
        for(String jp : json_paths){
            Runnable queryRunnable = () -> {
                App app = new App(jp);
                app.runQuery(args[0]);
                synchronized (results){
                    results.add(new AppResult(app.video, app.time));
                }
            };

            Thread queryThread = new Thread(queryRunnable);
            queryThread.start();
            threads.add(queryThread);
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Time: " + results.get(0).getTime());
        System.out.println("Video: " + results.get(0).getVideo());

        // VideoPlayer videoPlayer = new VideoPlayer(results.get(0).getVideo(), (int)results.get(0).getTime());
        // String arg[] = {results.get(0).getVideo(), ""+results.get(0).getTime()};
        //videoPlayer.play(arg);
    }
}
