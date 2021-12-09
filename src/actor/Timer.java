//package actor;
//
//public class Timer extends Runnable {
//    private Object objectToNotify;
//    private long time;
//    private boolean exit;
//
//    public Timer(long time, Object obj) {
//        this.objectToNotify = obj;
//        this.time = time;
//    }
//
//    @Override
//    public void run() {
//        this.exit = false;
//        while (!exit && (System.currentTimeMillis - Manager.startTime) < time);
//        this.running = false;
//        if (!exit)
//            synchronized (objectToNotify) {
//                objectToNotify.notify();
//            }
//    }
//
//    public void stop() {
//        this.exit = true;
//    }
//}
