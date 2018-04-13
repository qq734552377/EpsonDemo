package com.ucast.jnidiaoyongdemo.mytime;

/**
 * Created by Allen on 2017/2/9.
 */

public class MyTimer {

    public MyTimeTask myTimeTask;
    public Long startTime = 1000L;
    //循环时间
    //time in milliseconds between successive task executions.
    public Long period    = 1000*5L;

    public MyTimer(MyTimeTask myTimeTask) {
        this.myTimeTask = myTimeTask;
    }


    public MyTimer(MyTimeTask myTimeTask, Long startTime, Long period) {
        this.myTimeTask = myTimeTask;
        this.startTime = startTime;
        this.period = period;
    }
    /**
     * @category 初始化自定义定时线程
     */
    public  MyTimer initMyTimer() {
        myTimeTask.setFlag(true);
        myTimeTask.setStartTime(startTime);
        myTimeTask.setPeriod(period);
        System.out.println("初始化自定义定时线程");
        return this;
    }

    /**
     * @category 开启自定义定时线程
     *
     */
    public  void startMyTimer() {
        myTimeTask.start();
        System.out.println("开启自定义定时线程");
    }

    /**
     * @category 停止自定义定时线程
     */
    public  void stopMyTimer() {
        myTimeTask.setFlag(false);
        myTimeTask.setOn(false);
        System.out.println("停止自定义定时线程");
    }
}
