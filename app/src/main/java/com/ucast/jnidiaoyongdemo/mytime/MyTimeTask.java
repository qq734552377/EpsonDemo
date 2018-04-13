package com.ucast.jnidiaoyongdemo.mytime;

/**
 * Created by Allen on 2017/2/9.
 */

public class MyTimeTask extends Thread {

    //线程开关
    public boolean isOn=true;
    //开关控制标志位
    public boolean flag = true;
    //开始时间
    //delay in milliseconds before task is to be executed.
    public Long startTime = 1000L;
    //循环时间
    //time in milliseconds between successive task executions.
    public Long period = 1000 * 5L;

    public Runnable task;

    public MyTimeTask(Runnable task) {
        this.task = task;
    }

    public MyTimeTask(Runnable task, Long startTime, Long period) {
        this.task = task;
        this.startTime = startTime;
        this.period = period;
    }

    @Override
    public void run() {
        while (isOn) {
            try {
                //开始时间
                if (startTime > 0) {
                    Thread.sleep(startTime);
                    startTime = -1L;
                }
                //只有当flag为true时，才采集相关信息
                while (flag) {
                    //业务逻辑处理块
                    if (task != null) {
                        task.run();
                    } else {
                        return;
                    }
                    //循环时间
                    Thread.sleep(period);
                }
                //当flag为false时，线程休息中
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }
}
