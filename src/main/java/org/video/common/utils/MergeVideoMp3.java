package org.video.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gutongxue
 * @date 2019/11/19 9:23
 **/
public class MergeVideoMp3 {
    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath, String mp3InputPath, String seconds, String videoOutputPath) throws Exception{
        //ffmpeg.exe -i video1.mp4 -i bgm.mp3 -t 4 -y output.mp4

        List<String> command = new ArrayList<>();
        command.add(this.ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(seconds);

        command.add("-y");
        command.add(videoOutputPath);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";
        //进行读取释放
        while ((line = bufferedReader.readLine()) != null) {

        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }

    public static void main(String[] args) {
        MergeVideoMp3 ffMpegTest = new MergeVideoMp3("D:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("D:\\wxxcx\\video1.mp4", "D:\\wxxcx\\userfiles\\bgm\\卡路里.mp3", "4", "D:\\wxxcx\\合并.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
