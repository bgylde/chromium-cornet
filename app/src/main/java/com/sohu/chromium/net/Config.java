package com.sohu.chromium.net;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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

    private static final int MAX_FILE_CNT = 4;
    private static final int MAX_M3U8_TS_INDEX = 180;
    private static final String urlPrefix = "http%s://www.bgylde.com:%d/bipbop/gear%d/fileSequence%s.ts";

    public static String[] getUrls(boolean isHttps, int port, int maxUrlLength) {
        List<String> urls = new ArrayList<>();
        String https = "";
        if (isHttps) {
            https = "s";
        }

        GENETRAL_URL:
        for (int i = 1; i <= MAX_FILE_CNT; i++) {
            for (int j = 0; j <= MAX_M3U8_TS_INDEX; j++) {
                String url = formatString(urlPrefix, https, port, i, j);
                urls.add(url);
                if (urls.size() == maxUrlLength) {
                    break GENETRAL_URL;
                }
            }
        }

        return urls.toArray(new String[urls.size()]);
    }

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
