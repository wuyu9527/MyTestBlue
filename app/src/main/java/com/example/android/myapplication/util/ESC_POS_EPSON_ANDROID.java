/**
 * Copyright (c) <2015>, <Christian Ebner cebner@gmx.at>
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.android.myapplication.util;

import com.zj.btsdk.BluetoothService;

import java.nio.charset.Charset;

/**
 * EPSON ESC/POS Commands
 * Created by ebc on 05.01.2015.
 * http://content.epson.de/fileadmin/content/files/RSD/downloads/escpos.pdf
 */
public class ESC_POS_EPSON_ANDROID {

    public BluetoothService mService = null;


    public static final byte ESC = 27;
    public static final byte FS = 28;
    public static final byte GS = 29;
    public static final byte DLE = 16;
    public static final byte EOT = 4;
    public static final byte ENQ = 5;
    public static final byte SP = 32;
    public static final byte HT = 9;
    public static final byte LF = 10;
    public static final byte CR = 13;
    public static final byte FF = 12;
    public static final byte CAN = 24;

    /**
     * 代码表
     */
    public static class CodePage {
        public static final byte PC437 = 0;
        public static final byte KATAKANA = 1;
        public static final byte PC850 = 2;
        public static final byte PC860 = 3;
        public static final byte PC863 = 4;
        public static final byte PC865 = 5;
        public static final byte WPC1252 = 16;
        public static final byte PC866 = 17;
        public static final byte PC852 = 18;
        public static final byte PC858 = 19;
    }

    /**
     * 条码表
     */
    public static class BarCode {
        public static final byte UPC_A = 0;
        public static final byte UPC_E = 1;
        public static final byte EAN13 = 2;
        public static final byte EAN8 = 3;
        public static final byte CODE39 = 4;
        public static final byte ITF = 5;
        public static final byte NW7 = 6;
        //public static final byte CODE93      = 72;
        // public static final byte CODE128     = 73;
    }


    public ESC_POS_EPSON_ANDROID(BluetoothService mService) {
        this.mService = mService;
    }


    /**
     * 打印换行
     * LF
     *
     * @return bytes for this command
     */
    public byte[] print_linefeed() {
        byte[] result = new byte[1];
        result[0] = LF;
        mService.write(result);
        return result;
    }

