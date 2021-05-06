'''
#!/usr/bin/env python

#from __future__ import unicode_literals
#from __future__ import print_function
import warnings
warnings.filterwarnings("ignore")
import logging
import time
timestr = time.strftime("%Y%m%d-%H%M%S")
print("Program started text_processing_lib.py %s"%(timestr))
logpath = r"/home/hqej/Desktop/Fragments_Retrieval_2to3/Fragments_Retrieval_2to3/logs/"
logname = logpath+'ocr-log_'+timestr+'.log'
logging.basicConfig(filename=logname,
                            filemode='w',
                            format='%(asctime)s %(lineno)d %(name)s %(funcName)s %(levelname)s %(message)s',level=logging.INFO)

import numpy as np
import re
from collections import defaultdict
from fuzzywuzzy import fuzz
from nltk.corpus import stopwords
from stop_words import get_stop_words
from nltk.stem.porter import PorterStemmer

## Arun: These may be provided by Austin
## Bigram: Some English words occur together more frequently. For example - Sky High, do or die, best performance, heavy rain etc. 
## So, in a text document we may need to identify such pair of words which will help in sentiment analysis.

AUSTIN_BIGRAMS = (
    "forc majeur", "majeur event", "drill unit",
    "mechan failur", "work stoppag", "loop current",
    "failur damag", "stoppag servic", "storm packer",
    "receiv compen", "majeur rate", "stoppag due",
    "damag drill", "dgd equip", "event forc", "repair mainten",
    "repair drill", "stoppag continu", "servic stoppag",
    "name storm", "equip downtim", "storm loop", "stoppag bank",
    "result disrupt", "delay failur", "tropic storm",
    "hurrican loop", "downtim forc",
)

## STOPWORDS: Stopwords are the English words which does not add much meaning to a sentence.
## They can safely be ignored without sacrificing the meaning of the sentence.
## For example, the words like the, he, have etc. Such words are already captured this in corpus named corpus. 

## TWO CATEGORIES OR CLASSES 1) FORCE MAJEUR 2) RIG REPAIR 3) NONE

RIG_REPAIR_STOPWORDS = (
    'stoppag', 'recommenc', 'expir', 'stop', 'downtim',
    'zero', 'return', 'damage', 'oblige', 'repair',
)

FORCE_MAJEURE_STOPWORDS = (
    'event', 'majeur', 'forc', 'loop', 'weather', 'sea', 'storm', 'hurrican',
)

## RESPONSIBILITY KEYWORDS may be related to some RESPONSIBILITY

RESPONSIBILITY_KEYWORDS = (
    'addit compens', 'compani termin', 'compens compani', 'compens period',
    'document cost', 'handl charg', 'pay money', 'payment oblig',
    'shall advis', 'shall paid', 'shall result', "contractor shall liabil",
    "conduct repair", "includ payment", "demobil fee", "contractor shall receiv",
    "shall entitl compens", "shall entitl portion compens", "contractor paid",
    "paid oper rate", "shall receiv compens", "shall payment", "oper rate shall reduc",
    "oper rate would reduc", "shall remain oblig", "shall reliev oblig",
    "adjust compens payabl", "shall contractor entitl", "rate reduc",
    "remain rate"
)
##############################PORTER STEMMING ALGORITHM################################
## In the areas of Natural Language Processing we come across situation where two or more words have a common root.
## For example, the three words - agreed, agreeing and agreeable have the same root word agree.
## A search involving any of these words should treat them as the same word which is the root word.
## So, it becomes essential to link all the words into their root word. 
## The NLTK library has methods to do this linking and give the output showing the root word.
#There are three most used stemming algorithms available in nltk. They give slightly different result. The below example shows the use of all the three stemming algorithms and their result.
porter = PorterStemmer()
stop_words = get_stop_words('english')

###***PorterStemmer****

###Actual: Aging  || Stem: age
###Actual: head  || Stem: head
###Actual: of  || Stem: of
###Actual: famous  || Stem: famou
###Actual: crime  || Stem: crime
###Actual: family  || Stem: famili
###Actual: decides  || Stem: decid
###Actual: to  || Stem: to
###Actual: transfer  || Stem: transfer
###Actual: his  || Stem: hi
###Actual: position  || Stem: posit
###Actual: to  || Stem: to
###Actual: one  || Stem: one
###Actual: of  || Stem: of
###Actual: his  || Stem: hi
###Actual: subalterns  || Stem: subaltern

#clean_text
#Author:
#Functionality: splits data, strips spaces and converts to lower case

def clean_text(text):
    # Split into words and convert to lower case (remove numbers).
    words = re.split(r'\W+', str(text).lower().strip())
    # Filter out stop words.
    # Stemming of words.
    words = [porter.stem(word)
             for word in words
             if word.isalpha() and not word in stop_words]
    return " ".join(words)
	

########################### N-GRAM ############################### 
## n-gram is a contiguous sequence of n items from a given sample of text or speech. 
## The items can be phonemes, syllables, letters, words or base pairs according to the application. 
## The n-grams typically are collected from a text or speech corpus. 
## When the items are words, n-grams may also be called shingles

def get_n_grams(text, N=2, use_freq_thresholds=False, do_cleaning=False):
    words = clean_text(text) if do_cleaning else text.split()
    n_grams = {n : defaultdict(int) for n in range(1, N+1)}

    for n in range(1, N+1):
        for index in range(len(words)):
            if index + N <= len(words):
                term = " ".join(words[index:index+n])
                n_grams[n][term] += 1
    if not use_freq_thresholds:
        return n_grams
    # Minimum number of occurrences for term to be in n-grams list.
    thresholds = {1: 10, 2: 3}
    n_grams_filtered = {n : dict() for n in range(1, N+1)}
    for n in range(1, N+1):
        for term, freq in list(n_grams[n].items()):
            if freq >= thresholds[n]:
                n_grams_filtered[n][term] = freq
    return n_grams_filtered

def generate_hc_features(texts):
    X = []
    for text in texts:
        text = clean_text(text)
        text_features = []
        ngrams = get_n_grams(text)
        # RIG_REPAIR_STOPWORDS frequencies.
        for unigram in RIG_REPAIR_STOPWORDS:
             text_features.append(ngrams[1][unigram])
        # FORCE_MAJEURE_STOPWORDS frequencies.
        for unigram in FORCE_MAJEURE_STOPWORDS:
             text_features.append(ngrams[1][unigram])
        # Bigrams frequencies.
        for bigram in AUSTIN_BIGRAMS:
             text_features.append(ngrams[2][bigram])
        # Number of words.
        text_features.append(len(text.split()))
        # Occured uni- grams counts.
        text_features.append(sum([ngrams[1][unigram]
                             for unigram in RIG_REPAIR_STOPWORDS]))
        text_features.append(sum([ngrams[1][unigram]
                             for unigram in FORCE_MAJEURE_STOPWORDS]))
        X.append(text_features)
    return np.array(X).astype(np.float64)

def get_npt_responsibility(text):
    text_ = clean_text(text)
    resp_presented = sum([int(term in text_)
                          for term in RESPONSIBILITY_KEYWORDS]) > 0
    return resp_presented
'''