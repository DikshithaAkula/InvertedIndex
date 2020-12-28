package uneethak_project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

//https://stackoverflow.com/questions/1625234/how-to-append-text-to-an-existing-file-in-java
//https://github.com/jiepujiang/cs646_tutorials
//https://github.com/jiepujiang/LuceneExamples/blob/master/README.md
//https://stackoverflow.com/questions/5689269/printwriter-failing-to-print-to-file
public class uneethak_project {
	static ArrayList<String> dictionary = new ArrayList<String>();
	static ArrayList<LinkedList<Integer>> postinglist = new ArrayList<>();
		
	public static void generateInvertedIndex(String path_to_file) {
		System.out.println("Entering inverted index");
		System.out.println("The path is::"+path_to_file);
		try {
			Directory directory = FSDirectory.open(Paths.get(path_to_file));
			System.out.println("Will reade index next");
			IndexReader reader = DirectoryReader.open(directory);
			//System.out.println(reader.hasDeletions()); //has no deletions
			ArrayList<String> fields = new ArrayList<String>();
			System.out.println("Document size::"+reader.maxDoc());
			for (int i=0; i<reader.maxDoc(); i++) {
				
				//System.out.println("Document number " + new Integer(i).toString());
			    Document doc = reader.document(i);
			    List<IndexableField> flds =  doc.getFields();
			    String t1 = flds.get(0).name();
			    if (!fields.contains(t1)) {fields.add(t1);}
			    String t2 = flds.get(1).name();
			    if (!fields.contains(t2)) {fields.add(t2);}
			}
			fields.remove(0); //removing id
			//System.out.println(fields); //prints all field names
			
			for(int i=0;i<fields.size();i++) {
				Terms terms = MultiFields.getTerms(reader,fields.get(i));//for fields of type text_xx
				TermsEnum termsEnum = terms.iterator();
				int c=0;
				while (termsEnum.next() != null) {
				    String t = termsEnum.term().utf8ToString();
				    //if(!dictionary.contains(t)) {
				    	dictionary.add(t);
				    	//System.out.println(t);
				    	c+=1;
				    	//}
				    //System.out.println(new Integer(i).toString() + flds.get(1).name().toString() + termsEnum.term().utf8ToString());	
				    }
				//System.out.println(fields.get(i) +" : " + new Integer(c).toString()); //Stats input data
				
			}
			//System.out.println("Total  : " + new Integer(dictionary.size()).toString()); //Stats input data
			
			for(int i=0;i<dictionary.size();i++) {	
				String trm = dictionary.get(i);
				LinkedList<Integer> postings = new LinkedList<Integer>();
				for(int j=0;j<fields.size();j++) {
					PostingsEnum post = MultiFields.getTermDocsEnum(reader, fields.get(j), new BytesRef(dictionary.get(i)));
					if(post!=null) {
						int docid;
						while((docid = post.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
							//System.out.println(docid);
							postings.add(docid);
						}
					}
				}
				postinglist.add(postings);
				/*to print the entire PostingsList*/
				//System.out.println(trm + " -> " + postings.toString()); 
				/*testing output*/
				/*
				if ("ateint".equals(trm.trim())) {
					System.out.println(trm + " -> " + postings.toString());
				}
				if ("feme".equals(trm.trim())) {
					System.out.println(trm + " -> " + postings.toString());
				}
				*/
			}
		} catch (IOException e) {
			System.out.println("Exception occured");
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public static ArrayList<LinkedList<Integer>> getPostingsLists(String[] terms,PrintWriter out) {
		ArrayList<LinkedList<Integer>> plist = new ArrayList<>();
		//for(int i=0;i<terms.length;i++)
			//{System.out.println(terms[i]);}
		for(int i=0;i<terms.length;i++) {
			out.println("GetPostings");
			out.println(terms[i]);
			System.out.println("Dictionary size::"+dictionary.size());
			int termid = dictionary.indexOf(terms[i]);
			//System.out.println(termid);
			LinkedList<Integer> postings = postinglist.get(termid);
			plist.add(postings);
			String tmplist = "";
			for(int j=0;j<postings.size();j++) {
				String t = postings.get(j).toString();
				tmplist = tmplist.concat(" " + t);
			}
			out.println("Postings list:" + tmplist);
			//System.out.println(terms[i] + " -> " + postings.toString());
		}
		return plist;
	}
	
	public static void taat_and(ArrayList<LinkedList<Integer>> postings,PrintWriter out) {
		LinkedList<Integer> t1 = postings.get(0);
		LinkedList<Integer> f_list = new LinkedList<Integer>();
		int comparisons = 0;
		//for(int i=0;i<postings.size();i++) {System.out.println(postings.get(i).size());}
		for(int i=1;i<postings.size();i++) {
			//System.out.println(t1);
			LinkedList<Integer> t1_skip = new LinkedList<Integer>();
			int skip_1 = (int) Math.sqrt(t1.size());
			for(int k=0;k<t1.size();k=k+skip_1) {
				t1_skip.add(t1.get(k));
			}
			int t1_p=0;
			LinkedList<Integer> t2 = postings.get(i);
			LinkedList<Integer> t2_skip = new LinkedList<Integer>();
			int skip_2 = (int) Math.sqrt(t2.size());
			for(int k=0;k<t2.size();k=k+skip_2) {
				t2_skip.add(t2.get(k));
			}
			int t2_p=0;
			//int p1=0,p2=0; //pointers
			Iterator<Integer> i1 = t1.iterator();
			Iterator<Integer> i2 = t2.iterator();
			Integer v1 = (Integer)i1.next();
			Integer v2 = (Integer)i2.next();
			//while(p1<t1.size() && p2<t2.size()) {
			while(true) {
				//int v1 = t1.get(p1);
				//int v2 = t2.get(p2);
				//System.out.println(v1+ " " +v2);
				comparisons+=1;
				int tmp_1=0;
				int tmp_2=0;
				//System.out.println(v1+" "+v2+" "+comparisons); //The listing of comparisons and count
				if (v1.intValue()==v2.intValue()) {
					f_list.add(v1.intValue());
					if (i1.hasNext() && i2.hasNext()) {
						v1 = (Integer)i1.next();//p1++;
						v2 = (Integer)i2.next();//p2++;
					}
					else {break;}
				}
				else if(v1.intValue()<v2.intValue()){
					if(t1_p<t1_skip.size()) {
						if(v1.intValue()==t1_skip.get(t1_p)) {
							if(t1_p+1 <t1_skip.size()) {
								if(t1_skip.get(t1_p+ 1)<=v2.intValue()) {
									for(int l=0;l<skip_1-1;l++) {i1.next();}
									v1 = (Integer)i1.next();
									tmp_1=1;
									//t1_p=t1_p+1;
								}
							}
							t1_p = t1_p + 1;
						}
					}
					if(tmp_1==0) {
					if(i1.hasNext()) {v1 = (Integer)i1.next();}//p1++;}
					else {break;}
					}
				}
				
				else {
					if(t2_p<t2_skip.size()) {
						if(v2.intValue()==t2_skip.get(t2_p)) {
							if((t2_p+1) <t2_skip.size()) {
								//System.out.println(t2_skip.get(t2_p+1));
								if(t2_skip.get(t2_p+1)<=v1.intValue()) {
									//System.out.println(skip_2-1);
									for(int l=0;l<skip_2-1;l++) {i2.next();}
									v2 = (Integer)i2.next();
									tmp_2=1;
									//t2_p=t2_p+1;
								}
							}
							t2_p = t2_p + 1;
						}
					}
					if(tmp_2==0) {
					if(i2.hasNext()) {v2 = (Integer)i2.next();}//p2++;}
					else {break;}
					}
				}
			}
			t1 = new LinkedList(f_list);
			//System.out.println(t1);
			f_list.clear();
		}		
		//just formating
		String tmplist = "";
		for(int j=0;j<t1.size();j++) {
			String t = t1.get(j).toString();
			tmplist = tmplist.concat(" " + t);
		}
		if(t1.size()==0) {
			out.println("Results: empty");
			out.println("Number of documents in results: 0");
		}
		else {
			out.println("Results:" + tmplist);
			out.println("Number of documents in results: " + new Integer(t1.size()).toString());
		}
		out.println("Number of comparisons: " + new Integer(comparisons));
	}

	public static void taat_or(ArrayList<LinkedList<Integer>> postings,PrintWriter out) {
		LinkedList<Integer> t1 = postings.get(0);
		LinkedList<Integer> f_list = new LinkedList<Integer>();
		int comparisons = 0;
		for(int i=1;i<postings.size();i++) {
			LinkedList<Integer> t2 = postings.get(i);
			Iterator<Integer> i1 = t1.iterator();
			Iterator<Integer> i2 = t2.iterator();
			Integer v1 = (Integer)i1.next();
			Integer v2 = (Integer)i2.next();
			//int p1=0,p2=0; //pointers
			//while(p1<t1.size() && p2<t2.size()) {
			while(true) {
				//int v1 = t1.get(p1);
				//int v2 = t2.get(p2);
				//System.out.println(v1+" "+v2);
				comparisons+=1;
				//System.out.println(v1+" "+v2+" "+comparisons); //The listing of comparisons and count
				if (v1.intValue()==v2.intValue()) {
					//p1++;
					//p2++;
					f_list.add(v1.intValue());
					if (i1.hasNext() && i2.hasNext()) {
						v1 = (Integer)i1.next();//p1++;
						v2 = (Integer)i2.next();//p2++;
					}
					else {break;}
				}
				else if(v1.intValue()<v2.intValue()){
					f_list.add(v1.intValue());
					if(i1.hasNext()) {v1 = (Integer)i1.next();}//p1++;}
					else {f_list.add(v2.intValue());break;}
				}	
				else {
					f_list.add(v2.intValue());
					//p2++;
					if(i2.hasNext()) {v2 = (Integer)i2.next();}//p1++;}
					else {f_list.add(v1.intValue());break;}
				}
			}
			
			//while(p1<t1.size()) {f_list.add(t1.get(p1));p1++;}
			//while(p2<t2.size()) {f_list.add(t2.get(p2));p2++;}
			while(i1.hasNext()) {f_list.add(i1.next());}
			while(i2.hasNext()) {f_list.add(i2.next());}			
			t1 = new LinkedList(f_list);
			f_list.clear();
		}		
		Collections.sort(t1);
		/*
		System.out.println(t1.size());
		ArrayList<Integer> tmp = new ArrayList<>(); 
		for(int i=0;i<postings.size();i++) {
			LinkedList<Integer> t = postings.get(i);
			for(int j=0;j<t.size();j++) {tmp.add(t.get(j));}
		}
		Set<Integer> primesWithoutDuplicates = new LinkedHashSet<Integer>(tmp);
		System.out.print("Size: ");
		System.out.println(primesWithoutDuplicates.size());
		Integer[] nodup = primesWithoutDuplicates.toArray(new Integer[primesWithoutDuplicates.size()]);
		for(int i=0;i<primesWithoutDuplicates.size();i++) {
			if(!t1.contains(nodup[i])) {System.out.println(nodup[i]);}
		}
		*/
		//just formating
		String tmplist = "";
		for(int j=0;j<t1.size();j++) {
			String t = t1.get(j).toString();
			tmplist = tmplist.concat(" " + t);
		}
		if(t1.size()==0) {
			out.println("Results: empty");
			out.println("Number of documents in results: 0");
		}
		else {
			out.println("Results:" + tmplist);
			out.println("Number of documents in results: " + new Integer(t1.size()).toString());
		}
		out.println("Number of comparisons: " + new Integer(comparisons));		
	}

	public static void daat_and(ArrayList<LinkedList<Integer>> postings,PrintWriter out) {
		LinkedList<Integer> f_list = new LinkedList<Integer>();
		int comparisons = 0; int n = postings.size();
		//int[] ptr = new int[n];
		ArrayList<Iterator<Integer>> it = new ArrayList<>();
		for(int i=0;i<n;i++) {it.add(postings.get(i).iterator());}//ptr[i]=0;} 
		//int flag = 0;
		//for(int i=0;i<n;i++) {if(ptr[i]==postings.get(i).size()-1){flag=1;break;}}
		ArrayList<Integer> v = new ArrayList<>();
		for(int i=0;i<n;i++) {v.add(it.get(i).next());}
		//while(flag==0) {
		
		//Preparing skipped pointer linkedlist and skip size
		ArrayList<LinkedList<Integer>> skip_l = new ArrayList<>();
		ArrayList<Integer> skip_size = new ArrayList<>(); 
		int p[] = new int[n];
		for(int i=0;i<n;i++) {
			LinkedList<Integer> f = new LinkedList<>();
			LinkedList<Integer> t = postings.get(i);			
			int size = (int) Math.sqrt(t.size());
			skip_size.add(size);
			for(int j=0;j<t.size();j+=size) {f.add(t.get(j));}
			skip_l.add(f);
			p[i]=0;
		}
		System.out.println(skip_l);
		boolean x = true;
		while(x) {
			System.out.println(v);
			comparisons=comparisons+n-1;	
			int min=13000;
			int max=-1;
			for(int i=0;i<n;i++) {
				//int t = postings.get(i).get(ptr[i]);
				int t = v.get(i).intValue();
				if(t<min) {min=t;}
				if(t>max) {max=t;}
				}
			int c=0;
			for(int i=0;i<n;i++) {
				//if(postings.get(i).get(ptr[i])==min) {ptr[i]++;c++;}
				if(v.get(i).intValue()==min) {c++;}
			}
			if(c==n) {
				f_list.add(min);
		
				int flg=0;
				for(int i=0;i<n;i++) {
					if(v.get(i).intValue()==min) {
						if(it.get(i).hasNext()) {continue;}
						else {flg=1;break;}
					}
				}
				if(flg==1) {break;}
				else {
					for(int i=0;i<n;i++) {
						if(v.get(i).intValue()==min) {
						v.set(i,it.get(i).next());}
					}
				}
			}
			else {
				for(int i=0;i<n;i++) {
					int fl=0;
					if(p[i]<skip_l.get(i).size()) {
						if(v.get(i) == skip_l.get(i).get(p[i])) {
							if(p[i]+1<skip_l.get(i).size()) {
								if(skip_l.get(i).get(p[i]+1)<=max) {
									fl=1;
									//System.out.println(skip_l.get(i).get(p[i]+1));
									//p[i]=p[i]+1;
									for(int l=0;l<skip_size.get(i)-1;l++) {
										it.get(i).next();
									}
									v.set(i,it.get(i).next());
								}
							}
							p[i]=p[i]+1;
						}
					}
					if(fl==0) {
						if(v.get(i)!=max) { 
							if(it.get(i).hasNext()) {
								v.set(i,it.get(i).next());
								}
							else {x=false;break;}
						}
					}
				}
				
			}
		}
		
		//just formatting
		String tmplist = "";
		for(int j=0;j<f_list.size();j++) {
			String t = f_list.get(j).toString();
			tmplist = tmplist.concat(" " + t);
		}
		if(f_list.size()==0) {
			out.println("Results: empty");
			out.println("Number of documents in results: 0");
		}
		else {
			out.println("Results:" + tmplist);
			out.println("Number of documents in results: " + new Integer(f_list.size()).toString());
		}
		out.println("Number of comparisons: " + new Integer(comparisons));
	}

	public static void daat_or(ArrayList<LinkedList<Integer>> postings,PrintWriter out) {
		LinkedList<Integer> f_list = new LinkedList<Integer>();
		int comparisons = 0; int n = postings.size();

		ArrayList<Iterator<Integer>> it = new ArrayList<>();
		for(int i=0;i<n;i++) {it.add(postings.get(i).iterator());}//ptr[i]=0;} 
		ArrayList<Integer> v = new ArrayList<>();
		for(int i=0;i<n;i++) {v.add(it.get(i).next());}

		while(it.size()>0) {
			//System.out.println(v);
			n=it.size();
			comparisons=comparisons+n-1;	
			int min=13000;
			for(int i=0;i<n;i++) {
				//int t = postings.get(i).get(ptr[i]);
				int t = v.get(i).intValue();
				if(t<min) {min=t;}
				}
			f_list.add(min);

			//int flg=0;
			ArrayList<Integer> rm = new ArrayList<>();
			for(int i=0;i<n;i++) {
				if(v.get(i).intValue()==min) {
					if(!it.get(i).hasNext()) {rm.add(i);}
					}
				}

			for(int i=rm.size()-1;i>=0;i--) {
				it.remove(rm.get(i).intValue());
				v.remove(rm.get(i).intValue());
			}
			n=it.size();
			for(int i=0;i<n;i++) {
				if(v.get(i).intValue()==min) {
				v.set(i,it.get(i).next());}
			}
		}
		
		//just formatting
		Collections.sort(f_list);
		/*
		System.out.println(f_list.size());
		ArrayList<Integer> tmp = new ArrayList<>(); 
		for(int i=0;i<postings.size();i++) {
			LinkedList<Integer> t = postings.get(i);
			for(int j=0;j<t.size();j++) {tmp.add(t.get(j));}
		}
		Set<Integer> primesWithoutDuplicates = new LinkedHashSet<Integer>(tmp);
		System.out.print("Size: ");
		System.out.println(primesWithoutDuplicates.size());
		Integer[] nodup = primesWithoutDuplicates.toArray(new Integer[primesWithoutDuplicates.size()]);
		for(int i=0;i<primesWithoutDuplicates.size();i++) {
			if(!f_list.contains(nodup[i])) {System.out.println(nodup[i]);}
		}
		 */
		
		String tmplist = "";
		for(int j=0;j<f_list.size();j++) {
			String t = f_list.get(j).toString();
			tmplist = tmplist.concat(" " + t);
		}
		if(f_list.size()==0) {
			out.println("Results: empty");
			out.println("Number of documents in results: 0");
		}
		else {
			out.println("Results:" + tmplist);
			out.println("Number of documents in results: " + new Integer(f_list.size()).toString());
		}
		out.println("Number of comparisons: " + new Integer(comparisons));
	}
	
	
	public static void main(String[] args) {
	System.out.println("Entering main");
	generateInvertedIndex(args[0]);
	System.out.println("After inverted index");
	//for(int i=0;i<dictionary.size();i++) {System.out.println(dictionary.get(i));}
	//System.out.print(dictionary.size());
	try {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(args[1], true)));
		File file = new File(args[2]);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line;
			try {
				while((line = br.readLine()) != null){
				    //process the line
					String query = line;
					//String query = "ateint feme";
					String[] query_terms = query.split(" ");
					ArrayList<LinkedList<Integer>> plist = getPostingsLists(query_terms,out);

					//term list horizontal
					String tmplist = "";
					for(int j=0;j<query_terms.length;j++) {
						String t = query_terms[j].toString();
						tmplist = tmplist.concat(" " + t);
						}
					//TaatAnd 
					out.println("TaatAnd");
					out.println(tmplist.trim());
					taat_and(plist,out);
					//System.out.println("TaatAnd");
					
					//TaatOr 
					out.println("TaatOr");
					out.println(tmplist.trim());
					taat_or(plist,out);
					//System.out.println("TaatOr");

					//DaatAnd 
					out.println("DaatAnd");
					out.println(tmplist.trim());
					daat_and(plist,out);
					//System.out.println("DaatAnd");

					//DaatOr 
					out.println("DaatOr");
					out.println(tmplist.trim());
					daat_or(plist,out);
					//System.out.println("DaatOr");
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e + "1");
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}	
		out.close();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	}
}
