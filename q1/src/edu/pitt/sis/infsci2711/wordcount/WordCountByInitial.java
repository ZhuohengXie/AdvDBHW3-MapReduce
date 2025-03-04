package edu.pitt.sis.infsci2711.wordcount;

import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountByInitial extends Configured implements Tool {
   public static void main(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      int res = ToolRunner.run(new Configuration(), new WordCountByInitial(), args);
      
      System.exit(res);
   }

   @Override
   public int run(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      Job job = new Job(getConf(), "WordCount");
      job.setJarByClass(WordCountByInitial.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);

      job.setMapperClass(Map.class);
      job.setReducerClass(Reduce.class);

      job.setInputFormatClass(TextInputFormat.class);
      job.setOutputFormatClass(TextOutputFormat.class);

      FileInputFormat.addInputPath(job, new Path(args[0]));
      FileOutputFormat.setOutputPath(job, new Path(args[1]));

      job.waitForCompletion(true);
      
      return 0;
   }
   
   /**
    * This class is to mapping.
    * LongWritable
    * Text - the type that need to be analyzed.
    * Text - the key type of the map outcome.
    * IntWritable - the value type of the map outcome.
    *
    */
   public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
      private final static IntWritable ONE = new IntWritable(1);
      public static String cha ="abcdefghijklmnopqrstuvwxyz";
      private Text word = new Text();

      /**
       * This method is to map the text.
       * @param key 
       * @param value - the text needed to be analyzed.
       * @param context - the outcome of the map method.
       */
      @Override
      public void map(LongWritable key, Text value, Context context)
              throws IOException, InterruptedException {
         for (String token: value.toString().split("\\s+")) {
        	 
        	 if(token.length()>0){
        	 
        	 
            String c = token.substring(0, 1).toLowerCase();
        //	 System.out.println(token);
           word.set(c);
        	// word.set(token);
            context.write(word, ONE);
         }
      }
   }
   }
   
   /**
    * This class is to reducing.
    */
   public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
	   /**
	    * This method is to reduce by calculating values according to the same key.
	    * @param key - the key from the map outcome.
	    * @param values - key's value
	    * @param - reduce outcome.
	    */
      @Override
      public void reduce(Text key, Iterable<IntWritable> values, Context context)
              throws IOException, InterruptedException {
         int sum = 0;
         for (IntWritable val : values) {
            sum += val.get();
         }
         context.write(key, new IntWritable(sum));
      }
   }
}