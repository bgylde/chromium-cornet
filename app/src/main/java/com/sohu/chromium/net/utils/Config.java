package com.sohu.chromium.net.utils;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by wangyan on 2019/3/29
 */
public class Config {
    public static String[] quicTestUrls = {
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.0.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.1.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.2.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.3.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.4.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.5.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.6.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.7.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.8.ts",
            "https://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.9.ts"
    };

    public static String[] httpTestUrls = {
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.0.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.1.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.2.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.3.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.4.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.5.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.6.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.7.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.8.ts",
            "http://www.bgylde.com/m3u8/s0029flk3iu_p212_mp4_av.1.9.ts"
    };

    public static String[] http2TestUrls = {
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.0.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.1.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.2.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.3.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.4.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.5.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.6.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.7.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.8.ts",
            "https://www.bgylde.com:8080/m3u8/s0029flk3iu_p212_mp4_av.1.9.ts"
    };

    public static String formatUnion(long length) {
        String result = "";
        byte index = 0;
        double resLength = length;
        if (length > 1024) {
            index = 1;
            resLength = resLength / 1024;
            if (resLength > 1024) {
                index = 2;
                resLength = resLength / 1024;
            }
        }

        DecimalFormat df = new DecimalFormat("#####0.00");
        switch (index) {
            case 0:
                result = formatString("%sB", df.format(resLength));
                break;
            case 1:
                result = formatString("%sKB", df.format(resLength));
                break;
            case 2:
                result = formatString("%sMB", df.format(resLength));
                break;
        }

        return result;
    }

    public static String formatTime(int time) {
        String result = "";
        double resultTime = time;
        byte index = 0;

        if (resultTime > 1000) {
            index = 1;
            resultTime = resultTime / 1000;
            if (resultTime > 60) {
                index = 2;
                resultTime = resultTime / 60;
            }
        }

        DecimalFormat df = new DecimalFormat("#####0.00");
        switch (index) {
            case 0:
                result = formatString("%s ms", df.format(resultTime));
                break;
            case 1:
                result = formatString("%s s", df.format(resultTime));
                break;
            case 2:
                result = formatString("%s m", df.format(resultTime));
                break;
        }

        return result;
    }

    public static String formatString(String format, Object... objects) {
        return String.format(Locale.CHINA, format, objects);
    }
}
