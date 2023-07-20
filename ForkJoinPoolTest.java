package javaforkjoinpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

class FJSumOfNaturalNumbers extends RecursiveTask<Integer> {
	int first;
	int last;
	FJSumOfNaturalNumbers sum1;
	FJSumOfNaturalNumbers sum2;
	
	public FJSumOfNaturalNumbers(int first, int last) {
		this.first = first;
		this.last = last;
		
	}

	@Override
	protected Integer compute() {
		if(last - first > 10) {
			sum1 = new FJSumOfNaturalNumbers(first , first + 9);
			sum2 = new FJSumOfNaturalNumbers(first + 10, last);
			int sum = 0;
			invokeAll(sum1, sum2);
			try {

				sum = sum1.get() + sum2.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return sum;
			//return (sum1.join() + sum2.join());
			
		}
		else {
			int sum3 = 0;
			for(int i = first; i <= last; sum3 += i, i++ );
			return sum3;
		}
		
	}
}

class FJTaskThatReturnsNoFutureResult extends RecursiveTask<Integer> {

	@Override
	protected Integer compute() {
		while(true);
	}
	
}

public class ForkJoinPoolTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ForkJoinPool fjPool = new ForkJoinPool();
        System.out.println("Parallelism level " + fjPool.getParallelism());
        System.out.println("active threads " + fjPool.getActiveThreadCount());
        System.out.println("is Queued " + fjPool.getQueuedTaskCount()); 
        System.out.println("is Pool Quiescent " + fjPool.isQuiescent());
        
        FJSumOfNaturalNumbers fjSum = new FJSumOfNaturalNumbers(1, 100);
        fjPool.invoke(fjSum);
        System.out.println("Sum of 100 natural number " + fjSum.get());
	fjSum.setForkJoinTaskTag((short)-1);
		System.out.println("ForkJoin Tag " + fjSum.getForkJoinTaskTag());
		
		fjSum.compareAndSetForkJoinTaskTag((short)-2, (short)3);
		System.out.println("Updated ForkJoin Tag " + fjSum.getForkJoinTaskTag());
		
		FJTaskThatReturnsNoFutureResult fjTaskThatReturnsNoFutureResult = new FJTaskThatReturnsNoFutureResult();
		//fjPool.invoke(fjTaskThatReturnsNoFutureResult);
		//System.out.println("Reached");                  //Unreacheable at this point as the task invoke returns no result.
		
		/*
		fjTaskThatReturnsNoFutureResult.completeExceptionally(new Exception("Task Completed abnormally with exception"));
		System.out.println("Reached");
		System.out.println(fjTaskThatReturnsNoFutureResult.getException());
		*/
		
		fjTaskThatReturnsNoFutureResult.complete(5);
		System.out.println("Forced result in future task " + fjTaskThatReturnsNoFutureResult.get());
		
		NonForkJoinTask nonForkJoinTask = new NonForkJoinTask();
		fjPool.execute(nonForkJoinTask);
		
		System.out.println("Pool hosting FJSumOfNaturalNumbers " + FJSumOfNaturalNumbers.getPool());
		
		System.out.println("Queued Task Count " + FJSumOfNaturalNumbers.getQueuedTaskCount());
		
			
	}

}
