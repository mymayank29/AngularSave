import sys
print("Python version:")
print(sys.version)

import time
timestr = time.strftime("%Y%m%d-%H%M%S")
print("Program started at %s"%(timestr))

spark_master = sys.argv[1]
print('Spark Master: ' + spark_master)
# So the spark_master is passed as a argument through command line 

from pyspark.sql import SparkSession
from pyspark.sql import SQLContext
import os
import json
import glob
from collections import Counter
import numpy as np
from PIL import Image, ImageSequence
import pytesseract
import cv2
from pdfminer.pdfinterp import PDFResourceManager, PDFPageInterpreter
from pdfminer.pdfpage import PDFPage
from pdfminer.converter import TextConverter
from pdfminer.layout import LAParams
from pdf2image import convert_from_path 

# Building unified API - parent
spark = SparkSession.builder.master("local").appName("GOM-ICA: OCR").getOrCreate() 
num_of_executors = int(spark.sparkContext._conf.get('spark.executor.instances'))
print("[driver] num of executors: {}".format(num_of_executors))

# Building core API - child
sc = spark.sparkContext
sqlContext= SQLContext(sc)
print("Spark core context created")

# Importing the configurations from the json config
with open('config.json') as config_file:
    data = json.load(config_file)
    input_path = data['inputdata_path']
    output_path = data['outputdata_path']
    print('Input data path: %s'%(input_path))
    print('Output data path: %s'%(output_path))

# Write function to make a toogle to run code on Azure/Local/HDFS -- passed as an argument
def azure_local_hdfs:
    pass


# O drive path
def extract_files(input_path):
    logging.info("Starting extracting files...")
    global pdffiles
    global xlsfiles
    global msgfiles
    global docfiles
    global tiffiles
    # Changing path to RAW input files 
    print('Current directory to %s'%(os.getcwd()))
    logging.info('Current directory to %s'%(os.getcwd()))
    os.chdir(input_path)
    print('Going through internal files for executing OCR')
    logging.info('Going through internal files for executing OCR')
    rootDir = '.'
    SplitTypes=[]		
    for dirName, subdirList, fileList in os.walk(rootDir):
        for fname in fileList:
            SplitTypes.append(fname.split(".")[-1])
    print ([ [l, SplitTypes.count(l)] for l in set(SplitTypes)])
    logging.info([ [l, SplitTypes.count(l)] for l in set(SplitTypes)])    
    accepted_extensions = ['PDF', 'TIF', 'xls', 'msg', 'doc', 'docx', 'tif', 'pdf']
    pdffiles = []
    tiffiles = []
    xlsfiles = []
    msgfiles = []
    docfiles = []
    path = str(input_path)
    

    try:
        print('Checking for PDF files...')
        logging.info('Checking for PDF files...')
        for dirpath, subdirs, files in os.walk(path):
            pdffiles.extend(os.path.join(dirpath, x) for x in files if x.endswith(".pdf") or x.endswith(".PDF"))
    except:
        logging.error("Error: No PDF files found in folder")
        print('No PDF files')

    try:
        print('Checking TIF/tif files...')
        logging.info('Checking for TIF files...')
        for dirpath, subdirs, files in os.walk(path):
            tiffiles.extend(os.path.join(dirpath, x) for x in files if x.endswith(".tif") or x.endswith(".TIF"))
    except:
        logging.error("Error: No TIF files found in folder")
        print('No TIF files')

    try:
        print('Checking XLS/XLSX files...')
        logging.info('Checking for XLS files...')
        for dirpath, subdirs, files in os.walk(path):
            xlsfiles.extend(os.path.join(dirpath, x) for x in files if x.endswith(".xls") or x.endswith(".xlsx"))
    except:
        logging.error("Error: No XLS files found in folder")
        print('No XLS/XLSX files')

    try:
        print('Checking for MSG files...')
        logging.info('Checking for MSG files...')
        for dirpath, subdirs, files in os.walk(path):
            msgfiles.extend(os.path.join(dirpath, x) for x in files if x.endswith(".msg"))
    except:
        logging.error("Error: No MSG files found in folder")
        print('No msg files')

    try:
        print('Checking for DOC/DOCX files...')
        logging.info('Checking for DOC files...')
        for dirpath, subdirs, files in os.walk(path):
            docfiles.extend(os.path.join(dirpath, x) for x in files if x.endswith(".doc") or x.endswith(".docx"))
    except:
        logging.error("Error: No DOC/DOCX files found in folder")
        print('No DOC/DOCX files')
    
    return pdffiles, msgfiles, docfiles, tiffiles, xlsfiles
    
