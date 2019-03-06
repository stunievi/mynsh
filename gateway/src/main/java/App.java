import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableCallable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

//    public static class Battle {
//        Random rand = new Random();
//        int num = 1000;
//        Actor[] actors = new Actor[num];
//        AtomicInteger living = new AtomicInteger(num);
//
//
//        public class Actor extends Task {
//            Mailbox<Integer> damage = new Mailbox<>();
//            int hp = 1 + rand.nextInt(10);
//
//            public void execute() throws Pausable {
//                while (hp > 0) {
//                    hp -= damage.get();
//                    actors[rand.nextInt(num)].damage.putnb(1);
//                    actors[rand.nextInt(num)].damage.putnb(1);
//                    actors[rand.nextInt(num)].damage.putnb(1);
//                    Task.sleep(100);
//                }
//                living.decrementAndGet();
//            }
//        }
//
//        void start() {
//            for (int ii = 0; ii < num; ii++) (actors[ii] = new Actor()).start();
//            actors[0].damage.putb(1);
//
//            for (int cnt, prev = num; (cnt = living.get()) > num / 2 || cnt < prev; prev = cnt, sleep())
//                System.out.println(cnt);
//
//        }
//
//        void sleep() {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//            }
//        }
//    }

    public static void main(String[] args){
//        Battle battle = new Battle();
//        battle.start();
//        Task.idledown();

//        System.out.format("\n%d actors survived the Battle Royale\n\n",battle.living.get());

        Channel<Object> channel = Channels.newChannel(10);
        AtomicInteger c = new AtomicInteger(1);

        Fiber<Object> a = new Fiber<Object>(
                () -> {
                    while (true) {
                        Fiber.sleep(1000);
                        channel.send(c.getAndIncrement());
                        System.out.println(Thread.currentThread().getName());
                    }
                }
        ).start();

        new Fiber<Object>(() -> {
            while(true){
                int aa = (int) channel.receive();
                System.out.println(aa);
                System.out.println(Thread.currentThread().getName());
            }
        }).start();

        new Fiber<Object>(new SuspendableCallable<Object>() {
            @Override
            public Object run() throws SuspendExecution, InterruptedException {
                while(true){
                    Fiber.sleep(1000);
                }
            }
        }).start();


        try {
            a.join();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
