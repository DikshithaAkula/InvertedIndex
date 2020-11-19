# -*- coding: utf-8 -*-
"""
Created on Sat Oct 12 18:06:15 2019

@author: akula
"""
import sys
import operator
temp1=0
doc_count=0
temp2=0
temp3=0

def get_postinglist(test):
    global temp1
    for i in test:        
        k=[]
        k=posting_dict[i]
        if temp1==0:
            print('GetPostings')
            temp1=1
        else:
            print('\nGetPostings')
        print(i)
        print("Postings list:",end="")
        for l in k:
            print("",l,end="")
    print("")
            
   
def Compare_PostingList_AND(list1, list2):
    result = list(set(list1) & set(list2))
    result.sort()
    temp6=[]
    temp_temp=[]
    for l in list1:
        temp6.append(l)
    for m in list2:
        temp6.append(m)
    for i in temp6:
        if (temp6.count(i)==2):
            temp_temp.append(i)        
    i=j=0
    comparisions=0
    while(i<len(list2) and j<len(list1)):
        if list1[j] > list2[i]:
            i+=1
            comparisions+=1
        elif(list1[j]==list2[i]):
            i+=1
            j+=1
            comparisions+=1
        else: 
            j+=1
            comparisions+=1
    return comparisions,result

def Compare_PostingList_OR(list1, list2):
    result = list(set(list1) | set(list2))
    result.sort()
    temp5=[]
    for l in list1:
        if l not in temp5:
            temp5.append(l)
    for m in list2:
        if m not in temp5:
            temp5.append(m)
    temp5.sort()
    i=j=0
    comparisions=0
    while(i<len(list2) and j<len(list1)):
        if list1[j] > list2[i]:
            i+=1
            comparisions+=1
        elif(list1[j]==list2[i]):
            i+=1
            j+=1
            comparisions+=1
        else: 
            j+=1
            comparisions+=1
    return comparisions,result

def DAAT_Boolean_AND(query_list):
    print('DaatAnd')
    for i in range(0,len(query_list)):
        if(i!=len(query_list)-1):
            print(query_list[i],"",end="")
        else:
            print(query_list[i])
    if len(query_list) == 0:
        return None
    if len(query_list) == 1:
        result = posting_dict[query_list[0]]        
    else:
        result=[]
        number_of_comparisions=0
        for i in range(1, len(query_list)):
            if (len(result) == 0):
                comparisions,result = Compare_PostingList_AND(posting_dict[query_list[0]], posting_dict[query_list[i]])
            else:
                comparisions,result = Compare_PostingList_AND(result, posting_dict[query_list[i]])
            number_of_comparisions+=comparisions
        if(len(result)==0):
            print('Results: empty')
        else:
            print('Results: ',end="")
            for i in range(0,len(result)):
                if(i!=len(result)-1):
                    print(result[i],"",end="")
                else:
                    print(result[i])
        print('Number of documents in results:',len(result))
        print('Number of comparisons:',number_of_comparisions)
        tfidf_AND(result,query_list)

def DAAT_Boolean_OR(query_list): 
    print('DaatOr')
    for i in range(0,len(query_list)):
        if(i!=len(query_list)-1):
            print(query_list[i],"",end="")
        else:
            print(query_list[i])            
    if len(query_list) == 0:
        return None
    if len(query_list) == 1:
        result = posting_dict[query_list[0]]
        
    else:
        result = []
        number_of_comparisions=0
        for i in range(1, len(query_list)):
            if (len(result) == 0):
                comparisions,result = Compare_PostingList_OR(posting_dict[query_list[0]], posting_dict[query_list[i]])
            else:
                comparisions,result = Compare_PostingList_OR(result, posting_dict[query_list[i]])
            number_of_comparisions+=comparisions
        if(len(result)==0):
            print('Results: empty')
        else:
            print('Results: ',end="")
            for i in range(0,len(result)):
                if(i!=len(result)-1):
                    print(result[i],"",end="")
                else:
                    print(result[i])
        print('Number of documents in results:',len(result))
        print('Number of comparisons:',number_of_comparisions)
        tfidf_OR(result,query_list)
        