def msg2txt(path):
	logging.info("Starting .msg OCR...")
	import extract_msg
	x = os.path.split(path)
	new_f_name = x[-1]
	new_f_txt = new_f_name.replace('.msg', '.txt')
	print(new_f_txt)
	os.chdir(output_path)
	f = open(new_f_txt, "a+")
	msg = extract_msg.Message(path)
	msg_sender = msg.sender
	msg_date = msg.date
	msg_subj = msg.subject
	msg_message = msg.body
	f.write('Sender: {} \n'.format(msg_sender) + 'Sent On: {} \n'.format(msg_date)+'Subject: {} \n'.format(msg_subj)+'Body: {} \n'.format(msg_message))
	f.close
	logging.info("Ocrd .msg file : %s ", new_f_txt)
	
def read_tiff(path):
	global images
	global gray

	im = Image.open(path)
	x = os.path.split(path)
	new_f_name = x[-1]
	if new_f_name.endswith('.tif'):
	    new_f_txt = new_f_name.replace('.tif', '.txt')
	elif new_f_name.endswith('.TIF'):
		new_f_txt = new_f_name.replace('.TIF', '.txt')
	print(new_f_txt)
	os.chdir(output_path)
	f = open(new_f_txt, "a+")

	for i, page in enumerate(ImageSequence.Iterator(im)):
		image = np.array(page.convert('RGB'))
		gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
		ret1, th1 = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY)
		ret2, th2 = cv2.threshold(th1, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
		blur = cv2.GaussianBlur(th2, (1, 1), 0)
		ret3, th3 = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
		text = pytesseract.image_to_string(th3)
		text = pytesseract.image_to_string(gray)
		f.write(text)
		print("----------------------------------------------------")
	f.close()
	print('Saved text to file %s'%(f))


def pdf2text(path):
    x = os.path.split(path)
    new_f_name = x[-1]
    if new_f_name.endswith('.pdf'):
        new_f_txt = new_f_name.replace('.pdf', '.txt')
    elif new_f_name.endswith('.PDF'):
        new_f_txt = new_f_name.replace('.PDF', '.txt')
    print(new_f_txt)
    os.chdir(output_path)
    f = open(new_f_txt, "a")
    try:
	    rsrcmgr = PDFResourceManager()
	    retstr = io.StringIO()
	    codec = 'utf-8'
	    laparams = LAParams()
	    device = TextConverter(rsrcmgr, retstr, codec=codec, laparams=laparams)
	    interpreter = PDFPageInterpreter(rsrcmgr, device)
	    for page in PDFPage.get_pages(f):
	        interpreter.process_page(page)
	        path =  retstr.getvalue()
	    print(path)
    except:
	    print('This pdf contains images. Starting OCR....')
	    pages = convert_from_path(path, 500) 
	    image_counter = 1
	    for page in pages: 
	        filename = "page_"+str(image_counter)+".jpg"
	        page.save(filename, 'JPEG') 
	        image_counter = image_counter + 1
	    filelimit = image_counter-1
	    for i in range(1, filelimit + 1):
	        filename = "page_"+str(i)+".jpg"
	        text = str(((pytesseract.image_to_string(Image.open(filename)))))
	        text = text.replace('-\n', '')
	        f.write(text)
	    f.close()
	    del_files = glob.glob(output_path+'*')
	    for i in del_files:
	        if i.endswith(".jpg") or i.endswith(".jpeg") or i.endswith(".JPG"):
	            os.remove(i)
	    print('OCRed file: %s is available' %(new_f_txt)) 
	    print('Deleted all images to the above job')
    
def doc2txt(path):
    x = os.path.split(path)
    new_f_name = x[-1]
    if new_f_name.endswith('.pdf'):
        new_f_txt = new_f_name.replace('.doc', '.txt')
    elif new_f_name.endswith('.PDF'):
        new_f_txt = new_f_name.replace('.docx', '.txt')
    print(new_f_txt)
    doc = docx.Document(path)
    fullText = []
    for para in doc.paragraphs:
        txt = para.text.encode('ascii', 'ignore')
        fullText.append(txt)
    return '\n'.join(fullText)
 


if __name__ == "__main__":
    extract_files(input_path)
    tif_ocr = sc.parallelize(tiffiles)
    tif_ocr.foreach(read_tiff)
    pdf_ocr = sc.parallelize(pdffiles)
    pdf_ocr.foreach(pdf2text)
    msg_ocr = sc.parallelize(msgfiles)
    msg_ocr.foreach(msg2txt)
    
# to do - doc and docx 
    
    