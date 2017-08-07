package com.example.android.myapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import java.text.SimpleDateFormat;

/**
 * Created by android on 2017/6/15.
 */

public class PrintUtil {

    /**
     * 复位打印机
     */
    public static final byte[] RESET = {0x1b, 0x40};

    /**
     * 左对齐
     */
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /**
     * 中间对齐
     */
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /**
     * 右对齐
     */
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /**
     * 选择加粗模式 字体大小
     */
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};

    /**
     * 取消加粗模式 字体大小
     */
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /**
     * 宽高加倍 字体类型
     */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /**
     * 宽加倍 字体类型
     */
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /**
     * 高加倍 字体类型
     */
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /**
     * 宽字体 字体类型
     */
    public static final byte[] DOUBLE_BOLD = {0x1d, 0x21, 0x08};

    /**
     * 字体不放大 字体样式
     */
    public static final byte[] NORMAL_DEFAULT = {0x1d, 0x21, 0x00};

    /**
     * 设置默认行间距
     */
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};

    /** 字体对齐方式*/
//    typedef NS_ENUM(NSInteger, zTextAlignment) {
//        zTextAlignmentLeft = 0x00,
//                zTextAlignmentCenter = 0x01,
//                zTextAlignmentRight = 0x02
//    };

    /** 字体大小 */
//    typedef NS_ENUM(NSInteger, zFontSize) {
//        zFontSizeDefault = 0x00,
//                zFontSizeBig = 0x11
//    };

    /**
     * 字体类型
     */
