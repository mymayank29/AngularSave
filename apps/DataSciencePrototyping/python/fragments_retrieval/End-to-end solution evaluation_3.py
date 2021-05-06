'''
from __future__ import unicode_literals
from __future__ import print_function
import os
import sys
import json
import logging
import time
import glob2
import pandas as pd
import subprocess

#Importing the configuration's from config.json
with open('config.json') as config_file:
    data = json.load(config_file)
    mlmodel_path = data['mlmodel_path']
    input_files = data['input_files']
    logpath = data['logs_save_path']
    hdfs_file_path = data['hdfs_file_path']
    output_filename = data['output_filename']

timestr = time.strftime("%Y%m%d-%H%M%S")
print("Program End_to_End_solution started at %s"%(timestr))
logname = logpath+'fragments_retrieval_'+timestr+'.log'
logging.basicConfig(filename=logname, filemode='w',format='%(asctime)s %(lineno)d %(name)s %(funcName)s %(levelname)s %(message)s', level=logging.INFO)


from pyspark import SparkConf
from pyspark import SparkContext
from pyspark.sql import SQLContext, SparkSession
from pyspark import SparkFiles
from collections import defaultdict, Counter
from fragments_retrieval_lib import FragmentsRetrievalModel
import utilities

conf = SparkConf()
conf.setMaster('yarn-client')
conf.setAppName('FragmentsRetrieval')
spark = SparkSession.builder.appName("FragmentsRetrieval").config(conf=conf).getOrCreate()
sc = SparkContext.getOrCreate(SparkConf().setAppName("FragmentsRetrieval"))
#sc = SparkContext(conf=conf)
sqlContext = SQLContext(sc)

sc.addPyFile("/home/hqej/Desktop/Fragments_Retrieval_2to3/Fragments_Retrieval_2to3/utilities.py")
sc.addPyFile("/home/hqej/Desktop/Fragments_Retrieval_2to3/Fragments_Retrieval_2to3/fragments_retrieval_lib.py")
sys.path.insert(0,SparkFiles.getRootDirectory())
print('----------------------------------------------------------------------------------------------------')


def remove_prefix(filename):
    logging.info("Removing prefix for each filename...")
    x = filename.split("/")
    logging.info("Splitting filename based on ../.....")
    for index, term in enumerate(x):
        if term[:2] == "CW":
            logging.info("Term: %s" %("/".join(x[index:])))
            return "/".join(x[index:])

filename = "hdfs:///data/Stage/svccdf_d/CDF_InvoiceAnalytics/ocr_res/CW26012/FMC_IMA.pdf.txt"

def get_doc_content(filename):
    logging.info("Getting document content for filename : %s "%(filename))
    content = ''

    contents = sc.textFile(filename)
    content_i = contents.collect()
    for line in content_i:
        print(line)
        content += line.strip()+ " "
    print(content)
    return " ".join([word for word in content.split() if word != ""])



#cmd = 'hdfs dfs -find {} -name *.txt'.format(source_dir)
#'hdfs dfs -find /data/Stage/svccdf_d/CDF_InvoiceAnalytics/ocr_res/CW26012/ -name *.txt'
def main():
    model = FragmentsRetrievalModel(ml_model_path=mlmodel_path)
    logging.info("Provided .sav model as input...")
    results = defaultdict(list)
    logging.info("Created results Defaultdict...")
    cnt_errors = Counter()
    
    cmd = 'hdfs dfs -find {}'.format(hdfs_file_path)
    files = subprocess.check_output(cmd, shell=True).decode('utf-8').strip().split('\n')
    for filename in files:
        try:
            text = get_doc_content(filename)
            logging.info("Getting document content for filename : %s "%(filename))
            print(remove_prefix(filename).split("/")[0])
            print('FILENAME: %s'%(filename))
            relevant_sections = model.get_relevant_sections(text=text.encode('ascii', 'ignore'))
            logging.info("Extracting relevant sections from the filename : %s "%(filename))
      
            for section, label in relevant_sections:
                logging.info("Appending Filename, Contract Number, Contract NPT Type, Text to the output file...")
                results["Filename"].append(remove_prefix(filename))
                results["Contract_no"].append(remove_prefix(filename).split("/")[0])
                results["NPT_type"].append(label)
                results["Text"].append(str(section))
        except Exception:
            print("Error!")
            cnt_errors[Exception] += 1
    results = pd.DataFrame(results)
    logging.info("Creating results dataframe...")
    output_filename = "Output_files/FragmentsExtractionResults_"+timestr+".parquet"
    results = sqlContext.createDataFrame(results)
    results.write.parquet(output_filename)
    logging.info("Saving results to a xlsx file and saving at %s : "%(output_filename))
    print('Completed')

if __name__ == '__main__':
    main()


text = get_doc_content(filename)
model = FragmentsRetrievalModel(ml_model_path=mlmodel_path)
print('--------------------------------------------------------------')
#print(content)

results = defaultdict(list)
relevant_sections = model.get_relevant_sections(text=text.encode('ascii', 'ignore'))
print(relevant_sections)


def main():    

    for filename in glob2.glob(input_files, recursive=True):
        try:
            text = get_doc_content(filename)
            logging.info("Getting document content for filename : %s "%(filename))
            print(remove_prefix(filename).split("/")[0])
            print('FILENAME: %s'%(filename))
            relevant_sections = model.get_relevant_sections(text=text.encode('ascii', 'ignore'))
            logging.info("Extracting relevant sections from the filename : %s "%(filename))
      
            for section, label in relevant_sections:
                logging.info("Appending Filename, Contract Number, Contract NPT Type, Text to the output file...")
                results["Filename"].append(remove_prefix(filename))
                results["Contract_no"].append(remove_prefix(filename).split("/")[0])
                results["NPT_type"].append(label)
                results["Text"].append(str(section))
        except Exception:
            print("Error!")
            cnt_errors[Exception] += 1

    results = pd.DataFrame(results)
    logging.info("Creating results dataframe...")
    output_filename = "Output_files/FragmentsExtractionResults_"+timestr+".parquet"
    results = sqlContext.createDataFrame(results)
    results.write.parquet(output_filename)
    logging.info("Saving results to a xlsx file and saving at %s : "%(output_filename))
    print('Completed')

if __name__ == '__main__':
    main()
'''