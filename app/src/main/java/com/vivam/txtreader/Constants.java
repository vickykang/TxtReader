package com.vivam.txtreader;

import java.util.regex.Pattern;

/**
 * Created by kangweodai on 17/01/17.
 */

public class Constants {

    /**
     * 获取文本章节名称的正则表达式
     * "\s*"：表示0到n个空白符
     * "^"：表示段落是以空白字符加上"第"作为开始的
     * ".{1,9}"： 章节序号，一般不超过9个，"."表示任意字符，"{1,9}"表示长多最小1次，最大9次
     * "[章节卷集部篇回]"：章节序号后面是修饰，"[]"匹配单字符
     * "\s*"：标题签名一般有空白字符， 也有可能没有，所以添加空白字符的匹配
     * ".{1,15}"：章节标题可以是任意字符，一般不超过15个字
     * "\n|\r\n"：一般章节过后就是换行，所以要有换行的匹配
     */
    public static final String REX_CHAPTER =
            "(^\\n|\\r\\n)(第)(.{1,9})[章节卷集部篇回](\\s*)(.{0,15})(\\n|\\r\\n)";

    /**
     * 获取匹配章节
     * @return
     */
    public static Pattern getChapterPattern() {
        return Pattern.compile(REX_CHAPTER);
    }

    public static final int STATE_FAILED = -1;
    public static final int STATE_LOADING = 0;
    public static final int STATE_EMPTY = 1;
    public static final int STATE_SUCCESS = 2;

    public static final int FONT_OFFSET = 2;
}
