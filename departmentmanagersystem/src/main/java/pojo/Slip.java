package pojo;

import java.io.Serializable;

/**
 * @author ylr
 */
public class Slip implements Serializable {
    private int smallWidth;     //小图片宽度
    private int smallHeight;    //小图片高度
    private int bigWidth;       //大图片宽度
    private int bigHeight;      //大图片高度
    private int circleR;        //小圆半径
    private int good;       //成功范围
    private int randX;      //抠图X坐标
    private int randY;      //抠图Y坐标
    private String url;     //图片路径
    private String smallBase64;     //小图片base64
    private String bigBase64;       //大图片base64

    public int getSmallWidth() {
        return smallWidth;
    }

    public void setSmallWidth(int smallWidth) {
        this.smallWidth = smallWidth;
    }

    public int getSmallHeight() {
        return smallHeight;
    }

    public void setSmallHeight(int smallHeight) {
        this.smallHeight = smallHeight;
    }

    public int getBigWidth() {
        return bigWidth;
    }

    public void setBigWidth(int bigWidth) {
        this.bigWidth = bigWidth;
    }

    public int getBigHeight() {
        return bigHeight;
    }

    public void setBigHeight(int bigHeight) {
        this.bigHeight = bigHeight;
    }

    public int getCircleR() {
        return circleR;
    }

    public void setCircleR(int circleR) {
        this.circleR = circleR;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getRandX() {
        return randX;
    }

    public void setRandX(int randX) {
        this.randX = randX;
    }

    public int getRandY() {
        return randY;
    }

    public void setRandY(int randY) {
        this.randY = randY;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSmallBase64() {
        return smallBase64;
    }

    public void setSmallBase64(String smallBase64) {
        this.smallBase64 = smallBase64;
    }

    public String getBigBase64() {
        return bigBase64;
    }

    public void setBigBase64(String bigBase64) {
        this.bigBase64 = bigBase64;
    }
}
