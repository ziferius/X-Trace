package edu.brown.cs.systems.xtrace;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Performance tests of X-Trace metadata
 * @author a-jomace
 *
 */
public class TraceImplPerf extends TestCase {
  
  private static final DecimalFormat format = new DecimalFormat("#.##");
  private void printResults(double duration, double count, double cycles) {
    System.out.println("  Time:  " + format.format(duration/1000000000.0) + " seconds");
    System.out.println("  Count: " + count);
    System.out.println("  Avg:   " + format.format(duration/count) + " ns");
    System.out.println("  CPU:   " + format.format(cycles/count) + " cpu ns");
  }
  
  private static final ThreadMXBean tbean = ManagementFactory.getThreadMXBean();

  @Test
  public void testByteSet(){
    System.out.println("PARSE+SET\t TaskID");
    Trace xtrace = new Trace();
    byte[] md = TraceImplTest.randomTaskID();
    int perIteration = 10000000;
    
    long startcpu = tbean.getCurrentThreadCpuTime();
    long start = System.nanoTime();
    int count = 0;
    for (int i = 0; i < perIteration; i++) {
      xtrace.set(md);
      count++;
    }
    long duration = System.nanoTime() - start;
    long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
    printResults(duration, count, cycles);
  }

  @Test
  public void testByteSet2(){
    System.out.println("PARSE+SET\t TaskID + TenantID");
    Trace xtrace = new Trace();
    byte[] md = TraceImplTest.randomTaskIDAndTenant();
    int perIteration = 10000000;
    
    long startcpu = tbean.getCurrentThreadCpuTime();
    long start = System.nanoTime();
    int count = 0;
    for (int i = 0; i < perIteration; i++) {
      xtrace.set(md);
      count++;
    }
    long duration = System.nanoTime() - start;
    long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
    printResults(duration, count, cycles);
  }
  
  @Test
  public void testByteSet3(){
    System.out.println("PARSE+SET\t TaskID + TenantID + ParentID");
    Trace xtrace = new Trace();
    byte[] md = TraceImplTest.randomTaskIDAndTenantAndParents(1);
    int perIteration = 10000000;
    
    long startcpu = tbean.getCurrentThreadCpuTime();
    long start = System.nanoTime();
    int count = 0;
    for (int i = 0; i < perIteration; i++) {
      xtrace.set(md);
      count++;
    }
    long duration = System.nanoTime() - start;
    long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
    printResults(duration, count, cycles);
  }
  
  @Test
  public void testByteSet4(){
    System.out.println("PARSE+SET\t TaskID + TenantID + ParentID * 10");
    Trace xtrace = new Trace();
    byte[] md = TraceImplTest.randomTaskIDAndTenantAndParents(10);
    int perIteration = 10000000;
    
    long startcpu = tbean.getCurrentThreadCpuTime();
    long start = System.nanoTime();
    int count = 0;
    for (int i = 0; i < perIteration; i++) {
      xtrace.set(md);
      count++;
    }
    long duration = System.nanoTime() - start;
    long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
    printResults(duration, count, cycles);
  }
  
  @Test
  public void testGet1() throws InterruptedException{
    System.out.println("GET\t\t TaskID + TenantID + ParentID (1 thread)");
    final Trace xtrace = new Trace();
    final int perIteration = 100000000;
    int numthreads = 1;
    
    final AtomicLong totalcount = new AtomicLong();
    final AtomicLong totalduration = new AtomicLong();
    final AtomicLong totalcpu = new AtomicLong();
    
    final CountDownLatch ready = new CountDownLatch(numthreads);
    final CountDownLatch go = new CountDownLatch(1);
    
    Runnable work = new Runnable() {
      @Override
      public void run() {
        xtrace.set(TraceImplTest.randomTaskIDAndTenantAndParents(1));
        ready.countDown();
        try {
          go.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
          return;
        }
        long startcpu = tbean.getCurrentThreadCpuTime();
        long start = System.nanoTime();
        int count = 0;
        for (int i = 0; i < perIteration; i++) {
          xtrace.get();
          count++;
        }
        long duration = System.nanoTime() - start;
        long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
        totalcount.getAndAdd(count);
        totalduration.getAndAdd(duration);
        totalcpu.getAndAdd(cycles);
      }
    };
    
    Thread[] threads = new Thread[numthreads];
    for (int i = 0; i < numthreads; i++) {
      threads[i] = new Thread(work);
      threads[i].start();
    }
    ready.await();
    go.countDown();
    
    for (int i = 0; i < numthreads; i++) {
      threads[i].join();
    }
    
    printResults(totalduration.get(), totalcount.get(), totalcpu.get());
  }
  