//    typedef NS_ENUM(NSInteger, zFontStyle) {
//        zFontStyleDefault = 0x00,
//                zFontStyleMultipleHeight = 0x10,
//                zFontStyleBold  = 0x08
//    };

    private BluetoothService service;
    private int id;//
    private Context context;

    public PrintUtil(BluetoothService service, int id, Context context) {
        this.service = service;
        this.id = id;
        this.context = context;
    }

    /**
     * 初始化打印机
     * 打印机复位
     */
    public void initPrinter() {
        service.write(RESET);
    }

    /**
     * 空一行
     */
    public void defaultLineSpace() {
        byte lineSpace[] = {0x1B, 0x32};
        service.write(lineSpace);
    }

    /**
     * 设置绝对位置
     *
     * @param offset 传入位置参数
     */
    public void setOffset(int offset) {
        byte remainder = (byte) (offset % 256);
        byte consult = (byte) (offset / 256);
        byte spaceBytes2[] = {0x1B, 0x24, remainder, consult};
        service.write(spaceBytes2);
    }

    /**
     * 换行
     */
    public void appendReturn() {
        byte[] tail = new byte[]{10, 13, 0};//换行
        service.write(tail);
    }

    /**
     * 间隔线
     */
    public void getPrintSplitLine() {
        service.write(NORMAL_DEFAULT);
        String str = "";
        switch (id) {
            case 0:
                str = "------------------------------------------------";
                break;
            case 1:
                str = "---------------------------------------------------------------------";
                break;
            case 2:
                str = "-----------------------------------------------------------------------------------------------------------";
                break;
        }
        service.sendMessage(str, "GBK");
    }

    /**
     * @param points 行间距
     */
    public void setLineSpace(int points) {
        byte lineSpace[] = {0x1B, 0x33, (byte) points};
        service.write(lineSpace);
    }


    /**
     * 空行
     */
    public void feedLine(int number) {
        byte feedLine[] = {0x1B, 0x64, (byte) number};
        service.write(feedLine);
    }


    /**
     * 切刀
     */
    public void cutPaper() {
        feedLine(0x02);
        byte data[] = {0x1D, 0x56, 0x42, 0x00};
        service.write(data);
    }

    /**
     * 字体大小
     */
    public void setFontSize(int fontSize) {
        byte[] fontSizeBytes = {0x1D, 0x21, (byte) fontSize};
        service.write(fontSizeBytes);
    }

    /**
     * 字体样式
     */
    public void setFontStyle(int fontStyle) {
        byte fontStyleBytes[] = {0x1B, 0x21, (byte) fontStyle};
        service.write(fontStyleBytes);
    }

    /**
     * @param title 标题放大放宽
     */
    private void centerBigText(String title) {
        service.write(ALIGN_CENTER);
        if (id == 1) {
            service.write(DOUBLE_BOLD);
        }
        service.write(DOUBLE_HEIGHT_WIDTH);
        service.sendMessage(title);
        appendReturn();
        service.write(NORMAL_DEFAULT);
        service.write(BOLD_CANCEL);
    }

    public void printTwo(String left, String right) {
        service.write(ALIGN_LEFT);
        service.write(NORMAL_DEFAULT);
        service.sendMessage(left);
        switch (id) {
            case 0:
                setOffset(280);
                break;
            case 1:
                setOffset(410);
                break;
        }
        service.sendMessage(right);
        appendReturn();
    }

    /**
     * 合并标题内容
     *
     * @param titleText 标题名
     * @param num       联数
     */
    public void setTitleText(String titleText, int num) {
        feedLine(2);
        service.write(ALIGN_LEFT);
        service.sendMessage("【第" + num + 1 + "联】", "GBK");
        centerBigText(titleText);
        feedLine(1);
    }


    /**
     * 打印结尾内容
     */
    public void printFooterContent() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        switch (id) {
            case 0:
                service.sendMessage(getVersion(context) + " 客服电话 400-1818-975", "GBK");
                service.sendMessage("打印时间:" + date, "GBK");
                feedLine(1);
                cutPaper();
                break;
            case 1:
                service.sendMessage(getVersion(context) + " 客服电话 400-1818-975");
                setOffset(480);
                service.sendMessage("打印时间:" + date);
                feedLine(6);
                break;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return context.getString(R.string.app_name) + "  " + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "掌柜帮 2.0.0.0";
        }
    }

    /**
     * 80MM打印机样式
     *
     * @param strings 4个
     */
    public void titleTypeSettingContentDictionary(String... strings) {
        if (strings.length == 4) {
            setOffset(0);
            service.sendMessage(strings[0]);

            setOffset(170);
            service.sendMessage(strings[1]);

            setOffset(340);
            service.sendMessage(strings[2]);

            setOffset(470);
            service.sendMessage(strings[3]);

            appendReturn();
            getPrintSplitLine();
        }
    }


    /**
     * 打印抬头名
     */
    public void printeTitleName(String... strings) {
        if (strings.length == 5) {
            setOffset(0);
            service.sendMessage(strings[0]);

            setOffset(170);
            service.sendMessage(strings[1]);

            setOffset(340);
            service.sendMessage(strings[2]);

            setOffset(408);
            service.sendMessage(strings[3]);

            setOffset(470);
            service.sendMessage(strings[4]);

            appendReturn();
        }
    }

//    - (void)printThreeContent:(NSArray *)threeData {
//        for (NSDictionary * dict in threeData) {
//        [self setOffset:0];
//        [self setText:dict[@"k1"] maxChar:24];
//
//        [self setOffset:310];
//        [self setText:dict[@"k2"]];
//
//        [self setOffset:440];
//        [self setText:dict[@"k3"]];
//
//        [self appendReturn];
//        }
//    }

    /**
     * @param threes 三列
     */
    public void printThreeContent(String... threes) {

        setOffset(0);
        service.sendMessage(setStringLength(threes[0], 24));

        setOffset(310);
        service.sendMessage(threes[1]);

        setOffset(440);
        service.sendMessage(threes[2]);

        appendReturn();

    }

    /**
     * 80MM
     *
     * @param sales  销售数
     * @param amount 退货数
     * @param total  本单总金额
     */
    public void productSalesDetail(String sales, String amount, String total) {
        setOffset(0);
        service.sendMessage("销售数:" + sales);
        switch (id) {
            case 0:
                setOffset(280);
                break;
            case 1:
                setOffset(350);
                break;
        }
        service.sendMessage("退货数:" + amount, "GBK");
        service.sendMessage("本单总金额:" + total);
        appendReturn();
        getPrintSplitLine();
    }

