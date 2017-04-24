package test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Java_MI {
	static double compute_mutual_information(double[] aArray, double[] bArray) {
		if(aArray.length != bArray.length) {
			System.err.print("Size of input arrays are not equal!");
			System.exit(-1);
		}
		int length = aArray.length;
		Map<Double, ArrayList<Integer>> amap = getArrayValues(aArray);
		Map<Double, ArrayList<Integer>> bmap = getArrayValues(bArray);
		double mi = computeMI(amap, bmap, length);
		return mi;
	}
	
	public class AggregateInfo{
		public final Map<Float, Integer> value_count;
//		 public final Map<Integer, Float> index_map_value;
		public final float[] index_map_value;

		public AggregateInfo(Map<Float, Integer> value_count, float[] index_map_value) {
			this.value_count = value_count;
		    	this.index_map_value = index_map_value;
		}
	}
	
	static double compute_normalized_mutual_information(double[] aArray, double[] bArray) {
		double mi = compute_mutual_information(aArray, bArray);
		mi = mi / Math.max(Math.sqrt(compute_entropy(aArray)*compute_entropy(bArray)), 1e-10);
		return mi;
	}
	static double compute_entropy(double[] Array) {
		Map<Double, ArrayList<Integer>> map = getArrayValues(Array);
		Map<Double, Integer> amap = new HashMap<Double, Integer>();
		for (double av:map.keySet()) {
			ArrayList<Integer> indexs = map.get(av);
			amap.put(av, indexs.size()); 
		}
		if (amap.size() == 1) {
			return 1.0;
		}
		int length = Array.length;
		Set<Double> aset = map.keySet();
	    	Double[] arr = aset.toArray(new Double[aset.size()]);
		double entropy = 0;
		for(int i = 0; i < map.size(); i++) {
			double pi = (double) amap.get(arr[i]);
			double pi_sum = (double) length;
			entropy += (pi/pi_sum)*(Math.log(pi)-Math.log(pi_sum));
		}
		return Math.abs(entropy);
	}
	
	public static Map<Double,  ArrayList<Integer>> getArrayValues(double[] Array) {
		Map<Double,  ArrayList<Integer>> map = new HashMap<Double, ArrayList<Integer>>();
		for(int index = 0; index < Array.length; index++) {
			double value = Array[index];
			ArrayList<Integer> count = map.get(value);
			if(count == null) {
				ArrayList<Integer> scount = new ArrayList<Integer>();
				scount.add(index);
				map.put(value, scount);
			} else {
				count.add(index);
				map.put(value, count);
			}
		}
		return map;
	}
	
	static double computeEntropy(Map<Double, Integer> map, int num) {
		if (map.size() == 1) {
			return (float) 1.0;
		}
		Set<Float> aset = map.keySet();
	    	Float[] arr = aset.toArray(new Float[aset.size()]);
	    
	    	int sum = map.values().stream().mapToInt(Integer::intValue).sum();
	    	if (sum != samplenum) {
	    		System.out.print("Error");
	    	}
		float entropy = 0;
		float pi_sum = (float) samplenum;
		for(int i = 0; i < map.size(); i++) {
			float pi = (float) map.get(arr[i]);
			entropy += (pi/pi_sum)*(Math.log(pi)-Math.log(pi_sum));
		}
		return Math.abs(entropy);
	}
	
	public static  Map<String, Integer> getCrossMap(Map<Double,  ArrayList<Integer>> ainfo, Map<Double,  ArrayList<Integer>> tinfo, int length){
	   	Map<String, Integer> crossmap = new HashMap<String, Integer>();
	 	int all_zero = 0;
	   	for (int i=0; i<samplenum; i++) {
//			   Float av = amap.get(i);
//			   Float tv = tmap.get(i);
			float av = amap[i];
		   	float tv = tmap[i];
//		    	if (av == null) { av = (float) 0;}
//		    	if(tv == null) {tv = (float) 0;}
		   	if (av == 0 & tv == 0) {
			   	all_zero++;
			   	continue;
		   	}
			String cross = String.valueOf(av) + " " + String.valueOf(tv);
			Integer count = crossmap.get(cross);
			if(count == null) {
				count = 1;
			} else { count++;}
			crossmap.put(cross, count);
	   	} 
	   	String cross = String.valueOf(0) + " " + String.valueOf(0);
	   	Integer count = crossmap.get(cross);
	   	if(count == null) {
			count = all_zero;
	  	} else { count += all_zero;}
	   	crossmap.put(cross, count);
	   	return crossmap;
	}

	static double computeMI(Map<Double, ArrayList<Integer>> ainfo, Map<Double, ArrayList<Integer>> tinfo, int length) {
//		
		Map<Float, Integer> amap = ainfo.value_count;
		Map<Float, Integer> tmap = tinfo.value_count;

		Map<String, Integer> crossmap = getCrossMap(ainfo.index_map_value, tinfo.index_map_value, fnum);
		int alen = amap.size();
		int tlen = tmap.size();
		if (alen == tlen) {
			if(alen == 0 | alen == 1) {
				return (float) 1.0;
			}
		}
		float numinst =(float) fnum;
		float sum = 0;
		Set<Float> aset = amap.keySet();
		Float[] arr = aset.toArray(new Float[aset.size()]);
		Set<Float> tset = tmap.keySet();
		Float[] trr = tset.toArray(new Float[tset.size()]);

		for (int i = 0;i < arr.length;i++) {
			float av = arr[i];
			for (int j = 0;j < trr.length;j++) {
				float tv = trr[j];
				String cross = String.valueOf(av) + " " + String.valueOf(tv);
				Integer value = crossmap.get(cross);
				if(value != null) {
					float contingency_nm = (float) value/numinst;
		//	        		System.out.println("value:" + value + "contingency_nm:" + contingency_nm);
					float outer = (float) amap.get(av) * (float) tmap.get(tv);
					float log_outer = (float) -Math.log(outer) +  2*(float) Math.log(numinst);
					float sumtmp = contingency_nm* ((float) Math.log(value)-(float)Math.log(numinst)) + contingency_nm*log_outer;
					sum += sumtmp;
				}
			}
		}
		//	    System.out.println((float) Math.abs(sum) + "***" + computeEntropy(amap, fnum) + "***"+ computeEntropy(tmap, fnum));
		sum = (float) Math.abs(sum) / (float) Math.max(Math.sqrt(computeEntropy(amap, fnum)*computeEntropy(tmap, fnum)), 1e-10);
		//	    sum = (float) Math.abs(sum);
		return sum;
	}
}