    /**
     * 又强调模式，设置在1-dot宽度
     * ESC - n
     *
     * @return bytes for this command
     */
    public byte[] underline_1dot_on() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 1;
        mService.write(result);
        return result;
    }

    /**
     * 又强调模式，设置在2-dot宽度
     * ESC - n
     *
     * @return bytes for this command
     */
    public byte[] underline_2dot_on() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 2;
        mService.write(result);
        return result;
    }

    /**
     * 把underline离线模式
     * ESC - n
     *
     * @return bytes for this command
     */
    public byte[] underline_off() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 0;
        mService.write(result);
        return result;
    }


    /**
     * @return bytes for this command
     * 初始化打印机
     * 清除打印缓冲区中的数据，并将打印机模式重置为所述模式
     */
    public byte[] init_printer() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 64;
        mService.write(result);
        return result;
    }

    /**
     * 打开强调模式
     * ESC E n
     * @return bytes for this command
     */
    public byte[] emphasized_on() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        mService.write(result);
        return result;
    }

    /**
     * 关闭强调模式
     * ESC E n
     *
     * @return bytes for this command
     */
    public byte[] emphasized_off() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        mService.write(result);
        return result;
    }

    /**
     * double_strike_on
     * ESC G n
     *
     * @return bytes for this command
     */
    public byte[] double_strike_on() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 71;
        result[2] = 0xF;
        mService.write(result);
        return result;
    }

    /**
     * double_strike_off
     * ESC G n
     *
     * @return bytes for this command
     */
    public byte[] double_strike_off() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 71;
        result[2] = 0xF;
        mService.write(result);
        return result;
    }

    /**
     * 选择字体A
     * ESC M n
     * @return bytes for this command
     */
    public byte[] select_fontA() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 0;
        mService.write(result);
        return result;
    }

    /**
     * 选择字体B
     * ESC M n
     * @return bytes for this command
     */
    public byte[] select_fontB() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 1;
        mService.write(result);
        return result;
    }

    /**
     * 选择字体 C ( some printers don't have font C )
     * ESC M n
     *
     * @return bytes for this command
     */
    public byte[] select_fontC() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 2;
        mService.write(result);
        return result;
    }

    /**
     * 双模式的字体高度、宽度 A
     * ESC ! n
     *
     * @return bytes for this command
     */
    public byte[] double_height_width_on() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 56;
        mService.write(result);
        return result;
    }

    /**
     * 双高度宽度模式关闭字体 A
     * ESC ! n
     *
     * @return bytes for this command
     */
    public byte[] double_height_width_off() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 0;
        mService.write(result);
        return result;
    }

    /**
     * 选择双模式字体高度 A
     * ESC ! n
     *
     * @return bytes for this command
     */
    public byte[] double_height_on() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 16;
        mService.write(result);
        return result;
    }

    /**
     * 禁用双高度模式，选择字体 A
     * ESC ! n
     *
     * @return bytes for this command
     */
    public byte[] double_height_off() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 0;
        mService.write(result);
        return result;
    }

    /**
     * justification_left 居左
     * ESC a n
     *
     * @return bytes for this command
     */
    public byte[] justification_left() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 0;
        mService.write(result);
        return result;
    }

    /**
     * justification_center居中
     * ESC a n
     *
     * @return bytes for this command
     */
    public byte[] justification_center() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 1;
        mService.write(result);
        return result;
    }

    /**
     * justification_right居右
     * ESC a n
     *
     * @return bytes for this command
     */
    public byte[] justification_right() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 2;
        mService.write(result);
        return result;
    }

    /**
     * 打印和填充n行
     *打印打印缓冲区中的数据并馈送n行
     * ESC d n
     *
     * @param n lines
     * @return bytes for this command
     */
    public byte[] print_and_feed_lines(byte n) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 100;
        result[2] = n;
        mService.write(result);
        return result;
    }

    /**
     * 打印和反向进给的n行
     *打印打印缓冲区中的数据，并在保留方向上填充n行。
     * ESC e n
     *
     * @param n lines
     * @return bytes for this command
     */
    public byte[] print_and_reverse_feed_lines(byte n) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 101;
        result[2] = n;
        mService.write(result);
        return result;
    }

    /**
     * 抽屉踢
     *  抽屉踢出连接器的引脚2
     * ESC p m t1 t2
     *
     * @return bytes for this command
     */
    public byte[] drawer_kick() {
        byte[] result = new byte[5];
        result[0] = ESC;
        result[1] = 112;
        result[2] = 0;
        result[3] = 60;
        result[4] = 120;
        mService.write(result);
        return result;
    }

    /**
     * 选择打印彩色1
     * ESC r n
     *
     * @return bytes for this command
     */
    public byte[] select_color1() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 114;
        result[2] = 0;
        mService.write(result);
        return result;
    }

    /**
     * 选择印刷色彩鲜艳
     * ESC r n
     *
     * @return bytes for this command
     */
    public byte[] select_color2() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 114;
        result[2] = 1;
        mService.write(result);
        return result;
    }

    /**
     * 选择字符代码表
     * ESC t n
     *
     * @param cp example:CodePage.WPC1252
     * @return bytes for this command
     */
    public byte[] select_code_tab(byte cp) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 116;
        result[2] = cp;
        mService.write(result);
        return result;
    }

    /**
     * 白色打印模式
     *把黑白印刷反模式
     * GS B n
     *
     * @return bytes for this command
     */
    public byte[] white_printing_on() {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 66;
        result[2] = (byte) 128;
        mService.write(result);
        return result;
    }

    /**
     * 白色打印模式关闭
     *打开白色/黑色反向打印模式
     * GS B n
     *
     * @return bytes for this command
     */
    public byte[] white_printing_off() {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 66;
        result[2] = 0;
        mService.write(result);
        return result;
    }

    /**
     * 饲料纸和切
     *送纸至（切割位置+垂直垂直运动单位）
     *并执行一个完整的削减（完全削减纸张）
     *
     * @return bytes for this command
     */
    public byte[] feedpapercut() {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 65;
        result[3] = 0;
        mService.write(result);
        return result;
    }

    /**
     *饲料纸和切部分
     *送纸至（切割位置+垂直垂直运动单位）
     *执行部分切割（一点左未切割）
     *
     * @return bytes for this command
     */
    public byte[] feedpapercut_partial() {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 66;
        result[3] = 0;
        mService.write(result);
        return result;
    }

    /**
     *选择条码高度
     *选择条形码的高度作为n个点
     *默认点= 162
     *
     * @param dots ( heigth of the bar code )
     * @return bytes for this command
     */
    public byte[] barcode_height(byte dots) {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 104;
        result[2] = dots;
        mService.write(result);
        return result;
    }

    /**
     * 选择字体HRI
     *选择一个为人类可读的解释字体（HRI）字符时，打印条码，使用n如下：
     *
     * @param n Font
     *          0, 48 Font A
     *          1, 49 Font B
     * @return bytes for this command
     */
    public byte[] select_font_hri(byte n) {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 102;
        result[2] = n;
        mService.write(result);
        return result;
    }

    /**
     * 选择position_hri
     * 以人类可读的解释打印位置（HRI）字符打印条形码时，使用N如下：
     *
     * @param n Print position
     *          0, 48 Not printed
     *          1, 49 Above the barcode
     *          2, 50 Below the barcode
     *          3, 51 Both above and below the barcode
     * @return bytes for this command
     */
    public byte[] select_position_hri(byte n) {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 72;
        result[2] = n;
        mService.write(result);
        return result;
    }

    /**
     *打印条形码
     *
     * @param barcode_typ(Barcode.CODE39,Barcode.EAN8,...)
     * @param barcode2print
     * @return bytes for this command
     */
    public byte[] print_bar_code(byte barcode_typ, String barcode2print) {
        byte[] barcodebytes = barcode2print.getBytes();
        byte[] result = new byte[3 + barcodebytes.length + 1];
        result[0] = GS;
        result[1] = 107;
        result[2] = barcode_typ;
        int idx = 3;

        for (int i = 0; i < barcodebytes.length; i++) {
            result[idx] = barcodebytes[i];
            idx++;
        }
        result[idx] = 0;
        mService.write(result);

        return result;
    }


    /**
     * Set horizontal tab positions
     *
     * @param col ( coulumn )
     * @return bytes for this command
     */
    public byte[] set_HT_position(byte col) {
        byte[] result = new byte[4];
        result[0] = ESC;
        result[1] = 68;
        result[2] = col;
        result[3] = 0;
        mService.write(result);
        return result;
    }

    /**
     * print_line
     * adds a LF command to the text
     *
     * @param line (text to print)
     */
    public void print_line(String line) {
        if (line.isEmpty()) return;
        mService.write(line.getBytes(Charset.forName("GB2312")));
        print_linefeed();
    }

    /**
     * print_text
     * without LF , means text is not printed immediately
     *
     * @param line (text to print)
     */
    public void print_text(String line) {
        if (line.isEmpty()) return;
        //mService.write(line.getBytes());
        mService.write(line.getBytes(Charset.forName("GB2312")));
    }


    public void print_sample1() {
        String test = null;
        init_printer();
        select_fontA();
        select_code_tab(ESC_POS_EPSON_ANDROID.CodePage.WPC1252);
        underline_1dot_on();
        justification_center();
        test = "Sample Receipt 1";
        print_line(test);
        test = "Umlaute";
        print_line(test);
        double_height_width_on();
        test = "ÄÖÜß";
        print_line(test);
        double_height_width_off();
        feedpapercut();
    }


    public void print_sample() {
        String test = null;
        init_printer();
        select_fontA();
        underline_1dot_on();
        justification_center();
        test = "Sample Receipt";
        print_line(test);
        underline_off();
        print_linefeed();
        justification_left();
        test = "Left justification";
        print_text(test);
        print_linefeed();
        justification_right();
        test = "right justification";
        print_line(test);
        print_linefeed();
        justification_left();
        test = "Testzeile\tTab1\tTab2";
        print_line(test);
        set_HT_position((byte) 35); //Set horizontal tab positions: 35th column
        test = "Testzeile\tTab1";
        print_line(test);
        emphasized_on();
        test = "emphasized_on";
        print_line(test);
        emphasized_off();
        underline_2dot_on();
        justification_right();
        test = "underline 2dot";
        print_line(test);
        underline_off();
        double_strike_on();
        test = "double strike";
        print_line(test);
        double_strike_off();
        select_fontB();
        test = "Font B";
        print_line(test);
        white_printing_on();
        test = "white printing on";
        print_line(test);
        white_printing_off();
        print_and_feed_lines((byte) 3);
        select_position_hri((byte) 2);
        print_bar_code(BarCode.CODE39, "123456789");
        print_linefeed();
        print_and_feed_lines((byte) 2);
        barcode_height((byte) 80);
        justification_center();
        select_position_hri((byte) 1);
        print_bar_code(BarCode.EAN13, "9783125171541");
        print_linefeed();
        feedpapercut();

    }
}
