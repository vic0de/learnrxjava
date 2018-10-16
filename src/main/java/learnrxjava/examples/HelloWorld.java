package learnrxjava.examples;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HelloWorld {

    public static void main(String[] args) {

        // Hello World
        Observable.create(subscriber -> {
            subscriber.onNext("Hello World!");
            subscriber.onComplete();
        }).subscribe(System.out::println);

        // shorten by using helper method
        Observable.just("Hello", "World!")
                .subscribe(System.out::println);

        // add onError and onComplete listeners
        Observable.just("Hello World!")
                .subscribe(System.out::println,
                        Throwable::printStackTrace,
                        () -> System.out.println("Done"));

        // expand to show full classes
        ObservableOnSubscribe<String> handler = emitter -> {
        	emitter.onNext("Hello World!");
        	emitter.onComplete();
        };
        
        Observable.create(handler).subscribe(new Consumer<String>() {

			@Override
			public void accept(String t) throws Exception {
                System.out.println(t);
			}
		});
    

        Observable.create(emitter -> {
        	 
        	try {
        		emitter.onNext("Hello World!");
        		emitter.onComplete();
        	}catch(Exception ex) {
        		emitter.onError(ex);
        	}
        	
        }).subscribe(System.out::println);

        // add concurrency (manually)
        Observable.create(emitter -> {
            new Thread(() -> {
                try {
                	emitter.onNext(getData());
                	emitter.onComplete();
                } catch (Exception e) {
                	emitter.onError(e);
                }
            }).start();
        }).subscribe(System.out::println);

        // add concurrency (using a Scheduler)
        Observable.create(emitter -> {
            try {
            	emitter.onNext(getData());
            	emitter.onComplete();
            } catch (Exception e) {
            	emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(System.out::println);

        // add operator
        Observable.create(emitter -> {
            try {
            	emitter.onNext(getData());
            	emitter.onComplete();
            } catch (Exception e) {
            	emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .map(data -> data + " --> at " + System.currentTimeMillis())
                .subscribe(System.out::println);

        // add error handling
        Observable.create(emitter -> {
            try {
            	emitter.onNext(getData());
            	emitter.onComplete();
            } catch (Exception e) {
            	emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .map(data -> data + " --> at " + System.currentTimeMillis())
                .onErrorResumeNext(e -> {
                	return Observable.just("Fallback Data");})
                .subscribe(System.out::println);

        // infinite
        Observable.create(emitter -> {
            int i = 0;
            while (!emitter.isDisposed()) {
            	emitter.onNext(i++);
            }
        }).take(10).subscribe(System.out::println);

        //Hello World
        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).onErrorResumeNext(throwable -> {
            return Observable.just("fallback value");
        }).subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).onErrorResumeNext(Observable.just("fallback value"))
                .subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).onErrorReturn(throwable -> {
            return "fallback value";
        }).subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (throwable, i) -> i)
                    .flatMap(i -> {
                        System.out.println("delay retry by " + i + " second(s)");
                        return Observable.timer(i, TimeUnit.SECONDS);
                    }).concatWith(Observable.error(new RuntimeException("Exceeded 3 retries")));
        })
                .subscribe(System.out::println, t -> t.printStackTrace());

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private static String getData() {
        return "Got Data!";
    }

}