//    /**
//     * 110MM
//     *
//     * @param sales  销售数
//     * @param amount 退货数
//     * @param total  本单总金额
//     */
//    public void productSales110MMDetail(String sales, String amount, String total) {
//        setOffset(0);
//        service.sendMessage("销售数:" + sales);
//        setOffset(350);
//        service.sendMessage("退货数:" + amount);
//        appendReturn();
//        setOffset(0);
//        service.sendMessage("本单总金额:" + total);
//        appendReturn();
//        getPrintSplitLine();
//    }


    /**
     * 80MM
     *
     * @param realPaid 实付金额
     * @param unpaid   未付金额
     */
    public void printPayDetail(String realPaid, String unpaid, String... strings) {
        service.sendMessage("本单总实付:" + realPaid);
        switch (id) {
            case 0:
                setOffset(280);
                break;
            case 1:
                setOffset(350);
                break;
        }
        service.sendMessage("未付:" + unpaid);
        appendReturn();
        for (String string : strings) {
            if (!string.equals("") && !string.equals("0.00") && !string.equals("0")) {
                service.sendMessage(string, "GBK");
            }
        }
        getPrintSplitLine();
    }


    /**
     * 限制字符串
     *
     * @param string 传入字符
     * @param max    最大字节
     * @return
     */
    private String setStringLength(String string, int max) {
        String str = string;
        if (string.length() > max) {
            str = string.substring(0, max);
        }
        return str;
    }


    /**
     * 110
     *
     * @param strings
     */
    public void printStoreInfoLeft(String... strings) {
        if (strings.length > 5) {
            setOffset(10);
            service.sendMessage(strings[0]);
            setOffset(75);
            service.sendMessage(strings[1]);
            setOffset(230);
            service.sendMessage(strings[2]);
            setOffset(530);
            service.sendMessage(strings[3]);
            appendReturn();
        }
    }

    /**
     * @param string 空格 单行文本
     */
    public void printOneLineText(String string) {
        setOffset(10);
        service.sendMessage(string, "GBK");
    }


    /**
     * @param strings 四个
     */
    public void fourItermsTitleDictionary(String... strings) {
        setOffset(0);
        service.sendMessage(strings[0]);

        setOffset(195);
        service.sendMessage(strings[1]);

        setOffset(400);
        service.sendMessage(strings[2]);

        setOffset(640);
        service.sendMessage(strings[3]);

        appendReturn();
    }

    /**
     * @param strings 5个
     */
    public void openTitleTypeSettingContentDictionary(String... strings) {
        service.write(NORMAL_DEFAULT);
        setOffset(0);
        service.sendMessage(strings[0]);
        setOffset(175);
        service.sendMessage(strings[1]);
        setOffset(400);
        service.sendMessage(strings[2]);
        setOffset(540);
        service.sendMessage(strings[3]);
        setOffset(690);
        service.sendMessage(strings[4]);
        appendReturn();
        getPrintSplitLine();
    }


    /**
     * @param strings 4g
     */
    public void printIntoStorageProductItermNo(String... strings) {
        setOffset(0);
        service.sendMessage(strings[0]);
        setOffset(195);
        service.sendMessage(strings[1]);
        setOffset(400);
        service.sendMessage(strings[2]);
        setOffset(640);
        service.sendMessage(strings[3]);
        appendReturn();
    }


    /**
     * @param strings 6个 款号     名称     颜色/尺码    单价  *  数量  小计
     */
    public void openPrintStyleNumber(String... strings) {
        service.sendMessage(strings[0]);
        setOffset(175);
        service.sendMessage(setStringLength(strings[1], 16));
        setOffset(400);
        service.sendMessage(strings[2]);
        setOffset(540);
        service.sendMessage(strings[3]);
        setOffset(613);
        service.sendMessage(strings[4]);
        setOffset(690);
        service.sendMessage(strings[5]);
        appendReturn();
    }

    /**
     * @param strings 5个 款号     颜色              尺码(数量)              单价*数量       小计
     */
    public void titleType110MMSettingContentDictionary(String... strings) {
        setOffset(0);
        service.sendMessage(strings[0]);
        setOffset(130);
        service.sendMessage(strings[1]);
        setOffset(330);
        service.sendMessage(strings[2]);
        setOffset(570);
        service.sendMessage(strings[3]);
        setOffset(710);
        service.sendMessage(strings[4]);
        appendReturn();
        getPrintSplitLine();
    }

    /**
     *
     */
    public void allColor(String nameAndItem, String color, String[] sizes, String price, String num, String tol) {
        setOffset(0);
        service.sendMessage(nameAndItem);
        setOffset(130);
        service.sendMessage(color);
        setOffset(240);
        if (sizes != null) {
            int offset = 120;
            int startOffset = 240;
            int onePagerNum = 1;
            for (int i = 1; i < sizes.length + 1; i++) {
                service.sendMessage(sizes[i - 1]);
                setOffset(startOffset + offset * onePagerNum);
                onePagerNum++;
                if (i % 3 == 0) {
                    if (i != sizes.length) {
                        appendReturn();
                    }
                    setOffset(240);
                    onePagerNum = 1;
                }
            }
        }
        setOffset(570);
        service.sendMessage(price);
        setOffset(643);
        service.sendMessage(num);
        setOffset(710);
        service.sendMessage(tol);
        appendReturn();
    }

    /**
     * @param total 总数
     * @return 总页数
     * onePager 一页几个
     */
    public int getAllPager(int total) {
        int pageCount = total / 3 + 1;
        if (total % 3 == 0) {
            pageCount = total / 3;
        }
        return pageCount;
    }

    //*****************************************************************************

    /**
     * 主标题
     *
     * @param title 标题名字
     */
    public void printMainTitle(String title) {
        appendReturn();
        w2h2Set();
        setOffset(180);
        service.sendMessage(title);
        w2h2Cancel();
    }


    public void w2h2Set() {
        byte fontSizeBytes[] = {0x1C, 0x57, 0x01};
        service.write(fontSizeBytes);
    }

    public void w2h2Cancel() {
        byte fontSizeBytes[] = {0x1C, 0x57, 0x00};
        service.write(fontSizeBytes);
    }


    /**
     * 第一行
     *
     * @param strings 4g
     */
    public void printBillNum(String... strings) {
        setOffset(0);
        service.sendMessage(strings[0]);
        setOffset(100);
        service.sendMessage(strings[1]);
        setOffset(260);
        service.sendMessage(strings[2]);
        setOffset(340);
        service.sendMessage(strings[3]);
    }


    public void printProductSerialNum(String... strings) {
        setOffset(0);
        service.sendMessage(strings[0]);
        setOffset(30);
        service.sendMessage(strings[1]);
        setOffset(100);
        service.sendMessage(strings[2]);
        setOffset(190);
        service.sendMessage(strings[3]);
        setOffset(250);
        service.sendMessage(strings[4]);
        setOffset(300);
        service.sendMessage(strings[5]);
        setOffset(340);
        service.sendMessage(strings[6]);
        setOffset(400);
        service.sendMessage(strings[7]);
        appendReturn();
    }
       /**  货品信息标题  */
    public void printProductSerialTitle(String... strings) {
        fontBoldSet();
        setOffset(0);
        service.sendMessage(strings[0]);
        setOffset(110);
        service.sendMessage(strings[1]);
        setOffset(250);
        service.sendMessage(strings[2]);
        setOffset(300);
        service.sendMessage(strings[3]);
        setOffset(340);
        service.sendMessage(strings[4]);
        setOffset(400);
        service.sendMessage(strings[5]);
        fontBoldCancel();
    }


    public void fontBoldSet() {
        byte fontSizeBytes[] = {0x1B, 0x45};
        service.write(fontSizeBytes);
    }

    public void fontBoldCancel() {
        byte fontSizeBytes[] = {0x1B, 0x46};
        service.write(fontSizeBytes);
    }

    /**
     * @param num     6
     * @param strings 数组
     */
    public void printProductSerialNum(int num, String... strings) {
        fontBoldSet();
        setOffset(0);
        service.sendMessage(strings[0]);
        setOffset(110);
        service.sendMessage(strings[1]);
        setOffset(250);
        service.sendMessage(strings[2]);
        setOffset(300);
        service.sendMessage(strings[3]);
        setOffset(340);
        service.sendMessage(strings[4]);
        setOffset(400);
        service.sendMessage(strings[5]);
        fontBoldCancel();
    }

    /**
     * 金额统计
     */
    public void printTotalMoneyWithArr1(String[] strings, String[] strings1) {
        fontBoldSet();
        //总计 、付款信息
        for (int discount = 0; discount < strings.length; discount++) {
            if (discount > 0 && discount % 4 == 0) {
                appendReturn();
            }
            setOffset(discount % 4 * 130);
            service.sendMessage(strings[discount]);
        }
        if (strings1.length > 0) {
            appendReturn();
            //余额信息
            for (int discount = 0; discount < strings1.length; discount++) {
                setOffset(discount % 4 * 130);
                service.sendMessage(strings[discount]);
            }
        }
        fontBoldCancel();
    }

    /**
     * 联系方式、地址、账号、门店和开单备注
     */
    public void printContactInfo(String[] lianxifangshi, String dizhi, String[] zhanghaoxinxi, String[] mendianxinxi, String billRemark) {
        service.sendMessage("联系方式:");
        for (int discount = 0; discount < lianxifangshi.length; discount++) {
            if (discount > 0 && discount % 4 == 0) {
                appendReturn();
            }
            setOffset(48 + discount % 4 * 118);
            service.sendMessage(lianxifangshi[discount]);
        }
        appendReturn();
        if (!dizhi.equals("")) {
            service.sendMessage(dizhi);
            appendReturn();
        }

        //账号信息
        for (int discount = 0; discount < zhanghaoxinxi.length; discount++) {
            if (discount > 0 && discount % 2 == 0) {
                appendReturn();
            }
            setOffset(discount % 2 * 260);
            service.sendMessage(zhanghaoxinxi[discount]);
        }
        appendReturn();
        //门店备注 mendianxinxi
        for (int discount = 0; discount < mendianxinxi.length; discount++) {

            if (discount > 0) {
                appendReturn();
            }
            service.sendMessage(mendianxinxi[discount]);

        }
        if (!billRemark.equals("")) {
            appendReturn();
            service.sendMessage(billRemark);
        }
    }

    /**
     * 版本、打印日期
     */
    public void printVersion(String version, String printDate) {
        setOffset(0);
        service.sendMessage(version);
        setOffset(260);
        service.sendMessage(printDate);
    }

    public void defaultSettingPinBT() {
        byte BytesUnit[] = {0x1B, 0x28, 0x55, 1, 0, 60}; //定义单位
        service.write(BytesUnit);
        byte pageLengthBytes[] = {0x1B, 0x28, 0x43, 2, 0, 74, 1}; // 按单位设置页长
        service.write(pageLengthBytes);
        byte pageSpaceBytes[] = {0x1B,0x4E,4}; // 设置页缝空白
        service.write(pageSpaceBytes);
    }

    public void lineSpacePinTBSet(){
        byte fontSizeBytes[] = {0x1B,0x41,15};
        service.write(fontSizeBytes);
    }


    public void nextPage(){
        byte fontSizeBytes[] = {0x0C};
        service.write(fontSizeBytes);
    }

}
