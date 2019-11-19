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
public class FFMpegTest {
    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath, String videoOutputPath) throws Exception{
        //$ ffmpeg -i input.mp4 output.avi

        List<String> command = new ArrayList<>();
        command.add(this.ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutputPath);
        for (String s : command) {
            System.out.print(s + " ");
        }
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
        FFMpegTest ffMpegTest = new FFMpegTest("D:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("D:\\wxxcx\\video1.mp4", "D:\\wxxcx\\haha.avi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
