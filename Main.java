package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main implements Runnable {
	private boolean running;
	private Thread thread;
	private ArrayList<Candle> candles;

	public static void main(String[] args) {
		new Main().start();
	}
	
	public synchronized void start() {
		if(this.running == true) {
			return;
		}
		this.thread = new Thread(this);
		this.thread.start();
		this.running = true;
	}
	
	public synchronized void stop() {
		this.running = false;
		//clean up
	}
	
	private void init() {
		System.out.println("Starting...");
		//load candles
		this.candles = new ArrayList<Candle>();
		try(BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\data.txt"))) {
			String line;
			while((line = br.readLine()) != null) {
				String[] lineData = line.split(",");
				this.candles.add(new Candle(Float.valueOf(lineData[1]), Float.valueOf(lineData[2]), Float.valueOf(lineData[3]), Float.valueOf(lineData[4])));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(this.candles.size() + " trading days loaded.");
		//
		System.out.println(this.RSI(15, 14));
	}
	
	@Override
	public void run() {
		this.init();
	}
	
	private float RSI(int i, int n) {
		if(i < n) {
			return 0;
		}
		float avrgUp = 0;
		float avrgDown = 0;
		float[] ups = new float[n];
		float[] downs = new float[n];
		for(int j = i; j > (i-n); j--) { //for example i = 13 , n = 14
			System.out.println(j + " " + (i-j));
			float delta = (this.candles.get(j).close - this.candles.get(j-1).close);
			ups[i-j] = delta > 0 ? delta : 0; 
			downs[i-j] = delta < 0 ? delta : 0; 
		}
		float k = 2.0f / (n+1.0f);
		System.out.println(k);
		for(int j = 0; j < n; j++) {
			avrgUp += k * ups[j];
		}
		for(int j = 0; j < n; j++) {
			avrgDown += k * downs[j];
		}
		return 100.0f - (100.0f / (1.0f + (avrgUp / avrgDown)));
	}

}