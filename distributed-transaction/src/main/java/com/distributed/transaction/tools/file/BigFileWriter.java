package com.distributed.transaction.tools.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BigFileWriter {

	private static final String MODE = "rw";
	private static final int TEMP_SIZE = 1024 * 1024 * 128;
	private static final int PARTITION = 16;
	private static final int PARTITION_TEMP_SIZE = TEMP_SIZE / 8;
	private static byte[] data;

	static {
		data = new byte[PARTITION_TEMP_SIZE];
		data[0] = 1;
	}

	private static class WriterByChannel implements Runnable {

		private long startingPos;
		private FileChannel fc;

		public WriterByChannel(long startingPos, FileChannel fc) {
			this.startingPos = startingPos;
			this.fc = fc;
		}

		@Override
		public void run() {
			ByteBuffer bb = ByteBuffer.allocate(PARTITION_TEMP_SIZE);
			int length = TEMP_SIZE;
			while (length > PARTITION_TEMP_SIZE) {
				bb.put(data);
				try {
					bb.flip();
					fc.write(bb, startingPos + TEMP_SIZE - length);
					fc.force(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				bb.clear();
				length = -PARTITION_TEMP_SIZE;
			}
			bb = null;
			if (length != 0) {
				ByteBuffer left = ByteBuffer.allocate(length);
				left.put(data);
				try {
					left.flip();
					fc.write(left, TEMP_SIZE - length);
					fc.force(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				left = null;
			}
			System.out.println("One Thread Completed");
		}
	}

	private static class WriterByMappedBuffer implements Runnable {

		private long startingPos;
		private FileChannel fc;

		public WriterByMappedBuffer(long startingPos, FileChannel fc) {
			this.startingPos = startingPos;
			this.fc = fc;
		}

		@Override
		public void run() {
			MappedByteBuffer mbb = null;
			int length = TEMP_SIZE;
			while (length > PARTITION_TEMP_SIZE) {
				try {
					mbb = fc.map(MapMode.READ_WRITE, startingPos + TEMP_SIZE - length, PARTITION_TEMP_SIZE);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mbb.put(data);
				mbb.force();
				mbb.clear();
				length -= PARTITION_TEMP_SIZE;
			}
			if (length != 0) {
				try {
					mbb = fc.map(MapMode.READ_WRITE, startingPos + TEMP_SIZE - length, length);
					mbb.put(data);
					mbb.force();
					mbb.clear();
					mbb = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void writerBigFile(String filePath) throws IOException {
		RandomAccessFile file = new RandomAccessFile(new File(filePath), MODE);
		FileChannel fc = file.getChannel();

		ExecutorService threadPool = Executors.newFixedThreadPool(PARTITION);
		long length = 0;
		List<Future<Void>> result = new ArrayList<>();

		for (int count = 0; count < PARTITION; count++) {
			result.add((Future<Void>) threadPool.submit(new WriterByMappedBuffer(length, fc)));
			length += TEMP_SIZE;
		}

		for (Future<Void> one : result) {
			try {
				one.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		fc.close();
		file.close();
	}

	public static void main(String[] args) throws IOException {
		writerBigFile("e:/bigFile.txt");
		System.out.println("Complete");
	}

}
