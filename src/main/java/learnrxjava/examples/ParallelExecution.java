package learnrxjava.examples;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class ParallelExecution {

    public static void main(String[] args) {
        System.out.println("------------ mergingAsync");
        mergingAsync();
        System.out.println("------------ mergingSync");
        mergingSync();
        System.out.println("------------ mergingSyncMadeAsync");
        mergingSyncMadeAsync();
        System.out.println("------------ flatMapExampleSync");
        flatMapExampleSync();
        System.out.println("------------ flatMapExampleAsync");
        flatMapExampleAsync();
        System.out.println("------------ flatMapBufferedExampleAsync");
        flatMapBufferedExampleAsync();
        System.out.println("------------ flatMapWindowedExampleAsync");
        flatMapWindowedExampleAsync();
        System.out.println("------------");
    }

    private static void mergingAsync() {
        Observable.merge(getDataAsync(1), getDataAsync(2)).blockingForEach(System.out::println);
    }

    private static void mergingSync() {
        // here you'll see the delay as each is executed synchronously
        Observable.merge(getDataSync(1), getDataSync(2)).blockingForEach(System.out::println);
    }

    private static void mergingSyncMadeAsync() {
        // if you have something synchronous and want to make it async, you can schedule it like this
        // so here we see both executed concurrently
        Observable.merge(getDataSync(1).subscribeOn(Schedulers.io()), getDataSync(2).subscribeOn(Schedulers.io())).blockingForEach(System.out::println);
    }

    private static void flatMapExampleAsync() {
        Observable.range(0, 5).flatMap(i -> {
            return getDataAsync(i);
        }).blockingForEach(System.out::println);
    }

    private static void flatMapExampleSync() {
        Observable.range(0, 5).flatMap(i -> {
            return getDataSync(i);
        }).blockingForEach(System.out::println);
    }

    private static void flatMapBufferedExampleAsync() {
        Observable.range(0, 5000).buffer(500).flatMap(i -> {
            return Observable.fromIterable(i).subscribeOn(Schedulers.computation()).map(item -> {
                // simulate computational work
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                    return item + " processed " + Thread.currentThread();
                });
        }).blockingForEach(System.out::println);
    }

    private static void flatMapWindowedExampleAsync() {
        Observable.range(0, 5000).window(500).flatMap(work -> {
            return work.observeOn(Schedulers.computation()).map(item -> {
                // simulate computational work
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                    return item + " processed " + Thread.currentThread();
                });
        }).blockingForEach(System.out::println);
    }

    // artificial representations of IO work
    static Observable<Integer> getDataAsync(int i) {
        return getDataSync(i).subscribeOn(Schedulers.io());
    }

    static Observable<Integer> getDataSync(int i) {
    	
    	return Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				emitter.onNext(i);
				emitter.onComplete();
				
			}
		});
    }
}
