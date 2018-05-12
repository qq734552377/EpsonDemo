package com.ucast.jnidiaoyongdemo.jsonObject;

/**
 * Created by pj on 2018/5/12.
 */
public class TicketMsgResult {
    //打印的信息
    public String Str ;
    //需要生成二维码的信息
    public String Link ;
    //需要打印的图片的现在地址
    public String Img ;
    //返回小票的结算金额
    public String Amount;

    public String getStr() {
        return Str;
    }

    public void setStr(String str) {
        Str = str;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