  @Test
  public void testGet2() throws InterruptedException{
    System.out.println("GET\t\t TaskID + TenantID + ParentID (10 thread)");
    final Trace xtrace = new Trace();
    final int perIteration = 100000000;
    int numthreads = 10;
    
    final AtomicLong totalcount = new AtomicLong();
    final AtomicLong totalduration = new AtomicLong();
    final AtomicLong totalcpu = new AtomicLong();
    
    final CountDownLatch ready = new CountDownLatch(numthreads);
    final CountDownLatch go = new CountDownLatch(1);
    
    Runnable work = new Runnable() {
      @Override
      public void run() {
        xtrace.set(TraceImplTest.randomTaskIDAndTenantAndParents(1));
        ready.countDown();
        try {
          go.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
          return;
        }
        long startcpu = tbean.getCurrentThreadCpuTime();
        long start = System.nanoTime();
        int count = 0;
        for (int i = 0; i < perIteration; i++) {
          xtrace.get();
          count++;
        }
        long duration = System.nanoTime() - start;
        long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
        totalcount.getAndAdd(count);
        totalduration.getAndAdd(duration);
        totalcpu.getAndAdd(cycles);
      }
    };
    
    Thread[] threads = new Thread[numthreads];
    for (int i = 0; i < numthreads; i++) {
      threads[i] = new Thread(work);
      threads[i].start();
    }
    ready.await();
    go.countDown();
    
    for (int i = 0; i < numthreads; i++) {
      threads[i].join();
    }
    
    printResults(totalduration.get(), totalcount.get(), totalcpu.get());
  }
  
  @Test
  public void testGet3() throws InterruptedException{
    System.out.println("GET\t\t TaskID + TenantID + ParentID (100 thread)");
    final Trace xtrace = new Trace();
    final int perIteration = 100000000;
    int numthreads = 10;
    
    final AtomicLong totalcount = new AtomicLong();
    final AtomicLong totalduration = new AtomicLong();
    final AtomicLong totalcpu = new AtomicLong();
    
    final CountDownLatch ready = new CountDownLatch(numthreads);
    final CountDownLatch go = new CountDownLatch(1);
    
    Runnable work = new Runnable() {
      @Override
      public void run() {
        xtrace.set(TraceImplTest.randomTaskIDAndTenantAndParents(1));
        ready.countDown();
        try {
          go.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
          return;
        }
        long startcpu = tbean.getCurrentThreadCpuTime();
        long start = System.nanoTime();
        int count = 0;
        for (int i = 0; i < perIteration; i++) {
          xtrace.get();
          count++;
        }
        long duration = System.nanoTime() - start;
        long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
        totalcount.getAndAdd(count);
        totalduration.getAndAdd(duration);
        totalcpu.getAndAdd(cycles);
      }
    };
    
    Thread[] threads = new Thread[numthreads];
    for (int i = 0; i < numthreads; i++) {
      threads[i] = new Thread(work);
      threads[i].start();
    }
    ready.await();
    go.countDown();
    
    for (int i = 0; i < numthreads; i++) {
      threads[i].join();
    }
    
    printResults(totalduration.get(), totalcount.get(), totalcpu.get());
  }
  
  /**
   * Tests the cost of updating parent ID but reusing context object
   */
  @Test
  public void testMetadataUpdate() {
    System.out.println("NEXT NOALLOC");
    Trace xtrace = new Trace();
    int perIteration = 100000000;

    long startcpu = tbean.getCurrentThreadCpuTime();
    long start = System.nanoTime();
    int count = 0;
    for (int i = 0; i < perIteration; i++) {
      xtrace.setParentEventID(0, true);
      count++;
    }
    long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
    long duration = System.nanoTime() - start;
    printResults(duration, count, cycles);
  }

  @Test
  public void testMetadataUpdateAlloc() {
    System.out.println("NEXT ALLOC");
    Trace xtrace = new Trace();
    int perIteration = 100000000;

    long startcpu = tbean.getCurrentThreadCpuTime();
    long start = System.nanoTime();
    int count = 0;
    for (int i = 0; i < perIteration; i++) {
      xtrace.setParentEventID(0, false);
      count++;
    }
    long cycles = tbean.getCurrentThreadCpuTime() - startcpu;
    long duration = System.nanoTime() - start;
    printResults(duration, count, cycles);
  }

}