def tfidf_AND(docs,terms):
    print('TF-IDF')
    if(len(docs)==0):
        print('Results: empty')
    else:
        tfidf_sorted_dictionary={}
        for i in docs:
            count=0
            for j in terms:
                count+=tf_dict[i+'_'+j]
            tfidf_words=len(pre_dictionary[i])
            tfidf_sorted_dictionary.update({i:((count/tfidf_words)*(doc_count/len(docs)))})
        sorted_dictionary=sorted(tfidf_sorted_dictionary.items(), key=operator.itemgetter(1))
        tf_idf_sorted_list=[]
        for i in sorted_dictionary:
            tf_idf_sorted_list.append(i[0])
        tf_idf_sorted_list.reverse()
        print('Results: ',end="")
        for k in range(0,len(tf_idf_sorted_list)):
            if(k!=len(tf_idf_sorted_list)-1):
                print(tf_idf_sorted_list[k],"",end="")
            else:
                print(tf_idf_sorted_list[k])

def tfidf_OR(docs,terms):
    print('TF-IDF')
    global temp3
    temp3+=1
    if(len(docs)==0):
        print('Results: empty')
    else:
        tfidf_sorted_dictionary={}
        for i in docs:
            k=0
            for j in terms:
                if j in pre_dictionary[i]: 
                    count=0
                    x=0
                    count+=tf_dict[i+'_'+j]
                    tfidf_words=len(pre_dictionary[i])
                    x=doc_count/len(posting_dict[j])
                    k+=((count/tfidf_words)*x)
            tfidf_sorted_dictionary.update({i:k})
        sorted_dictionary=sorted(tfidf_sorted_dictionary.items(), key=operator.itemgetter(1))
        tf_idf_sorted_list=[]
        for i in sorted_dictionary:
            tf_idf_sorted_list.append(i[0])
        tf_idf_sorted_list.reverse()
        print('Results: ',end="")
        for k in range(0,len(tf_idf_sorted_list)):
            if(k!=len(tf_idf_sorted_list)-1):
                print(tf_idf_sorted_list[k],"",end="")
            else:
                if(temp2!=temp3):
                    print(tf_idf_sorted_list[k])
                else:
                    print(tf_idf_sorted_list[k],end="")
    
def boolean_queries(test):    
    get_postinglist(test)
    DAAT_Boolean_AND(test)
    DAAT_Boolean_OR(test)
    
       

def generate_list(key,pre_dict):
     words_list=pre_dict[key]    
     for i in words_list:
         temp=[]
         count=0
         for k in check:
            if k==i:
              count+=1
         if count==0:
           check.append(i)
           temp.append(key)
           posting_dict.update({i:temp})
         else:
             d=posting_dict[i]
             d.append(key)
             posting_dict.update({i:d})
     for key,values in posting_dict.items():
         values.sort()

def main():
    input_doc=sys.argv[1]
    out=sys.argv[2]
    out_doc = open(out,'w')
    sys.stdout = out_doc
    with open(input_doc) as corpusopen:
        global doc_count
        for line in corpusopen:
            doc_count+=1
            split_list_all_docs=[]
            split_list_each_line=line.split()
            for i in split_list_each_line:
                count=0
                if split_list_each_line[0]!=i:
                    doc_id=split_list_each_line[0]
                    d=doc_id+'_'+i
                    count=split_list_each_line.count(i)
                    tf_dict.update({d:count})
                    split_list_all_docs.append(i)
            doc_words_lists_with_duplicates=[]
            for i in split_list_all_docs:
                doc_words_lists_with_duplicates.append(i)
            pre_dictionary.update({split_list_each_line[0]:doc_words_lists_with_duplicates})   
            doc_words_lists_no_duplicates=[] 
            for i in split_list_all_docs: 
                if i not in doc_words_lists_no_duplicates: 
                    doc_words_lists_no_duplicates.append(i)
            pre_dict.update({split_list_each_line[0]:doc_words_lists_no_duplicates})      
        for key in pre_dict:
            generate_list(key,pre_dict)
if __name__ == "__main__":
    pre_dict={}
    pre_dictionary={}
    tf_dict={}
    posting_dict={}
    add={}
    check=[]
    main()
    input_queries=sys.argv[3]
    with open(input_queries,'r') as testfile:
        temp2=sum(1 for line in open(input_queries))
        for line in testfile:
            test=[]
            query_terms=line.split()
            for i in query_terms:
                test.append(i)
            boolean_queries(test)